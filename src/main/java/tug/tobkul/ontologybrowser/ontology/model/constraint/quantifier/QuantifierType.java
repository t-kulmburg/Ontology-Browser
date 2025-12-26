package tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier;

public enum QuantifierType {
    FOR_ALL(QuantifierUtil.FOR_ALL),
    EXISTS(QuantifierUtil.EXISTS);

    private final String sign;

    QuantifierType(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public static QuantifierType fromSign(String sign) {
        for (QuantifierType type : QuantifierType.values()) {
            if (type.getSign().equals(sign)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with sign: " + sign);
    }
}
