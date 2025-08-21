package tug.tobkul.ontologybrowser.ontology.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.text.TextFlow;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Entity implements AttributeHolder, PdfContentProvider {
    @JsonIgnore
    private oSystem outerSystem;

    private String name;
    private String comment;
    private List<Attribute> attributes;

    @JsonIgnore
    private Entity superEntity;
    @JsonIgnore
    private String superEntityName;
    @JsonIgnore
    private ArrayList<Entity> subEntities;
    @JsonIgnore
    private List<String> subEntityNames;

    public Entity() {
    }

    public Entity(String name, String comment, Entity superEntity) {
        this.name = name;
        this.comment = comment;
        this.superEntity = superEntity;
        this.subEntities = new ArrayList<>();
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("super_entity")
    public void setSuperEntityName(String superEntityName) {
        this.superEntityName = superEntityName;
    }

    @JsonProperty("super_entity")
    public String getSuperEntityName() {
        return this.superEntity == null ? "" : this.superEntity.getName();
    }

    @JsonProperty("sub_entities")
    public void setSubEntityNames(List<String> subEntityNames) {
        this.subEntityNames = subEntityNames;
    }

    @JsonProperty("sub_entities")
    public List<String> getSubEntityNames() {
        return this.subEntities.stream().map(Entity::getName).toList();
    }

    @JsonIgnore
    public void setSuperEntity(Entity superEntity) {
        this.superEntity = superEntity;
    }

    @JsonIgnore
    public Entity getSuperEntity() {
        return this.superEntity;
    }

    @JsonIgnore
    public void setSubEntities(ArrayList<Entity> subEntities) {
        this.subEntities = subEntities;
    }

    @JsonIgnore
    public List<Entity> getSubEntities() {
        return this.subEntities;
    }

    public void printDetails(TextFlow textFlow) {
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.ENTITY_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(name));
        textFlow.getChildren().add(StringFormatUtil.SUPER_ENTITY_HEADER);
        if (superEntity != null) {
            textFlow.getChildren().add(StringFormatUtil.indent(superEntity.getName()));

        } else {
            textFlow.getChildren().add(StringFormatUtil.indent("None"));
        }
        textFlow.getChildren().add(StringFormatUtil.SUB_ENTITY_HEADER);
        for (Entity sub_entity : subEntities) {
            textFlow.getChildren().add(StringFormatUtil.listIndent(sub_entity.getName()));
        }
        textFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(comment));
        textFlow.autosize();
    }

    @JsonIgnore
    public void setOuterSystem(oSystem system) {
        this.outerSystem = system;
        setEntitiesFromOuterSystem();
    }

    @JsonIgnore
    public void setEntitiesFromOuterSystem() {
        if (this.outerSystem != null) {
            if (!superEntityName.isEmpty()) {
                this.superEntity = outerSystem.getEntities().stream().filter(e -> e.getName().equals(superEntityName)).toList().getFirst();

            }
            this.subEntities = new ArrayList<>(outerSystem.getEntities().stream().filter(e -> subEntityNames.contains(e.getName())).toList());
        }
    }

    @Override
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void addSubEntity(Entity entity) {
        subEntities.add(entity);
    }

    public void removeSubEntity(Entity entity) {
        subEntities.remove(entity);
    }

    @JsonIgnore
    public String getPlantUmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("entity ")
                .append(this.name.replace(" ", "_"))
                .append(" {\n");
        for (Attribute a : attributes) {
            builder.append(a.getName().replace(" ", "_"))
                    .append(" : ")
                    .append(a.getType())
                    .append("\n");
        }
        builder.append("}\n");

        return builder.toString();
    }

    @JsonIgnore
    public String getPlantUmlStringInheritance() {
        if (superEntity == null) return null;
        String builder = superEntity.getName().replace(" ", "_") +
                " <|-- " +
                name.replace(" ", "_") +
                "\n";
        return builder;
    }

    @JsonIgnore
    public List<String> getPdfStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if (this.comment != null && !this.comment.isBlank()) {
            strings.add("Comment: " + this.comment);
        }
        if (this.superEntity != null) {
            strings.add("Super Entity: " + superEntity.getName());
        }
        if (this.subEntities != null && !this.subEntities.isEmpty()) {
            strings.add("Sub Entities: ");
            for (Entity subEntity : subEntities) {
                strings.add("- " + subEntity.getName());
            }
        }
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
