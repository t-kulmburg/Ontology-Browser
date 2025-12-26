package tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;

public class QuantifierView extends HBox {

    private Quantifier quantifier;

    public QuantifierView(Quantifier quantifier, Runnable onDelete) {
        this.quantifier = quantifier;

        getChildren().add(new Label(createLabelText()));
        setAlignment(Pos.CENTER);
        setPickOnBounds(true);
        getStyleClass().add("quantifier");

        setOnDragDetected(e -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("quantifier");
            db.setContent(content);
            db.setDragView(snapshot(null, null), e.getX(), e.getY());

            setOpacity(0.4);
            e.consume();
        });

        setOnDragOver(e -> {
            if (e.getGestureSource() == this) return;
            if (e.getGestureSource() instanceof QuantifierView) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        setOnDragDropped(e -> {
            if (!(e.getGestureSource() instanceof QuantifierView source)) return;

            HBox parent = (HBox) getParent();
            ObservableList<Node> children = parent.getChildren();

            int sourceIndex = children.indexOf(source);
            int targetIndex = children.indexOf(this);

            boolean insertBefore = e.getX() < getWidth() / 2;

            children.remove(source);

            if (sourceIndex < targetIndex) {
                targetIndex--;
            }
            if (!insertBefore) {
                targetIndex++;
            }

            targetIndex = Math.max(0, Math.min(targetIndex, children.size()));
            children.add(targetIndex, source);

            e.setDropCompleted(true);
            e.consume();
        });

        setOnDragDone(e -> {
            setOpacity(1.0);
            e.consume();
        });

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            if (getParent() instanceof HBox parent){
                parent.getChildren().remove(this);
            }
            if (onDelete != null){
                onDelete.run();
            }
        });

        ContextMenu contextMenu = new ContextMenu(deleteItem);
        setOnContextMenuRequested(e ->
                contextMenu.show(this, e.getScreenX(), e.getScreenY())
        );
    }

    private String createLabelText() {
        return switch (quantifier.getType()) {
            case FOR_ALL -> "∀ " + quantifier.getEntity().getName();
            case EXISTS -> "∃ " + quantifier.getEntity().getName();
        };
    }
}
