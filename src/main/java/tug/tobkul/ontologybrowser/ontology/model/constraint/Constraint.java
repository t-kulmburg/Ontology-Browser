package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

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
}
