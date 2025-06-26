package tug.tobkul.ontologybrowser.ontology.model;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StringFormatUtil {
    public static final Text LIBRARY_HEADER = createBoldText("Library:\n");

    public static final Text SYSTEM_HEADER = createBoldText("System:\n");

    public static final Text ENTITY_HEADER = createBoldText("Entity:\n");
    public static final Text SUPER_ENTITY_HEADER = createBoldText("Super Entity:\n");
    public static final Text SUB_ENTITY_HEADER = createBoldText("Sub Entities:\n");

    public static final Text RELATION_HEADER = createBoldText("Relation:\n");
    public static final Text ENTITIES_HEADER = createBoldText("Entities:\n");
    public static final Text CARDINALITY_HEADER = createBoldText("Cardinality:\n");

    public static final Text CONSTRAINT_HEADER = createBoldText("Constraint:\n");
    public static final Text EXPRESSION_HEADER = createBoldText("Expression:\n");
    public static final Text TYPE_HEADER = createBoldText("Type:\n");

    public static final Text POSSIBLE_VALUES_HEADER = createBoldText("Possible Values:\n");

    public static final Text COMMENT_HEADER = createBoldText("Comment:\n");

    public static Text createBoldText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("System", FontWeight.BOLD, Font.getDefault().getSize())); // Set bold font
        return text;
    }

    public static Text indent(String content) {
        return new Text("\t" + content + "\n");
    }

    public static Text listIndent(String content) {
        return new Text("\t- " + content + "\n");
    }
}
