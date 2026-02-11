package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.BooleanOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

public class CompositeConstraint implements Constraint {
    private oSystem outerSystem;
    private Constraint lhs;
    private Constraint rhs;
    private BooleanOperator booleanOperator;
    private List<Quantifier> quantifierList;

    public CompositeConstraint() {
    }

    public CompositeConstraint(oSystem system, Constraint lhs, Constraint rhs, BooleanOperator booleanOperator,
                               List<Quantifier> quantifierList) {
        this.outerSystem = system;
        this.lhs = lhs;
        this.rhs = rhs;
        this.booleanOperator = booleanOperator;
        this.quantifierList = quantifierList;
    }

    public Constraint getLhs() {
        return lhs;
    }

    public void setLhs(Constraint lhs) {
        this.lhs = lhs;
    }

    public Constraint getRhs() {
        return rhs;
    }

    public void setRhs(Constraint rhs) {
        this.rhs = rhs;
    }

    public BooleanOperator getBooleanOperator() {
        return booleanOperator;
    }

    public void setBooleanOperator(BooleanOperator booleanOperator) {
        this.booleanOperator = booleanOperator;
    }

    @JsonIgnore
    public void setOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
    }

    @Override
    @JsonIgnore
    public oSystem getOuterSystem() {
        return outerSystem;
    }

    @JsonIgnore
    public void setQuantifierList(List<Quantifier> quantifierList) {
        this.quantifierList = quantifierList;
        lhs.setQuantifierList(quantifierList);
        rhs.setQuantifierList(quantifierList);
    }

    @JsonIgnore
    public List<Quantifier> getQuantifierList() {
        return quantifierList;
    }

    @Override
    @JsonIgnore
    public String getExpression() {
        return "(" + lhs.getExpression() + " " + booleanOperator.getSign() + " " + rhs.getExpression() + ")";
    }

    @Override
    @JsonIgnore
    public boolean isComposite() {
        return true;
    }

    @Override
    @JsonIgnore
    public void setEntitiesAndAttributesFromOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
        lhs.setEntitiesAndAttributesFromOuterSystem(outerSystem);
        rhs.setEntitiesAndAttributesFromOuterSystem(outerSystem);
    }

    @Override
    public boolean containsEntity(Entity entity) {
        return lhs.containsEntity(entity) || rhs.containsEntity(entity);
    }

    @Override
    public boolean containsAttribute(Attribute attribute) {
        return lhs.containsAttribute(attribute) || rhs.containsAttribute(attribute);
    }
}
