package tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.AddConstraintPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.EditCompositeConstraintPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.MakeCompositeConstraintLeftPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.MakeCompositeConstraintRightPopupController;
import tug.tobkul.ontologybrowser.ontology.model.constraint.CompositeConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CompositeConstraintView implements ConstraintView {
    private final HBox view;
    private CompositeConstraint constraint;
    private final AddConstraintPopupController controller;
    private ConstraintView leftView;
    private ConstraintView rightView;
    private CompositeConstraintView parent;
    private final List<Node> highlightGroup;

    private final Label openBracketLabel = new Label("(");
    private final Label closeBracketLabel = new Label(")");
    private final Label opLabel = new Label();

    public CompositeConstraintView(AddConstraintPopupController controller, CompositeConstraint constraint) {
        this.constraint = constraint;
        this.controller = controller;
        this.leftView = ConstraintViewFactory.create(controller, constraint.getLhs());
        this.rightView = ConstraintViewFactory.create(controller, constraint.getRhs());
        this.leftView.setParent(this);
        this.rightView.setParent(this);

        setOpLabel();

        String color = getRandomColor();
        openBracketLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        opLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        closeBracketLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        view = new HBox();
        view.setSpacing(5);
        view.setAlignment(Pos.CENTER);
        view.getChildren().addAll(
                openBracketLabel,
                leftView.getView(),
                opLabel,
                rightView.getView(),
                closeBracketLabel
        );

        highlightGroup = List.of(openBracketLabel, opLabel, closeBracketLabel);
        EventHandler<MouseEvent> highlightHandler = event -> {
            if (event.getButton() == MouseButton.SECONDARY || event.getButton() == MouseButton.PRIMARY) {
                HighlightManager.highlightGroup(highlightGroup, this);
            }
        };
        openBracketLabel.setOnMouseClicked(highlightHandler);
        opLabel.setOnMouseClicked(highlightHandler);
        closeBracketLabel.setOnMouseClicked(highlightHandler);
    }

    private void setOpLabel() {
        opLabel.setText(" " + constraint.getBooleanOperator().getSign() + " ");
    }

    @Override
    public Constraint getConstraint() {
        return new CompositeConstraint(constraint.getOuterSystem(), leftView.getConstraint(), rightView.getConstraint(), constraint.getBooleanOperator());
    }

    @Override
    public void setParent(CompositeConstraintView parent) {
        this.parent = parent;
    }

    @Override
    public void openEditPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(EditCompositeConstraintPopupController.class.getResource("editCompositeConstraintPopup.fxml"));
        Parent root = loader.load();

        EditCompositeConstraintPopupController controller = loader.getController();
        controller.setConstraintForEdit((CompositeConstraint) getConstraint());

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Edit Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));

        popupStage.showAndWait();

        if (controller.getResult() != null && !controller.wasReduced()) {
            this.constraint = (CompositeConstraint) controller.getResult();
            setOpLabel();
        } else if (controller.getResult() != null) {
            ConstraintView newView = ConstraintViewFactory.create(this.controller, controller.getResult());
            newView.setParent(parent);
            replaceInParent(view, newView);
            if (parent != null) {
                parent.replaceChild(this, newView);
            }
        }
    }

    @Override
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

    @Override
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

    @Override
    public Node getView() {
        return view;
    }

    public void replaceChild(ConstraintView oldChild, ConstraintView newChild) {
        int index = view.getChildren().indexOf(oldChild.getView());
        if (index == -1) return;

        if (oldChild == leftView) {
            leftView = newChild;
        } else if (oldChild == rightView) {
            rightView = newChild;
        }

        view.getChildren().set(index, newChild.getView());
    }

    private static String getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(180) + 70;
        int g = rand.nextInt(180) + 70;
        int b = rand.nextInt(180) + 70;
        return String.format("#%02x%02x%02x", r, g, b);
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
