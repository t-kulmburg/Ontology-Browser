package tug.tobkul.ontologybrowser.ontology.model.constraint.term;

public enum TermType {
    PARAMETER("parameter"),
    VALUE("value"),
    A_PARAMETER("arithmeticParameter"),
    A_VALUE("arithmeticValue");

    private final String str;

    TermType(String str) {
        this.str = str;
    }

    public static TermType fromString(String str) {
        for (TermType type : TermType.values()) {
            if (type.toString().equals(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with string: " + str);
    }

    @Override
    public String toString() {
        return str;
    }
}
