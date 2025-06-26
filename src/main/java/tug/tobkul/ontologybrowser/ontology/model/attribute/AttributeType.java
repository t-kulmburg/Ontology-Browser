package tug.tobkul.ontologybrowser.ontology.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeType {
    BOOL,
    ENUM,
    INT;

    @JsonValue
    @Override
    public String toString() {
        switch (this) {
            case BOOL -> {
                return "BOOL";
            }
            case ENUM -> {
                return "ENUM";
            }
            case INT -> {
                return "INT";
            }
        }
        return null;
    }
}
