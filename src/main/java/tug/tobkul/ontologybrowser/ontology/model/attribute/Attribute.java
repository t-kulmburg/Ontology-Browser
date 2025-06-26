package tug.tobkul.ontologybrowser.ontology.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javafx.scene.text.TextFlow;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.StringFormatUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonPropertyOrder({"name", "comment", "type", "value"})
public class Attribute implements PdfContentProvider {
    private String name;
    private String comment;
    private AttributeType type;
    private AttributeValue<?> value;

    @Override
    public String toString() {
        return this.name;
    }

    public Attribute() {
    }

    public Attribute(String name, String comment, AttributeType type, AttributeValue<?> value) {
        this.name = name;
        this.comment = comment;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeType getType() {
        return this.type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    @JsonProperty("value")
    public Object getFlattenedValue() {
        return this.value.getValue();
    }

    @JsonProperty("value")
    public void setFlattenedValue(Object value) {
        switch (this.type) {
            case BOOL -> this.value = new BooleanValue((Boolean) value);
            case ENUM -> this.value = new EnumValue((List<String>) value);
            case INT -> {
                LinkedHashMap<String, Double> v = (LinkedHashMap<String, Double>) value;
                this.value = new NumberRangeValue(new NumberRange(v.get("min"), v.get("max"), v.get("interval")));
            }
        }
    }

    @JsonIgnore
    public AttributeValue<?> getValue() {
        return this.value;
    }

    @JsonIgnore
    public void setValue(AttributeValue<?> value) {
        this.value = value;
    }

    public void printDetails(TextFlow textFlow) {
        textFlow.getChildren().clear();
        textFlow.getChildren().add(StringFormatUtil.TYPE_HEADER);
        textFlow.getChildren().add(StringFormatUtil.indent(type.toString()));
        if (type == AttributeType.ENUM) {
            textFlow.getChildren().add(StringFormatUtil.POSSIBLE_VALUES_HEADER);
            EnumValue v = (EnumValue) value;
            for (String s : v.getValue()) {
                textFlow.getChildren().add(StringFormatUtil.indent(s));
            }
        } else if (type == AttributeType.INT) {
            NumberRangeValue v = (NumberRangeValue) value;
            textFlow.getChildren().add(StringFormatUtil.POSSIBLE_VALUES_HEADER);
            textFlow.getChildren().add(StringFormatUtil.indent("Min:\t" + v.value.getMin()));
            textFlow.getChildren().add(StringFormatUtil.indent("Max:\t" + v.value.getMax()));
            textFlow.getChildren().add(StringFormatUtil.indent("Interval:\t" + v.value.getInterval()));
        }
        if (!comment.isBlank()) {
            textFlow.getChildren().add(StringFormatUtil.COMMENT_HEADER);
            textFlow.getChildren().add(StringFormatUtil.indent(comment));
        }
        textFlow.autosize();
    }

    @JsonIgnore
    public List<String> getPdfStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("Name: " + this.name);
        if (this.comment != null && !this.comment.isBlank()) {
            strings.add("Comment: " + this.comment);
        }
        strings.add("Type: " + this.type);
        if (this.type.equals(AttributeType.ENUM)) {
            strings.add("Values:");
            for (String v : value.getPossibleValueList()) {
                strings.add("- " + v);
            }
        }

        return strings;
    }
}
