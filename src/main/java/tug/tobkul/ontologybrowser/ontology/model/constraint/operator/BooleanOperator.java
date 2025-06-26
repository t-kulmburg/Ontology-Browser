package tug.tobkul.ontologybrowser.ontology.model.constraint.operator;

public enum BooleanOperator {
    AND(OperatorUtil.AND),
    OR(OperatorUtil.OR),
    IMPL(OperatorUtil.IMPL);

    private final String sign;

    BooleanOperator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public static BooleanOperator fromSign(String sign) {
        for (BooleanOperator operator : BooleanOperator.values()) {
            if (operator.getSign().equals(sign)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("No enum constant with sign: " + sign);
    }
}
