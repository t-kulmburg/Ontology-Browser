package tug.tobkul.ontologybrowser.ontology.model.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.text.TextFlow;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.StringFormatUtil;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.ArrayList;
import java.util.List;

public class ConstraintHolder implements PdfContentProvider {
    @JsonIgnore
    private oSystem outerSystem;

    private String name;
    private String comment;

    @JsonIgnore
    private String expressionFromJson;

    private Constraint constraint;

    public ConstraintHolder() {
    }

    public ConstraintHolder(String name, String comment, Constraint constraint) {
        this.name = name;
        this.comment = comment;
        this.constraint = constraint;
    }

    @JsonIgnore
    public void setOuterSystem(oSystem system) {
        this.outerSystem = system;
        constraint.setEntitiesAndAttributesFromOuterSystem(system);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public void printDetails(TextFlow detailsTextFlow) {
        detailsTextFlow.getChildren().clear();
        detailsTextFlow.getChildren().add(StringFormatUtil.CONSTRAINT_HEADER);
        detailsTextFlow.getChildren().add(StringFormatUtil.indent(name));
        detailsTextFlow.getChildren().add(StringFormatUtil.EXPRESSION_HEADER);
        detailsTextFlow.getChildren().add(StringFormatUtil.indent(constraint.getExpression()));

        if (!comment.isBlank()) {
            detailsTextFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
            detailsTextFlow.getChildren().add(StringFormatUtil.indent(comment));
        }
        detailsTextFlow.autosize();
    }

    @JsonIgnore
    public List<String> getPdfStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if (this.comment != null && !this.comment.isBlank()) {
            strings.add("Comment: " + this.comment);
        }
        strings.add("Expression: " + this.constraint.getExpression());
        return strings;
    }
}
