package tug.tobkul.ontologybrowser.ontology.model;

import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private String name;
    private String comment;
    private List<oSystem> systems;

    public Library() {
    }

    public Library(String name, String comment) {
        this.name = name;
        this.comment = comment;
        this.systems = new ArrayList<>();
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<oSystem> getSystems() {
        return this.systems;
    }

    public void setSystems(List<oSystem> systems) {
        this.systems = systems;
    }

    public void printDetails(TextFlow textFlow) {
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.LIBRARY_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(name));
        textFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(comment));
        textFlow.autosize();
    }
}
