package tug.tobkul.ontologybrowser.ontology.model.constraint.term;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class ValueTerm implements Term {
    private String value;

    public ValueTerm() {
    }

    public ValueTerm(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @JsonIgnore
    public String getExpression() {
        return value.replace(" ", "_");
    }

    @Override
    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
    }
}
