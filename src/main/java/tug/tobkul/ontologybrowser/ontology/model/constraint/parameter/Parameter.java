package tug.tobkul.ontologybrowser.ontology.model.constraint.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;
import java.util.Objects;

public class Parameter {

    @JsonIgnore
    protected Entity entity;
    @JsonIgnore
    protected String entityName;

    @JsonIgnore
    protected Attribute attribute;
    @JsonIgnore
    protected String attributeName;

    @JsonIgnore
    protected AttributeType attributeType;

    protected String expression;

    public Parameter() {
    }

    public Parameter(Entity entity, Attribute attribute, String expression) {
        this.entity = entity;
        this.entityName = entity.getName();
        this.attribute = attribute;
        this.attributeName = attribute.getName();
        this.attributeType = attribute.getType();
        this.expression = expression;
    }

    public AttributeType getType() {
        return this.attributeType;
    }

    public void setType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public String toString() {
        return getExpression();
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        if (expression == null) {
            // TODO REMOVE - JUST FOR COMPATIBILITY
            return entity.getName().replace(" ", "_") + "." + attribute.getName().replace(" ", "_");
        }
        return expression;
    }

    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        if (outerSystem != null) {
            List<Entity> listA = outerSystem.getEntities().stream().filter(e -> e.getName().equals(entityName))
                    .toList();
            if (listA.size() != 1) {
                throw new IllegalArgumentException(
                        "Entities list contains entities that do not match present " + "entities of this System");
            }
            entity = listA.getFirst();
            if (entity != null) {
                List<Attribute> attributes = entity.getAttributes().stream()
                        .filter(a -> a.getName().equals(attributeName)).toList();
                if (attributes.size() != 1) {
                    throw new IllegalArgumentException("Attribute A does not match present attributes of this Entity");
                }
                attribute = attributes.getFirst();
            }
        }
    }

    @JsonIgnore
    public Entity getEntity() {
        return entity;
    }

    @JsonIgnore
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @JsonIgnore
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonIgnore
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @JsonProperty("entity")
    public String getEntityNameForJson() {
        return entity.getName();
    }

    @JsonProperty("attribute")
    public String getAttributeANameForJson() {
        return attribute.getName();
    }

    @JsonProperty("entity")
    public void setEntityANameFromJson(String entityName) {
        this.entityName = entityName;
    }

    @JsonProperty("attribute")
    public void setAttributeANameFromJson(String attributeName) {
        this.attributeName = attributeName;
    }

    public boolean containsEntity(Entity entity) {
        return this.entity.equals(entity);
    }

    public boolean containsAttribute(Attribute attribute) {
        return this.attribute.equals(attribute);
    }

    // Need to override to have the correct uniqueness in the set of parameters
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter that = (Parameter) o;
        return Objects.equals(entityName, that.entityName)
                && Objects.equals(attributeName, that.attributeName)
                && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, attributeName, expression);
    }
}
