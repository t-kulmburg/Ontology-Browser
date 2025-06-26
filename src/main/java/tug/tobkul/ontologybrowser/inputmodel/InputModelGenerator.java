package tug.tobkul.ontologybrowser.inputmodel;

import javafx.scene.control.Alert;
import org.apache.commons.math3.util.Combinations;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import tug.tobkul.ontologybrowser.inputmodel.model.ConstraintBuilder;
import tug.tobkul.ontologybrowser.inputmodel.model.InputModel;
import tug.tobkul.ontologybrowser.ontology.graph.EntityGraphUtil;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.OperatorUtil;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputModelGenerator {
    private final InputModel inputModel = new InputModel();

    private final oSystem system;
    private final String name;

    private Entity rootEntity;

    private final boolean isCli;

    private String cliError = "";

    public InputModelGenerator(String name, oSystem system) {
        this.name = name;
        this.system = system;
        this.isCli = false;
    }

    public InputModelGenerator(String name, oSystem system, boolean isCli) {
        this.name = name;
        this.system = system;
        this.isCli = isCli;
    }

    public Entity getRootEntity() {
        return rootEntity;
    }

    public String getCliError() {
        return cliError;
    }

    public String generate() {
        rootEntity = getRootEntity(system);
        if (rootEntity == null) {
            return null;
        }
        inputModel.append(processEntity(rootEntity));
        inputModel.addExpandedConstrains(system);
        String ret = "";
        ret += "[System]\nName: " + name + "\n\n";
        ret += inputModel.toString();
        return ret;
    }

    private InputModel processEntity(Entity entity) {
        InputModel entityModel = new InputModel();
        for (Attribute attribute : entity.getAttributes()) {
            String name = getEntityAttributeName(entity, attribute);
            entityModel.addInputParameter(name, attribute.getType().toString());
            entityModel.addDomain(name, attribute.getValue().getPossibleValueList());
        }
        if (checkLeaf(entity)) {
            return entityModel;
        }
        Map<String, InputModel> subModels = new HashMap<>();
        // first loop of the algorithm - line 8:14
        for (Entity sub : entity.getSubEntities()) {
            InputModel subModel = processEntity(sub);
            entityModel.appendWithPrefix(entity.getName(), subModel);
            subModels.put(sub.getName(), subModel);
        }
        // second loop of the algorithm - line 15:23
        if (subModels.size() > 1) {
            List<String> keys = new ArrayList<>(subModels.keySet());
            for (int i = 0; i < subModels.size(); i++) {
                for (int j = i + 1; j < subModels.size(); j++) {
                    String entityA = keys.get(i);
                    String entityB = keys.get(j);
                    InputModel modelA = subModels.get(entityA);
                    InputModel modelB = subModels.get(entityB);
                    for (String attributeA : modelA.getAttributesList()) {
                        for (String attributeB : modelB.getAttributesList()) {
                            String constr = ConstraintBuilder.buildInheritanceConstr(
                                    entity.getName(), attributeA, attributeB
                            );
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
                if (Integer.parseInt(r.getCardinalityMin()) > 0) {
                    for (String attr : modelB.getAttributesList()) {

                        String regex = entity.getName().replace(".", "\\.") +
                                "_\\d+_" +
                                attr.replace(".", "\\.");
                        Pattern pattern = Pattern.compile(regex);

                        List<String> matchingAttrConstr = new ArrayList<>();
                        for (String addedAttr : entityModel.getAttributesList()) {
                            Matcher matcher = pattern.matcher(addedAttr);
                            if (matcher.matches()) {
                                if (entityModel.getTypeOfAttribute(addedAttr).equals("INT")) {
                                    matchingAttrConstr.add(addedAttr + " != " + Term.EPSILON_INT);
                                } else {
                                    matchingAttrConstr.add(addedAttr + " != " + "\"" + Term.EPSILON + "\"");
                                }

                            }
                        }
                        entityModel.addConstraint(buildCombinationConstraint(matchingAttrConstr, max, min));
                    }
                }
            }
        }
        return entityModel;
    }

    private String buildCombinationConstraint(List<String> constraints, int n, int k) {
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

    private Entity getRootEntity(oSystem system) {
        DefaultDirectedGraph<Entity, DefaultEdge> graph = EntityGraphUtil.buildGraph(system);
        Set<Entity> cycle = EntityGraphUtil.detectCycle(graph);
        if (cycle != null) {
            if (isCli) {
                cliError = """
                        Error: Cycle detected.
                        Involved Entities:
                        """ +
                        String.join(" - ", cycle.stream().map(Entity::getName).toList());
            } else {
                showErrorPopup("Error", "Cycle detected", "Involved Entities:\n" +
                        String.join(" - ", cycle.stream().map(Entity::getName).toList()));
            }
            return null;
        }
        Set<Entity> orphans = EntityGraphUtil.detectOrphans(graph);
        if (orphans != null) {
            System.out.println("orphans found");
            System.out.println(orphans);
        }
        Optional<Entity> root = EntityGraphUtil.findRootEntity(graph);
        if (root.isEmpty()) {
            if (isCli) {
                cliError = "Error: Root entity not found!";
            } else {
                showErrorPopup("Error", "Root entity not found", null);
            }
            return null;
        }
        return root.get();
    }

    private void showErrorPopup(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
