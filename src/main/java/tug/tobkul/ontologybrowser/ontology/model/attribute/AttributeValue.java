package tug.tobkul.ontologybrowser.ontology.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public abstract class AttributeValue<T> {
    protected T value;

    public AttributeValue() {
    }

    public AttributeValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String toString() {
        if (value != null) {
            return value.toString();
        }
        return "";
    }

    @JsonIgnore
    public abstract List<String> getPossibleValueList();
}
