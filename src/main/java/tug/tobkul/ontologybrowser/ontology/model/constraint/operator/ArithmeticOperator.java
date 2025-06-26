package tug.tobkul.ontologybrowser.ontology.model.constraint.operator;

public enum ArithmeticOperator {
    PLUS(OperatorUtil.PLUS),
    MINUS(OperatorUtil.MINUS),
    MULTIPLY(OperatorUtil.MULTIPLY),
    DIVIDE(OperatorUtil.DIVIDE),
    MODULO(OperatorUtil.MODULO);

    private final String sign;

    ArithmeticOperator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public static ArithmeticOperator fromSign(String sign) {
        for (ArithmeticOperator operator : ArithmeticOperator.values()) {
            if (operator.getSign().equals(sign)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("No enum constant with sign: " + sign);
    }
}
