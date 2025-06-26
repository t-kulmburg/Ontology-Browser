package tug.tobkul.ontologybrowser.inputmodel.model;

import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.OperatorUtil;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;

public class ConstraintBuilder {
    public static String buildInheritanceConstr(String entityName, String attributeA, String attributeB) {
        return OperatorUtil.PAREN_OPEN +
                entityName +
                "_" +
                attributeA +
                " = " +
                "\"" + Term.EPSILON + "\"" +
                " " +
                OperatorUtil.AND +
                " " +
                entityName +
                "_" +
                attributeB +
                " != " +
                "\"" + Term.EPSILON + "\"" +
                OperatorUtil.PAREN_CLOSE +
                " " +
                OperatorUtil.OR +
                " " +
                OperatorUtil.PAREN_OPEN +
                entityName +
                "_" +
                attributeA +
                " != " +
                "\"" + Term.EPSILON + "\"" +
                " " +
                OperatorUtil.AND +
                " " +
                entityName +
                "_" +
                attributeB +
                " = " +
                "\"" + Term.EPSILON + "\"" +
                OperatorUtil.PAREN_CLOSE;
    }
}
