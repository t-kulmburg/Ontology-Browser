package tug.tobkul.ontologybrowser.ontology.model.constraint.term;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.ArithmeticOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class ArithmeticValueTerm implements Term {
    private Parameter lhs;
    private String rhs;
    private ArithmeticOperator arithmeticOperator;

    public ArithmeticValueTerm() {
    }

    public ArithmeticValueTerm(Parameter lhs, String rhs, ArithmeticOperator arithmeticOperator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.arithmeticOperator = arithmeticOperator;
    }

    public Parameter getLhs() {
        return lhs;
    }

    public void setLhs(Parameter lhs) {
        this.lhs = lhs;
    }

    public String getRhs() {
        return rhs;
    }

    public void setRhs(String rhs) {
        this.rhs = rhs;
    }

    public ArithmeticOperator getArithmeticOperator() {
        return arithmeticOperator;
    }

    public void setArithmeticOperator(ArithmeticOperator arithmeticOperator) {
        this.arithmeticOperator = arithmeticOperator;
    }

    @Override
    @JsonIgnore
    public String getExpression() {
        return lhs.getExpression() + " " + arithmeticOperator.getSign() + " " + rhs.replace(" ","_");
    }

    @Override
    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        lhs.setEntitiesAndAttributesFromOuterSystem(outerSystem);
    }
}
