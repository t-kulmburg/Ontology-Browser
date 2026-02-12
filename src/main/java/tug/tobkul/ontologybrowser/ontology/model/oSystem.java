package tug.tobkul.ontologybrowser.ontology.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.text.TextFlow;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.graph.EntityGraphUtil;
import tug.tobkul.ontologybrowser.ontology.model.constraint.ConstraintHolder;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class oSystem implements PdfContentProvider {
    private String name;
    private String comment;
    private List<Entity> entities;
    private List<Relation> relations;
    private List<ConstraintHolder> constraintHolderList;

    private final int tikzXOffset = 4;
    private final int tikzYOffset = 3;

    public oSystem() {
    }

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

    public void setRelations(List<Relation> relations) {
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

    public void printDetails(TextFlow textFlow) {
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.SYSTEM_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(name));
        textFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(comment));
        textFlow.autosize();
    }

    @JsonIgnore
    public String getPlantUmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@startuml\n").append("!pragma layout smetana\n");
        for (Entity e : entities) {
            builder.append(e.getPlantUmlString());
        }
        for (Entity e : entities) {
            String i = e.getPlantUmlStringInheritance();
            if (i != null) builder.append(i);
        }
        for (Relation r : relations) {
            builder.append(r.getPlantUmlString());
        }
        return builder.append("@enduml\n").toString();
    }

    @JsonIgnore
    public String getTikzUmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\\begin{figure}%[t]\n").append("\\begin{footnotesize}\n").append("\\begin{center}\n")
                .append("\\begin{tikzpicture}[scale=1.0]\n");
        for (Entity e : entities) {
            builder.append(e.getTikzUmlString());
        }
        for (Relation r : relations) {
            builder.append(r.getTikzUmlString());
        }
        builder.append("\\end{tikzpicture}\n").append("\\end{center}\n").append("\\end{footnotesize}\n")
                .append("\\caption{Add caption}\n").append("\\label{fig:label}\n").append("\\end{figure}");

        String umlString = builder.toString();

        DefaultDirectedGraph<Entity, DefaultEdge> graph = EntityGraphUtil.buildGraph(this);
        Optional<Entity> root = EntityGraphUtil.findRootEntity(graph);
        if (root.isEmpty()) {
            return "Error: Could not determine root entity";
        }
        List<List<Entity>> entityMap = EntityGraphUtil.traverseGraph(graph, root.get());

        int y = 0;
        for (List<Entity> level : entityMap) {
            int x = 0;
            for (Entity e : level) {
                String name = "{" + e.getName() + "}";
                String from = name + "{@coords@}";
                String to = name + "{" + tikzXOffset * x + ",-" + tikzYOffset * y + "}";
                umlString = umlString.replace(from, to);
                x++;
            }
            y++;
        }


        return umlString;
    }

    @JsonIgnore
    public List<String> getPdfStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if (this.comment != null && !this.comment.isBlank()) {
            strings.add("Comment: " + this.comment);
        }
        if (this.entities != null && !this.entities.isEmpty()) {
            strings.add("Entities:");
            for (Entity e : entities) {
                List<String> s = e.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size()).mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Entities: []");
        }
        if (this.relations != null && !this.relations.isEmpty()) {
            strings.add("Relations:");
            for (Relation r : relations) {
                List<String> s = r.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size()).mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Relations: []");
        }
        if (this.constraintHolderList != null && !this.constraintHolderList.isEmpty()) {
            strings.add("Constraints:");
            for (ConstraintHolder c : constraintHolderList) {
                List<String> s = c.getPdfStrings();
                List<String> temp = IntStream.range(0, s.size()).mapToObj(i -> (i == 0 ? "- " : "  ") + s.get(i))
                        .toList();
                strings.addAll(temp);
            }
        } else {
            strings.add("Constraints: []");
        }

        return strings;
    }

    @JsonIgnore
    public Entity getRootEntity() {
        if (entities.isEmpty()) return null;
        if (entities.size() == 1) return entities.getFirst();

        DefaultDirectedGraph<Entity, DefaultEdge> graph = EntityGraphUtil.buildGraph(this);
        Set<Entity> cycle = EntityGraphUtil.detectCycle(graph);
        if (cycle != null) {
            throw new IllegalStateException(String.join(" - ", cycle.stream().map(Entity::getName).toList()));
        }
        Set<Entity> orphans = EntityGraphUtil.detectOrphans(graph);
        if (orphans != null) {
            System.out.println("orphans found");
            System.out.println(orphans);
        }
        Optional<Entity> root = EntityGraphUtil.findRootEntity(graph);
        if (root.isEmpty()) {
            throw new RuntimeException();
        }
        return root.get();
    }

    @JsonIgnore
    public List<Parameter> getParameterListForConstraint(List<Quantifier> quantifiers) {
        List<Parameter> parameters = getRootEntity().getParameterListForConstraint();

        if (quantifiers.isEmpty()) {
            return parameters;
        }

        Set<Parameter> result = new LinkedHashSet<>();
        Set<Parameter> resultNoQuant = new LinkedHashSet<>();
        for (Quantifier q : quantifiers) {
            String regex = q.getRelation().getEntityA().getName() + "_\\d+_" + q.getRelation().getEntityB().getName();
            String replacement =
                    q.getRelation().getEntityA().getName() + "_" + q.getType().getSign() + q.getIdentifier() + "_" +
                            q.getRelation().getEntityB().getName();
            Pattern pattern = Pattern.compile(regex);
            for (Parameter p : parameters) {
                Matcher matcher = pattern.matcher(p.getExpression());
                if (matcher.find()) {
                    result.add(new Parameter(p.getEntity(), p.getAttribute(), matcher.replaceAll(replacement)));
                } else {
                    resultNoQuant.add(p);
                }
            }
        }
        result.addAll(resultNoQuant);
        return result.stream().toList();
    }
}
