package tug.tobkul.ontologybrowser.ontology.model.constraint.term;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class ParameterTerm implements Term {
    private Parameter parameter;

    public ParameterTerm() {
    }

    public ParameterTerm(Parameter parameter) {
        this.parameter = parameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    @JsonIgnore
    public String getExpression() {
        return parameter.getExpression();
    }

    @Override
    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        parameter.setEntitiesAndAttributesFromOuterSystem(outerSystem);
    }

    @Override
    public boolean containsEntity(Entity entity) {
        return parameter.containsEntity(entity);
    }

    @Override
    public boolean containsAttribute(Attribute attribute) {
        return parameter.containsAttribute(attribute);
    }
}
