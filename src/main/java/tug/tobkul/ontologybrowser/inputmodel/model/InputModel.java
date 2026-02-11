package tug.tobkul.ontologybrowser.inputmodel.model;

import tug.tobkul.ontologybrowser.ontology.model.constraint.CompositeConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
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
    private final Map<String, String> V = new LinkedHashMap<>(); // Each key is am input parameters, value is the type.
    private final Map<String, List<String>> D = new LinkedHashMap<>(); // Domains. Each V is a Key with List of possible values as value.
    private final List<String> C = new ArrayList<>(); // Set of constraints

    private final Map<String, Map<String, List<String>>> userDefinedConstraintCorrespondenceMap = new LinkedHashMap<>();

    public void addInputParameter(String inputParameter, String type) {
        V.put(inputParameter, type);
    }

    public void addDomain(String inputParameter, List<String> domain) {
        D.put(inputParameter, domain);
    }

    public void addConstraint(String constraint) {
        if(constraint == null) return;
        C.add(constraint);
    }

    public void append(InputModel inputModel) {
        V.putAll(inputModel.V);
        D.putAll(inputModel.D);
        C.addAll(inputModel.C);
    }

    public void appendWithPrefix(String prefix, InputModel inputModel) {
        V.putAll(inputModel.V.entrySet().stream().collect(Collectors.toMap(
                entry -> prefix + "_" + entry.getKey(),
                Map.Entry::getValue)));
        D.putAll(inputModel.D.entrySet().stream().collect(Collectors.toMap(
                entry -> prefix + "_" + entry.getKey(),
                Map.Entry::getValue
        )));
        C.addAll(inputModel.C);
    }

    public void appendParametersWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        V.putAll(inputModel.V.entrySet().stream().collect(Collectors.toMap(
                entry -> prefix + "_" + index + "_" + entry.getKey(),
                Map.Entry::getValue
        )));
    }

    public void appendDomainsWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        D.putAll(inputModel.D.entrySet().stream().collect(Collectors.toMap(
                entry -> prefix + "_" + index + "_" + entry.getKey(),
                Map.Entry::getValue
        )));
    }

    public static List<String> getConstraintsWithPrefixAndIndex(String prefix, int index, InputModel inputModel) {
        Map<String, String> tempV = new LinkedHashMap<>(inputModel.V.entrySet().stream().collect(Collectors.toMap(
                entry -> prefix + "_" + index + "_" + entry.getKey(),
                Map.Entry::getValue
        )));
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

        return inputModel.C.stream().map(s -> Pattern.compile(regex).matcher(s)
                .replaceAll(match -> replacements.get(match.group()))).toList();
    }

    public void addEpsilonToDomains() {
        D.forEach((key, value) -> {
            if (V.get(key).equals("INT")) {
                if (!value.contains(Term.EPSILON_INT)) {
                    value.add(Term.EPSILON_INT);
                }
            } else {
                if (!value.contains(Term.EPSILON)) {
                    value.add(Term.EPSILON);
                }
            }
        });
    }

    public void addExpandedConstrains(oSystem system) {
        Set<String> expandedConstraints = expandConstraintsQuantifiers(system);
        System.out.println("Expanded Constraints:" + expandedConstraints);
        C.addAll(expandedConstraints);
    }

    public Set<String> expandConstraintsQuantifiers(oSystem system){
        Set<String> expandedConstraints = expandConstraints(system);
        AtomicReference<List<String>> cs = new AtomicReference<>(system.getConstraints().stream().map(c -> c.getConstraint().getExpression()).toList());
        Set<String> finalConstraints = new LinkedHashSet<>();

        cs.get().forEach(constraint -> {
            String regex = constraint.replace("∀", "\\d").replace("∃", "\\d");
            Pattern pattern = Pattern.compile(regex);

            List<String> matched = new ArrayList<>();
            expandedConstraints.removeIf(s -> {
                if (pattern.matcher(s).matches()) {
                    matched.add(s);
                    return true;
                } else {
                    return false;
                }
            });
            System.out.println("CS" + constraint);
            System.out.println("MATCHED" + matched);
            if(constraint.contains("∀")){
                finalConstraints.add(String.join(" && ", matched));
            } else if (constraint.contains("∃")){
                finalConstraints.add(String.join(" || ", matched));
            } else {
                finalConstraints.add(constraint);
            }
        });
        if(!expandedConstraints.isEmpty()) {
            System.out.println("WARNING: Not all quantifiers could be resolved!");
        }
        return finalConstraints;
    }

    public Set<String> expandConstraints(oSystem system) {
        System.out.println("In expandConstraints");
        buildConstraintCorrespondenceMap(system);
        System.out.println("ConstraintCorrespondenceMap");
        System.out.println(userDefinedConstraintCorrespondenceMap);
        AtomicReference<List<String>> cs = new AtomicReference<>(system.getConstraints().stream().map(c -> c.getConstraint().getExpression()).toList());
        System.out.println("CS:");
        System.out.println(cs);
        userDefinedConstraintCorrespondenceMap.forEach((entity, attributes) -> {
            System.out.println(entity);
            List<String> csx = new ArrayList<>();
            cs.get().forEach(constraint -> {
                List<String> csxSingle = new ArrayList<>();
                for (String attribute : attributes.keySet()) {
                    for (int i = 0; i < attributes.get(attribute).size(); i++) {
                        if (csxSingle.size() <= i) {
                            csxSingle.add(constraint.replace(attribute, attributes.get(attribute).get(i)));
                        } else {
                            csxSingle.set(i, csxSingle.get(i).replace(attribute, attributes.get(attribute).get(i)));
                        }
                    }
                }
                csx.addAll(csxSingle);
            });
            cs.set(csx);
        });
        // Use set to remove duplicates
        return new LinkedHashSet<>(cs.get());
    }

    public void buildConstraintCorrespondenceMap(oSystem system) {
        System.out.println("In buildConstraintCorrespondenceMap");
        system.getConstraints().forEach(
                constraint -> {
                    if (constraint.getConstraint().isComposite()) {
                        expandCompositeConstraint((CompositeConstraint) constraint.getConstraint());
                    } else {
                        expandSimpleConstraint((SimpleConstraint) constraint.getConstraint());
                    }
                }
        );
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
        if (!userDefinedConstraintCorrespondenceMap.containsKey(parameter.getEntity().getName())) {
            userDefinedConstraintCorrespondenceMap.put(parameter.getEntity().getName(), new HashMap<>());
        }
        userDefinedConstraintCorrespondenceMap.get(parameter.getEntity().getName()).put(
                parameter.getExpression(), correspondingParameters
        );
    }

    private List<String> getCorrespondingInputParameters(String attribute) {
        System.out.println("In getCorrespondingInputParameters: " + attribute);
        List<String> correspondingInputParameters = new ArrayList<>();
        for (String k : V.keySet()) {
            System.out.println(k);
            if (k.endsWith(attribute.replace(".", "_").replaceAll(".*?[∀∃]", ""))) {
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

