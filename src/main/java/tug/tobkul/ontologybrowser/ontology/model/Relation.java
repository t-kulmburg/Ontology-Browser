package tug.tobkul.ontologybrowser.ontology.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class Relation implements AttributeHolder, PdfContentProvider {
    @JsonIgnore
    private oSystem outerSystem;

    private String name;
    private String comment;

    @JsonIgnore
    private String cardinalityMin;

    @JsonIgnore
    private String cardinalityMax;

    @JsonIgnore
    private Entity A;
    @JsonIgnore
    private String AName;
    @JsonIgnore
    private Entity B;
    @JsonIgnore
    private String BName;

    private List<Attribute> attributes;

    public Relation() {
    }

    public Relation(oSystem outerSystem, Entity A, Entity B, String cardMin, String cardMax, String name, String comment) {
        this.outerSystem = outerSystem;
        this.A = A;
        this.B = B;
        this.cardinalityMin = cardMin;
        this.cardinalityMax = cardMax;
        this.name = name;
        this.comment = comment;
        this.attributes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Pair<Entity, Entity> getEntities() {
        return new Pair<>(this.A, this.B);
    }

    @JsonProperty("entities")
    public void setEntitiesFromJson(List<String> entities) {
        if (entities == null || entities.size() != 2) {
            throw new IllegalArgumentException("Entities list must contain exactly two entries");
        }
        AName = entities.getFirst();
        BName = entities.getLast();
    }

    @JsonProperty("entities")
    public List<String> getEntitiesForJson() {
        return Arrays.asList(A.getName(), B.getName());
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("cardinalities")
    public String getCardinalitiesForJson() {
        return cardinalityMin + ":" + cardinalityMax;
    }

    @JsonProperty("cardinalities")
    public void setCardinalitiesFromJson(String cardinalities) {
        String[] split = cardinalities.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Cardinalities must contain exactly two entries, split by ':'");
        }
        cardinalityMin = split[0].trim();
        cardinalityMax = split[1].trim();
    }

    @JsonIgnore
    public void setOuterSystem(oSystem system) {
        this.outerSystem = system;
        setEntitiesFromOuterSystem();
    }

    @JsonIgnore
    public void setEntitiesFromOuterSystem() {
        if (this.outerSystem != null) {
            List<Entity> listA = outerSystem.getEntities().stream().filter(e -> e.getName().equals(AName)).toList();
            List<Entity> listB = outerSystem.getEntities().stream().filter(e -> e.getName().equals(BName)).toList();
            if (listA.size() != listB.size() && listA.size() != 1) {
                throw new IllegalArgumentException("Entities list contains entities that do not match present entities of this System");
            }
            A = listA.getFirst();
            B = listB.getFirst();
        }
    }

    public void printDetails(TextFlow textFlow) {
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.RELATION_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(name));
        textFlow.getChildren().add(StringFormatUtil.ENTITIES_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(A.getName() + " : " + B.getName()));
        textFlow.getChildren().add(StringFormatUtil.CARDINALITY_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(!getCardinalitiesForJson().contains("null") ?
                getCardinalitiesForJson().replace(":", " : ") :
                "undefined"
        ));
        textFlow.autosize();
    }

    @Override
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    @JsonIgnore
    public Entity getEntityA() {
        return A;
    }

    @JsonIgnore
    public void setEntityA(Entity entity) {
        A = entity;
    }

    @JsonIgnore
    public Entity getEntityB() {
        return B;
    }

    @JsonIgnore
    public void setEntityB(Entity entity) {
        B = entity;
    }

    @JsonIgnore
    public String getCardinalityMin() {
        return cardinalityMin;
    }

    @JsonIgnore
    public void setCardinalityMin(String cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
    }

    @JsonIgnore
    public String getCardinalityMax() {
        return cardinalityMax;
    }

    @JsonIgnore
    public void setCardinalityMax(String cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
    }

    @JsonIgnore
    public String getPlantUmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append(A.getName().replace(" ", "_"))
                .append(" o-- ")
                .append("\"");
        if (cardinalityMin.equals(cardinalityMax)) {
            builder.append(cardinalityMax);
        } else {
            builder.append(cardinalityMin)
                    .append("..")
                    .append(cardinalityMax);
        }
        builder.append("\" ")
                .append(B.getName().replace(" ", "_"))
                .append(" : ")
                .append(name.replace(" ", "_"))
                .append("\n");
        return builder.toString();
    }

    @JsonIgnore
    public List<String> getPdfStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if (this.comment != null && !this.comment.isBlank()) {
            strings.add("Comment: " + this.comment);
        }
        strings.add("Entities: " + A.getName() + ":" + B.getName());
        strings.add("Cardinality: " + cardinalityMin + ":" + cardinalityMax);
        if (this.attributes != null && !this.attributes.isEmpty()) {
            strings.add("Attributes: ");
            for (Attribute a : attributes) {
                List<String> s = a.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size())
                        .mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        }
        return strings;
    }
}
