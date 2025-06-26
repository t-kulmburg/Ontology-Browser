package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.ontology.model.constraint.CompositeConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.BooleanOperator;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.IOException;

public abstract class MakeCompositeConstraintPopupController {
    protected Stage stage;
    protected oSystem outerSystem;
    protected Constraint lhs;
    protected Constraint rhs;

    protected CompositeConstraint result;

    @FXML
    protected ChoiceBox<BooleanOperator> booleanOperatorChoiceBox;
    @FXML
    protected TextFlow lhsTextFlow;
    @FXML
    protected TextFlow rhsTextFlow;
    @FXML
    protected Label invalidInputLabel;

    @FXML
    public void initialize() {
        booleanOperatorChoiceBox.getItems().addAll(BooleanOperator.values());
        lhsTextFlow.setTextAlignment(TextAlignment.CENTER);
        rhsTextFlow.setTextAlignment(TextAlignment.CENTER);
    }

    public abstract void setExistingConstraint(Constraint c);

    @FXML
    public abstract void onAddConstraint() throws IOException;

    public CompositeConstraint getResult() {
        return result;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (lhs == null || rhs == null) {
            invalidInputLabel.setText("Term missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        if (booleanOperatorChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Operator missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        this.result = new CompositeConstraint(outerSystem, lhs, rhs, booleanOperatorChoiceBox.getValue());
        stage.close();
    }

    protected Constraint getSimpleConstraintFromPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(SimpleConstraintPopupController.class.getResource("simpleConstraintPopup.fxml"));
        AddSimpleConstraintPopupController controller = new AddSimpleConstraintPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOuterSystem(outerSystem);

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Add Simple Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));
        popupStage.setX(stage.getX() + 20);
        popupStage.setY(stage.getY() + 20);

        popupStage.showAndWait();

        return controller.getResult();
    }

    private void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
