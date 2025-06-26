package tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;

import javafx.scene.Node;

import java.util.List;

public class HighlightManager {
    private static List<Node> currentGroup = null;
    private static ConstraintView currentView = null;

    public static void highlightGroup(List<Node> nodes, ConstraintView view) {
        clearCurrent();
        nodes.forEach(n -> n.getStyleClass().add("highlighted"));
        currentGroup = nodes;
        currentView = view;
    }

    public static void highlight(Node node, ConstraintView view) {
        clearCurrent();
        node.getStyleClass().add("highlighted");
        currentGroup = List.of(node);
        currentView = view;
    }

    public static void clearCurrent() {
        if (currentGroup != null) {
            currentGroup.forEach(n -> n.getStyleClass().remove("highlighted"));
        }
        currentGroup = null;
        currentView = null;
    }

    public static ConstraintView getSelectedView() {
        return currentView;
    }
}
