package tug.tobkul.ontologybrowser.ontology.model.constraint.operator.relational;

import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.OperatorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RelationalOperator {
    GREATER_THAN(OperatorUtil.GREATER_THAN),
    LESS_THAN(OperatorUtil.LESS_THAN),
    GREATER_EQUAL(OperatorUtil.GREATER_EQUAL),
    LESS_EQUAL(OperatorUtil.LESS_EQUAL),
    NOT_EQUAL(OperatorUtil.NOT_EQUAL),
    EQUAL(OperatorUtil.EQUAL);

    private final String sign;

    RelationalOperator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public static List<RelationalOperator> getOperators(AttributeType type) {
        switch (type) {
            case BOOL, ENUM -> {
                return new ArrayList<>(List.of(NOT_EQUAL, EQUAL));
            }
            case INT -> {
                return Arrays.stream(RelationalOperator.values()).toList();
            }
        }
        return new ArrayList<>();
    }

    public static RelationalOperator fromSign(String sign) {
        for (RelationalOperator operator : RelationalOperator.values()) {
            if (operator.getSign().equals(sign)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("No enum constant with sign: " + sign);
    }
}
