package tug.tobkul.ontologybrowser.ontology.model.constraint.term;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
    String EPSILON = "EPSILON";
    String EPSILON_INT = String.valueOf(Integer.MAX_VALUE);

    String getExpression();

    void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem);
}
