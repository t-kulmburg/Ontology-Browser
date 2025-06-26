package tug.tobkul.ontologybrowser.ontology.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.text.TextFlow;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.constraint.ConstraintHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class oSystem implements PdfContentProvider {
    private String name;
    private String comment;
    private List<Entity> entities;
    private List<Relation> relations;
    private List<ConstraintHolder> constraintHolderList;

    public oSystem() {}

    public oSystem(String name, String comment) {
        this.name = name;
        this.comment = comment;
        this.entities = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.constraintHolderList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
        entities.forEach(e -> e.setOuterSystem(this));
    }

    public List<Relation> getRelations() {
        return this.relations;
    }

    public void setRelations(List<Relation> relations){
        this.relations = relations;
        relations.forEach(r -> r.setOuterSystem(this));
    }

    public List<ConstraintHolder> getConstraints() {
        return this.constraintHolderList;
    }

    public void setConstraints(List<ConstraintHolder> constraintHolderList) {
        this.constraintHolderList = constraintHolderList;
        constraintHolderList.forEach(c -> c.setOuterSystem(this));
    }

    public void printDetails(TextFlow textFlow){
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.SYSTEM_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(name));
        textFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(comment));
        textFlow.autosize();
    }

    @JsonIgnore
    public String getPlantUmlString(){
        StringBuilder builder = new StringBuilder();
        builder.append("@startuml\n").append("!pragma layout smetana\n");
        for (Entity e : entities){
            builder.append(e.getPlantUmlString());
        }
        for (Entity e : entities){
            String i = e.getPlantUmlStringInheritance();
            if(i != null) builder.append(i);
        }
        for (Relation r : relations){
            builder.append(r.getPlantUmlString());
        }
        return builder.append("@enduml\n").toString();
    }

    @JsonIgnore
    public List<String> getPdfStrings(){
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if(this.comment != null && !this.comment.isBlank()){
            strings.add("Comment: " + this.comment);
        }
        if(this.entities != null && !this.entities.isEmpty()){
            strings.add("Entities:");
            for(Entity e : entities){
                List<String> s = e.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size())
                        .mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Entities: []");
        }
        if(this.relations != null && !this.relations.isEmpty()){
            strings.add("Relations:");
            for(Relation r : relations){
                List<String> s = r.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size())
                        .mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Relations: []");
        }
        if(this.constraintHolderList != null && !this.constraintHolderList.isEmpty()){
            strings.add("Constraints:");
            for(ConstraintHolder c : constraintHolderList){
                List<String> s = c.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size())
                        .mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Constraints: []");
        }

        return strings;
    }
}
