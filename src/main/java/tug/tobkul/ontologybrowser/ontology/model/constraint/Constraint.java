package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleConstraint.class, name = "simple"),
        @JsonSubTypes.Type(value = CompositeConstraint.class, name = "composite"),
})
public interface Constraint {
    boolean isComposite();

    String getExpression();

    void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem);

    oSystem getOuterSystem();

    List<Quantifier> getQuantifierList();

    void setQuantifierList(List<Quantifier> quantifierList);

    boolean containsEntity(Entity entity);

    boolean containsAttribute(Attribute attribute);
}
