package tug.tobkul.ontologybrowser.ontology.model.constraint.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

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

    public Parameter() {
    }

    public Parameter(Entity entity, Attribute attribute) {
        this.entity = entity;
        this.entityName = entity.getName();
        this.attribute = attribute;
        this.attributeName = attribute.getName();
        this.attributeType = attribute.getType();
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

    @JsonIgnore
    public String getExpression() {
        return entity.getName() + "_" + attribute.getName();
    }


    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        if (outerSystem != null) {
            List<Entity> listA = outerSystem.getEntities().stream().filter(e -> e.getName().equals(entityName)).toList();
            if (listA.size() != 1) {
                throw new IllegalArgumentException("Entities list contains entities that do not match present entities of this System");
            }
            entity = listA.getFirst();
            if (entity != null) {
                List<Attribute> attributes = entity.getAttributes().stream().filter(a -> a.getName().equals(attributeName)).toList();
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

    @JsonProperty("Entity")
    public String getEntityNameForJson() {
        return entity.getName();
    }

    @JsonProperty("Attribute")
    public String getAttributeANameForJson() {
        return attribute.getName();
    }

    @JsonProperty("Entity")
    public void setEntityANameFromJson(String entityName) {
        this.entityName = entityName;
    }

    @JsonProperty("Attribute")
    public void setAttributeANameFromJson(String attributeName) {
        this.attributeName = attributeName;
    }
}
