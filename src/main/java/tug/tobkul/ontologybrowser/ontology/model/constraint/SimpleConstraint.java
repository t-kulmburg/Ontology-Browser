package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.relational.RelationalOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

public class SimpleConstraint implements Constraint {
    private oSystem outerSystem;
    private Term lhs;
    private Term rhs;
    private RelationalOperator relationalOperator;
    private List<Quantifier> quantifierList;

    public SimpleConstraint() {
    }

    public SimpleConstraint(oSystem system, Term lhs, Term rhs, RelationalOperator op,
                            List<Quantifier> quantifierList) {
        this.outerSystem = system;
        this.lhs = lhs;
        this.relationalOperator = op;
        this.rhs = rhs;
        this.quantifierList = quantifierList;
    }

    public Term getLhs() {
        return lhs;
    }

    public void setLhs(Term lhs) {
        this.lhs = lhs;
    }

    public Term getRhs() {
        return rhs;
    }

    public void setRhs(Term rhs) {
        this.rhs = rhs;
    }

    public RelationalOperator getRelationalOperator() {
        return relationalOperator;
    }

    public void setRelationalOperator(RelationalOperator relationalOperator) {
        this.relationalOperator = relationalOperator;
    }

    @JsonIgnore
    public oSystem getOuterSystem() {
        return outerSystem;
    }

    @JsonIgnore
    public void setOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
    }

    @JsonIgnore
    public List<Quantifier> getQuantifierList() {
        return quantifierList;
    }

    @JsonIgnore
    public void setQuantifierList(List<Quantifier> quantifierList) {
        this.quantifierList = quantifierList;
    }

    @Override
    @JsonIgnore
    public boolean isComposite() {
        return false;
    }

    @Override
    @JsonIgnore
    public String getExpression() {
        return lhs.getExpression() + " " + relationalOperator.getSign() + " " + rhs.getExpression();
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
