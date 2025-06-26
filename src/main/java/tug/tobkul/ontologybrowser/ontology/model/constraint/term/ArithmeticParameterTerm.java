package tug.tobkul.ontologybrowser.ontology.model.constraint.term;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.ArithmeticOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class ArithmeticParameterTerm implements Term {
    private Parameter lhs;
    private Parameter rhs;
    private ArithmeticOperator arithmeticOperator;

    public ArithmeticParameterTerm() {
    }

    public ArithmeticParameterTerm(Parameter lhs, Parameter rhs, ArithmeticOperator arithmeticOperator) {
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

    public Parameter getRhs() {
        return rhs;
    }

    public void setRhs(Parameter rhs) {
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
        return lhs.getExpression() + " " + arithmeticOperator.getSign() + " " + rhs.getExpression();
    }

    @Override
    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        lhs.setEntitiesAndAttributesFromOuterSystem(outerSystem);
        rhs.setEntitiesAndAttributesFromOuterSystem(outerSystem);
    }
}