package tug.tobkul.ontologybrowser.inputmodel.model;

import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.OperatorUtil;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;

public class ConstraintBuilder {
    public static String buildInheritanceConstr(String entityName, String attributeA, String attributeB, String epsilon) {
        return OperatorUtil.PAREN_OPEN + entityName + "_" + attributeA + " = " + "\"" + epsilon + "\"" + " " +
                OperatorUtil.AND + " " + entityName + "_" + attributeB + " != " + "\"" + epsilon + "\"" +
                OperatorUtil.PAREN_CLOSE + " " + OperatorUtil.OR + " " + OperatorUtil.PAREN_OPEN + entityName + "_" +
                attributeA + " != " + "\"" + epsilon + "\"" + " " + OperatorUtil.AND + " " + entityName + "_" +
                attributeB + " = " + "\"" + epsilon + "\"" + OperatorUtil.PAREN_CLOSE;
    }
}
