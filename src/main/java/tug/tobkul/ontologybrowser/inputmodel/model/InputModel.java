package tug.tobkul.ontologybrowser.inputmodel.model;

import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.*;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.QuantifierType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ArithmeticParameterTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ArithmeticValueTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ParameterTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InputModel {
    // Each key is an input parameters, value is the type.
    private final Map<String, String> V = new LinkedHashMap<>();
    // Domains. Each V is a Key with List of possible values as value.
    private final Map<String, List<String>> D = new LinkedHashMap<>();
    // Set of constraints
    private final List<String> C = new ArrayList<>();

    public String EPSILON = "ϵ";
    public String EPSILON_INT; // for higher values, ACTS prints. Domains over [-21474836, 21474836] are
    // strongly inadvisable!

    private final Map<String, List<String>> userDefinedConstraintCorrespondenceMap = new LinkedHashMap<>();

    public InputModel(oSystem system) {
        int largestValue = Integer.MIN_VALUE;
        for (Entity entity : system.getEntities()) {
            int max = entity.getAttributes().stream().filter(attribute -> attribute.getType().equals(AttributeType.INT))
                    .map(Attribute::getValue).flatMap(attributeValue -> attributeValue.getPossibleValueList().stream())
                    .mapToInt(Integer::parseInt).max().orElse(Integer.MIN_VALUE);
            if (max > largestValue) {
                largestValue = max;
            }
        }
        EPSILON_INT = String.valueOf(largestValue + 2);
    }

    public static List<String> getConstraintsWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        Map<String, String> tempV = new LinkedHashMap<>(inputModel.V.entrySet().stream()
                .collect(Collectors.toMap(entry -> prefix + "_" + index + "_" + entry.getKey(), Map.Entry::getValue)));
        Map<String, String> replacements = new LinkedHashMap<>();
        for (String k : inputModel.V.keySet()) {
            for (String tempK : tempV.keySet()) {
                if (tempK.endsWith(k)) {
                    replacements.put(k, tempK);
                    break;
                }
            }
        }
        String regex = String.join("|", replacements.keySet());

        return inputModel.C.stream()
                .map(s -> Pattern.compile(regex).matcher(s).replaceAll(match -> replacements.get(match.group())))
                .toList();
    }

    public void addInputParameter(String inputParameter, String type) {
        V.put(inputParameter, type);
    }

    public void addDomain(String inputParameter, List<String> domain) {
        D.put(inputParameter, domain);
    }

    public void addConstraint(String constraint) {
        if (constraint == null) {
            return;
        }
        C.add(constraint);
    }

    public void append(InputModel inputModel) {
        V.putAll(inputModel.V);
        D.putAll(inputModel.D);
        C.addAll(inputModel.C);
    }

    public void appendWithPrefix(String prefix, InputModel inputModel) {
        V.putAll(inputModel.V.entrySet().stream()
                .collect(Collectors.toMap(entry -> prefix + "_" + entry.getKey(), Map.Entry::getValue)));
        D.putAll(inputModel.D.entrySet().stream()
                .collect(Collectors.toMap(entry -> prefix + "_" + entry.getKey(), Map.Entry::getValue)));
        C.addAll(inputModel.C);
    }

    public void appendParametersWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        V.putAll(inputModel.V.entrySet().stream()
                .collect(Collectors.toMap(entry -> prefix + "_" + index + "_" + entry.getKey(), Map.Entry::getValue)));
    }

    public void appendDomainsWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        D.putAll(inputModel.D.entrySet().stream()
                .collect(Collectors.toMap(entry -> prefix + "_" + index + "_" + entry.getKey(), Map.Entry::getValue)));
    }

    public void addEpsilonToDomains() {
        D.forEach((key, value) -> {
            if (V.get(key).equals("INT")) {
                if (!value.contains(EPSILON_INT)) {
                    value.add(EPSILON_INT);
                }
            } else {
                if (!value.contains(EPSILON)) {
                    value.add(EPSILON);
                }
            }
        });
    }

    public void addExpandedConstrains(Scenario scenario) {
        Set<String> expandedConstraints = expandConstraintsQuantifiers(scenario);
        C.addAll(expandedConstraints);
    }

    public Set<String> expandConstraintsQuantifiers(Scenario scenario) {
        buildConstraintCorrespondenceMap(scenario);
        AtomicReference<List<Constraint>> cs =
                new AtomicReference<>(scenario.getConstraintHolderList().stream().map(ConstraintHolder::getConstraint)
                        .collect(Collectors.toList()));
        Set<String> finalConstraints = new LinkedHashSet<>();

        cs.get().forEach(constraint -> {
            if (constraint.getQuantifierList().isEmpty()) {
                finalConstraints.add(constraint.getExpression());
                return;
            }
            String constraintAfterQuantifiers = constraint.getExpression();
            for (Quantifier quantifier : constraint.getQuantifierList().reversed()) {
                String quantifierRegex = "\\S*" + quantifier.getType().getSign() + quantifier.getIdentifier() + "\\S*";
                String key =
                        userDefinedConstraintCorrespondenceMap.keySet().stream().filter(s -> s.matches(quantifierRegex))
                                .findFirst().get();

                List<String> tempConstraints = new ArrayList<>();
                for (String attr : userDefinedConstraintCorrespondenceMap.get(key)) {
                    tempConstraints.add(constraintAfterQuantifiers.replaceAll(quantifierRegex, attr));
                }
                if (quantifier.getType().equals(QuantifierType.FOR_ALL)) {
                    constraintAfterQuantifiers = "(" + String.join(" && ", tempConstraints) + " )";
                } else if (quantifier.getType().equals(QuantifierType.EXISTS)) {
                    constraintAfterQuantifiers = String.join(" || ", tempConstraints);
                }
            }
            finalConstraints.add(constraintAfterQuantifiers);
        });
        return finalConstraints;
    }

    public void buildConstraintCorrespondenceMap(Scenario scenario) {
        scenario.getConstraintHolderList().forEach(constraint -> {
            if (constraint.getConstraint().isComposite()) {
                expandCompositeConstraint((CompositeConstraint) constraint.getConstraint());
            } else {
                expandSimpleConstraint((SimpleConstraint) constraint.getConstraint());
            }
        });
    }

    private void expandCompositeConstraint(CompositeConstraint constraint) {
        if (constraint.getLhs().isComposite()) {
            expandCompositeConstraint((CompositeConstraint) constraint.getLhs());
        } else {
            expandSimpleConstraint((SimpleConstraint) constraint.getLhs());
        }
        if (constraint.getRhs().isComposite()) {
            expandCompositeConstraint((CompositeConstraint) constraint.getRhs());
        } else {
            expandSimpleConstraint((SimpleConstraint) constraint.getRhs());
        }
    }

    private void expandSimpleConstraint(SimpleConstraint constraint) {
        expandTerm(constraint.getLhs());
        expandTerm(constraint.getRhs());
    }

    private void expandTerm(Term term) {
        if (term instanceof ParameterTerm) {
            expandParameter(((ParameterTerm) term).getParameter());
        } else if (term instanceof ArithmeticParameterTerm) {
            expandParameter(((ArithmeticParameterTerm) term).getLhs());
            expandParameter(((ArithmeticParameterTerm) term).getRhs());
        } else if (term instanceof ArithmeticValueTerm) {
            expandParameter(((ArithmeticValueTerm) term).getLhs());
        }
    }

    private void expandParameter(Parameter parameter) {
        List<String> correspondingParameters = getCorrespondingInputParameters(parameter.getExpression());
        userDefinedConstraintCorrespondenceMap.put(parameter.getExpression(), correspondingParameters);
    }

    private List<String> getCorrespondingInputParameters(String attribute) {
        List<String> correspondingInputParameters = new ArrayList<>();
        for (String k : V.keySet()) {
            if (k.endsWith(attribute.replace(".", "_").replaceAll(".*?[∀∃][a-z]", ""))) {
                correspondingInputParameters.add(k);
            }
        }
        return correspondingInputParameters;
    }

    public Map<String, String> getAttributeMap() {
        return V;
    }

    public List<String> getAttributesList() {
        return V.keySet().stream().toList();
    }

    public String getTypeOfAttribute(String attribute) {
        return V.get(attribute);
    }

    public List<String> getConstraints() {
        return C;
    }

    private String convertTypeForOutput(String type) {
        switch (type) {
            case "INT" -> {
                return "int";
            }
            case "ENUM", "BOOL" -> {
                return "enum";
            }
            default -> {
                return "";
            }
        }
    }

    @Override
    public String toString() {
        String ret = "[Parameter]";

        List<String> sortedParams = new ArrayList<>(V.keySet());
        Collections.sort(sortedParams);
        for (String param : sortedParams) {
            ret += "\n" + param + " (" + convertTypeForOutput(V.get(param)) + "): ";
            for (int i = 0; i < D.get(param).size(); i++) {
                ret += D.get(param).get(i);
                if (i != D.get(param).size() - 1) {
                    ret += ",";
                }
            }
        }
        ret += "\n\n[Constraint]";
        ret += "\n" + String.join("\n", C);
        return ret;
    }
}

