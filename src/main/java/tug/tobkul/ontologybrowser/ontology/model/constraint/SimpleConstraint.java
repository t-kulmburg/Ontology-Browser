package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.relational.RelationalOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class SimpleConstraint implements Constraint {
    private oSystem outerSystem;
    private Term lhs;
    private Term rhs;
    private RelationalOperator relationalOperator;

    public SimpleConstraint() {
    }

    public SimpleConstraint(oSystem system, Term lhs, Term rhs, RelationalOperator op) {
        this.outerSystem = system;
        this.lhs = lhs;
        this.relationalOperator = op;
        this.rhs = rhs;
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
    public void setOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
    }

    @JsonIgnore
    public oSystem getOuterSystem() {
        return outerSystem;
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
}
