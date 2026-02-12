package tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier;

import tug.tobkul.ontologybrowser.ontology.model.Relation;


public class Quantifier {

    private QuantifierType type;

    private Relation relation;

    private String identifier;

    public Quantifier() {
    }

    public Quantifier(QuantifierType type, Relation relation, String identifier) {
        this.type = type;
        this.relation = relation;
        this.identifier = identifier;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return type.getSign() + identifier + "∈" + relation.getEntityString();
    }
}
