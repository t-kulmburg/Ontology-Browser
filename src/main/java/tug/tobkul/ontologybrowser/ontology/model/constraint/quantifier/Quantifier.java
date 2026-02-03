package tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier;

import tug.tobkul.ontologybrowser.ontology.model.Relation;


public class Quantifier {

    private QuantifierType type;

    private Relation relation;

    public Quantifier() {
    }

    public Quantifier(QuantifierType type, Relation relation) {
        this.type = type;
        this.relation = relation;
    }

    public QuantifierType getType() {
        return type;
    }

    public void setType(QuantifierType type) {
        this.type = type;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return type.getSign() + " " + relation.getEntityString();
    }
}
