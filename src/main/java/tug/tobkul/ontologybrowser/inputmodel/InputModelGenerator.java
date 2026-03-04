package tug.tobkul.ontologybrowser.inputmodel;

import javafx.scene.control.Alert;
import org.apache.commons.math3.util.Combinations;
import tug.tobkul.ontologybrowser.inputmodel.model.ConstraintBuilder;
import tug.tobkul.ontologybrowser.inputmodel.model.InputModel;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Scenario;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.OperatorUtil;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputModelGenerator {
    private final InputModel inputModel;

    private final oSystem system;
    private final List<Scenario> scenarioList;
    private final String name;
    private final boolean isCli;
    private Entity rootEntity;
    private String cliError = "";

    public InputModelGenerator(String name, oSystem system, List<Scenario> scenarioList) {
        this.name = name;
        this.system = system;
        this.isCli = false;
        this.inputModel = new InputModel(system);
        this.scenarioList = scenarioList;
    }

    public InputModelGenerator(String name, oSystem system, List<Scenario> scenarioList, boolean isCli) {
        this.name = name;
        this.system = system;
        this.isCli = isCli;
        this.inputModel = new InputModel(system);
        this.scenarioList = scenarioList;
    }

    private static List<List<String>> transpose(List<List<String>> matrix) {
        if (matrix == null || matrix.isEmpty()) return Collections.emptyList();

        int rows = matrix.size();
        int cols = matrix.getFirst().size();
        List<List<String>> result = new ArrayList<>();

        for (int col = 0; col < cols; col++) {
            List<String> newRow = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                newRow.add(matrix.get(row).get(col));
            }
            result.add(newRow);
        }
        return result;
    }

    public Entity getRootEntity() {
        return rootEntity;
    }

    public String getCliError() {
        return cliError;
    }

    public String generate() {
        try {
            rootEntity = system.getRootEntity();
        } catch (IllegalFormatException e) {
            if (isCli) {
                cliError = """
                        Error: Cycle detected.
                        Involved Entities:
                        """ + e.getMessage();
            } else {
                showErrorPopup("Error", "Cycle detected", "Involved Entities:\n" + e.getMessage());
            }
        } catch (RuntimeException e) {
            if (isCli) {
                cliError = "Error: Root entity not found!";
            } else {
                showErrorPopup("Error", "Root entity not found", null);
            }
        }
        if (rootEntity == null) {
            return null;
        }
        inputModel.append(processEntity(rootEntity));
        scenarioList.forEach(scenario -> inputModel.addExpandedConstrains(scenario));
        String ret = "";
        ret += "[System]\nName: " + name + "\n\n";
        ret += inputModel.toString();
        return ret;
    }

    public String getEpsilonInt(){
        return inputModel.EPSILON_INT;
    }

    private InputModel processEntity(Entity entity) {
        InputModel entityModel = new InputModel(system);
        if (entity.getSubEntities().isEmpty()) {
            for (Attribute attribute : entity.getAttributes()) {
                String name = getEntityAttributeName(entity, attribute);
                entityModel.addInputParameter(name, attribute.getType().toString());
                entityModel.addDomain(name, attribute.getValue().getPossibleValueList());
            }
        }
        if (checkLeaf(entity)) {
            return entityModel;
        }
        Map<String, InputModel> subModels = new HashMap<>();
        // first loop of the algorithm - line 8:14
        for (Entity sub : entity.getSubEntities()) {
            InputModel subModel = processEntity(sub);
            for (Attribute attribute : entity.getAttributes()) {
                String name = getEntityAttributeName(sub, attribute);
                subModel.addInputParameter(name, attribute.getType().toString());
                subModel.addDomain(name, attribute.getValue().getPossibleValueList());
            }
            entityModel.appendWithPrefix(entity.getName(), subModel);
            subModels.put(sub.getName(), subModel);
        }
        // second loop of the algorithm - line 15:23
        if (!subModels.isEmpty()) {
            List<String> keys = new ArrayList<>(subModels.keySet());
            for (int i = 0; i < subModels.size(); i++) {
                for (int j = i + 1; j < subModels.size(); j++) {
                    String entityA = keys.get(i);
                    String entityB = keys.get(j);
                    InputModel modelA = subModels.get(entityA);
                    InputModel modelB = subModels.get(entityB);
                    for (String attributeA : modelA.getAttributesList()) {
                        for (String attributeB : modelB.getAttributesList()) {
                            String constr;
                            if (modelA.getTypeOfAttribute(attributeA).equals(AttributeType.INT.toString())) {
                                constr = ConstraintBuilder.buildInheritanceConstr(entity.getName(), attributeA
                                        , attributeB, entityModel.EPSILON_INT);
                            } else {
                                constr = ConstraintBuilder.buildInheritanceConstr(entity.getName(), attributeA,
                                        attributeB, entityModel.EPSILON);
                            }
                            entityModel.addConstraint(constr);
                        }
                    }
                }
            }
        }
        for (Relation r : system.getRelations()) {
            if (r.getEntityA().equals(entity)) {
                // LINE 25
                InputModel modelB = processEntity(r.getEntityB());
                // LINE 26
                modelB.addEpsilonToDomains();
                //LINE 27
                int min = Integer.parseInt(r.getCardinalityMin()); // n
                int max = Integer.parseInt(r.getCardinalityMax()); // m

                //LINE 28 to 30
                for (int i = 1; i <= max; i++) {
                    entityModel.appendParametersWithPrefixAndIndex(entity.getName(), i, modelB);
                    entityModel.appendDomainsWithPrefixAndIndex(entity.getName(), i, modelB);
                    List<String> tempConstr = InputModel.getConstraintsWithPrefixAndIndex(entity.getName(), i, modelB);
                    List<String> temp = new ArrayList<>(tempConstr);
                    temp.removeIf(String::isEmpty);

                    StringBuilder constr = new StringBuilder();
                    for (int j = 0; j < temp.size(); j++) {
                        constr.append(temp.get(j));
                        if (j != temp.size() - 1) {
                            constr.append(" ").append(OperatorUtil.AND).append(" ");
                        }
                    }
                    if (!constr.isEmpty()) {
                        entityModel.addConstraint(constr.toString());
                    }
                }
                // LINE 31 to 35
                if (Integer.parseInt(r.getCardinalityMin()) > 0) {
                    List<List<String>> constraintsForInheritance = new ArrayList<>();
                    // direct attributes
                    for (String attr : modelB.getAttributesList()) {
                        String regex = entity.getName().replace(".", "\\.") + "_\\d+_" + attr.replace(".", "\\.");
                        Pattern pattern = Pattern.compile(regex);

                        List<String> matchingAttrConstr = new ArrayList<>();
                        for (String addedAttr : entityModel.getAttributesList()) {
                            Matcher matcher = pattern.matcher(addedAttr);
                            if (matcher.matches()) {
                                if (entityModel.getTypeOfAttribute(addedAttr).equals("INT")) {
                                    matchingAttrConstr.add(addedAttr + " != " + entityModel.EPSILON_INT);
                                } else {
                                    matchingAttrConstr.add(addedAttr + " != " + "\"" + entityModel.EPSILON + "\"");
                                }
                            }
                        }
                        if (r.getEntityB().getSubEntities().isEmpty()) {
                            // LINE 33 if no sub entities
                            entityModel.addConstraint(buildCombinationConstraint(matchingAttrConstr, max, min));
                        } else {
                            // If sub entities, Entity B does not exist, but only sub entities of B
                            // Thus, attributes of B need to be inherited down to the sub entities
                            constraintsForInheritance.add(matchingAttrConstr);
                        }
                    }
                    // constraintsForInheritance contains:
                    // Lists of the same attribute.
                    // Length of the lists is the number of indices.
                    // Amount of lists is the number of attributes (already inherited down to sub-entities)
                    constraintsForInheritance = transpose(constraintsForInheritance);
                    // After being transposed, each list contains all attributes of a single index.
                    // Length of the lists is now the number of attributes
                    // One list per index
                    List<List<List<String>>> constraintsForInheritanceSplitPerSub = new ArrayList<>();
                    for (int i = 0; i < constraintsForInheritance.size(); i++) {
                        List<String> ci = constraintsForInheritance.get(i);

                        constraintsForInheritanceSplitPerSub.add(new ArrayList<>());
                        for (int j = 0; j < r.getEntityB().getSubEntities().size(); j++) {
                            constraintsForInheritanceSplitPerSub.get(i).add(new ArrayList<>());
                            Entity sub = r.getEntityB().getSubEntities().get(j);
                            for (String c : ci) {
                                if (c.contains(sub.getName())) {
                                    constraintsForInheritanceSplitPerSub.get(i).get(j).add(c);
                                }
                            }
                        }
                    }
                    // constraintsForInheritanceSplitPerSub now has the lists of attributes split up into one list
                    // per sub-entity
                    List<String> constraintPerIndex = new ArrayList<>();
                    for (List<List<String>> perIndex : constraintsForInheritanceSplitPerSub) {
                        StringBuilder constr = new StringBuilder();
                        int subIndex = 0;
                        for (List<String> perSub : perIndex) {
                            StringBuilder subConstr = new StringBuilder();
                            int attrIndex = 0;
                            for (String attr : perSub) {
                                if (attrIndex > 0) {
                                    subConstr.append(" ");
                                    subConstr.append(OperatorUtil.AND);
                                    subConstr.append(" ");
                                } else {
                                    subConstr.append("(");
                                }
                                subConstr.append(attr);
                                attrIndex++;
                            }
                            if (subIndex > 0) {
                                constr.append(" ");
                                constr.append(OperatorUtil.OR);
                                constr.append(" ");
                            }
                            subConstr.append(")");
                            constr.append(subConstr);
                            subIndex++;
                        }
                        constraintPerIndex.add(constr.toString());
                    }
                    // constraintPerIndex now contains one constraint per index
                    // each constraint ensures that the inherited attributes are set on the same sub entity
                    // When x,y are attributes of A, and B,C are sub-entities of A:
                    // (A.i.B.x && A.i.B.y) || (A.i.C.x && B.i.C.y)
                    // once per index i from 0 to maximum arity of the relation
                    entityModel.addConstraint(buildCombinationConstraint(constraintPerIndex, max, min));
                }
            }
        }
        return entityModel;
    }

    private String buildCombinationConstraint(List<String> constraints, int n, int k) {
        if (constraints.isEmpty()) {
            return null;
        }
        StringBuilder combinationConstr = new StringBuilder();
        Iterator<int[]> it = new Combinations(n, k).iterator();
        while (it.hasNext()) {
            int[] next = it.next();
            combinationConstr.append(OperatorUtil.PAREN_OPEN);
            for (int i = 0; i < next.length; i++) {
                if (next.length > 1) {
                    combinationConstr.append(OperatorUtil.PAREN_OPEN);
                }
                combinationConstr.append(constraints.get(next[i]));
                if (next.length > 1) {
                    combinationConstr.append(OperatorUtil.PAREN_CLOSE);
                }
                if (i != next.length - 1) {
                    combinationConstr.append(" ").append(OperatorUtil.AND).append(" ");
                } else {
                    combinationConstr.append(OperatorUtil.PAREN_CLOSE);
                }
            }
            if (it.hasNext()) {
                combinationConstr.append(" ").append(OperatorUtil.OR).append(" ");
            }
        }
        return combinationConstr.toString();
    }

    private boolean checkLeaf(Entity entity) {
        if (entity.getSubEntities() != null && !entity.getSubEntities().isEmpty()) {
            return false;
        }
        for (Relation relation : system.getRelations()) {
            if (relation.getEntityA().equals(entity)) {
                return false;
            }
        }
        return true;
    }

    private String getEntityAttributeName(Entity entity, Attribute attribute) {
        return entity.getName() + "_" + attribute.getName();
    }

    private void showErrorPopup(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
