package tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.*;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;

import java.io.IOException;

public class SimpleConstraintView implements ConstraintView {
    private SimpleConstraint constraint;
    private final HBox view;
    private final AddConstraintPopupController controller;
    private CompositeConstraintView parent;

    public SimpleConstraintView(AddConstraintPopupController controller, SimpleConstraint constraint) {
        this.constraint = constraint;
        this.controller = controller;
        this.view = new HBox();
        view.setAlignment(Pos.CENTER);
        view.setSpacing(5);
        view.setOnMouseClicked(event -> HighlightManager.highlight(view, this));
        setLabel();

    }

    private void setLabel() {
        Label label = new Label(constraint.getExpression().replace(" ", "\n"));
        label.setTextAlignment(TextAlignment.CENTER);
        this.view.getChildren().clear();
        this.view.getChildren().add(label);
    }

    @Override
    public Constraint getConstraint() {
        return constraint;
    }

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setParent(CompositeConstraintView parent) {
        this.parent = parent;
    }


    public void openEditPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(SimpleConstraintPopupController.class.getResource("simpleConstraintPopup.fxml"));
        EditSimpleConstraintPopupController controller = new EditSimpleConstraintPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOuterSystem(constraint.getOuterSystem());
        controller.setConstraintForEdit(constraint);

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Edit Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));

        popupStage.showAndWait();

        if (controller.getResult() != null) {
            this.constraint = controller.getResult();
            setLabel();
        }
    }

    public void makeCompositeRight() throws IOException {
        FXMLLoader loader = new FXMLLoader(MakeCompositeConstraintRightPopupController.class.getResource("makeCompositeConstraintRightPopup.fxml"));
        Parent root = loader.load();

        MakeCompositeConstraintRightPopupController controller = loader.getController();
        controller.setOuterSystem(constraint.getOuterSystem());
        controller.setExistingConstraint(constraint);

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Make Composite Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));

        popupStage.showAndWait();

        if (controller.getResult() != null) {
            ConstraintView newView = ConstraintViewFactory.create(this.controller, controller.getResult());
            newView.setParent(parent);
            replaceInParent(view, newView);
            if (parent != null) {
                parent.replaceChild(this, newView);
            }
        }
    }

    public void makeCompositeLeft() throws IOException {
        FXMLLoader loader = new FXMLLoader(MakeCompositeConstraintLeftPopupController.class.getResource("makeCompositeConstraintLeftPopup.fxml"));
        Parent root = loader.load();

        MakeCompositeConstraintLeftPopupController controller = loader.getController();
        controller.setOuterSystem(constraint.getOuterSystem());
        controller.setExistingConstraint(constraint);

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Make Composite Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));

        popupStage.showAndWait();

        if (controller.getResult() != null) {
            ConstraintView newView = ConstraintViewFactory.create(this.controller, controller.getResult());
            newView.setParent(parent);
            replaceInParent(view, newView);
            if (parent != null) {
                parent.replaceChild(this, newView);
            }
        }
    }

    private void replaceInParent(Node oldView, ConstraintView newView) {
        if (!(oldView.getParent() instanceof Pane parent)) throw new RuntimeException();

        int index = parent.getChildrenUnmodifiable().indexOf(oldView);
        if (index == -1) throw new RuntimeException();

        Platform.runLater(() -> {
            parent.getChildren().remove(index);
            parent.getChildren().add(index, newView.getView());

            if (controller.rootConstraintView.get().getView() == oldView) {
                controller.rootConstraintView.set(newView);
            }
        });
    }
}
