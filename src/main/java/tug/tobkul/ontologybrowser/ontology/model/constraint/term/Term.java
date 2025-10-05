package tug.tobkul.ontologybrowser.ontology.model.constraint.term;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParameterTerm.class, name = "parameter"),
        @JsonSubTypes.Type(value = ValueTerm.class, name = "value"),
        @JsonSubTypes.Type(value = ArithmeticParameterTerm.class, name = "arithmeticParameter"),
        @JsonSubTypes.Type(value = ArithmeticValueTerm.class, name = "arithmeticValue")
})
public interface Term {
    String EPSILON = "ϵ";
    String EPSILON_INT = "21474836"; // for higher values, ACTS prints. Domains over [-21474836, 21474836] are strongly inadvisable!

    String getExpression();

    void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem);

    boolean containsEntity(Entity entity);

    boolean containsAttribute(Attribute attribute);
}
