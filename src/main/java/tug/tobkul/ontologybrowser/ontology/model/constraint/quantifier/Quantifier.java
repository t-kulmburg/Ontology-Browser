package tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

public class Quantifier {

    private QuantifierType type;

    @JsonIgnore
    private Entity entity;

    @JsonIgnore
    private String entityName;

    public Quantifier(){}
    public Quantifier(QuantifierType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }


    public QuantifierType getType() {
        return type;
    }
    public void setType(QuantifierType type) {
        this.type = type;
    }

    @JsonIgnore
    public Entity getEntity() {
        return entity;
    }

    @JsonIgnore
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @JsonProperty("entity")
    public String getEntityNameForJson() {
        return entity.getName();
    }

    @JsonProperty("entity")
    public void setEntityANameFromJson(String entityName) {
        this.entityName = entityName;
    }

    @JsonIgnore
    public void setEntityFromOuterSystem(oSystem outerSystem) {
        if (outerSystem != null) {
            List<Entity> listA = outerSystem.getEntities().stream().filter(e -> e.getName().equals(entityName)).toList();
            if (listA.size() != 1) {
                throw new IllegalArgumentException("Entities list contains entities that do not match present entities of this System");
            }
            entity = listA.getFirst();
        }
    }

    @Override
    public String toString() {
        return type.getSign() + entity.getName();
    }
}
