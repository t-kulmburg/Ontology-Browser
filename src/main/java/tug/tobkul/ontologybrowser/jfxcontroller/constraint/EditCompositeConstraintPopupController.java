package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.ontology.model.constraint.CompositeConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.BooleanOperator;

public class EditCompositeConstraintPopupController {
    private Stage stage;
    private CompositeConstraint constraint;
    private Constraint lhs;
    private Constraint rhs;

    private Constraint result;
    private boolean wasReduced = false;

    @FXML
    private ChoiceBox<BooleanOperator> booleanOperatorChoiceBox;
    @FXML
    private TextFlow lhsTextFlow;
    @FXML
    private TextFlow rhsTextFlow;
    @FXML
    private CheckBox keepOnlyACheckBox;
    @FXML
    private CheckBox keepOnlyBCheckBox;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setConstraintForEdit(CompositeConstraint constraint) {
        this.constraint = constraint;
        result = constraint;
        booleanOperatorChoiceBox.getSelectionModel().select(constraint.getBooleanOperator());

        lhs = constraint.getLhs();
        lhsTextFlow.getChildren().clear();
        if (lhs != null) {
            lhsTextFlow.getChildren().add(new Text(lhs.getExpression().replace(" ", "\n")));
        }
        rhs = constraint.getRhs();
        rhsTextFlow.getChildren().clear();
        if (rhs != null) {
            rhsTextFlow.getChildren().add(new Text(rhs.getExpression().replace(" ", "\n")));
        }
    }

    @FXML
    public void initialize() {
        booleanOperatorChoiceBox.getItems().addAll(BooleanOperator.values());
        lhsTextFlow.setTextAlignment(TextAlignment.CENTER);
        rhsTextFlow.setTextAlignment(TextAlignment.CENTER);

        keepOnlyACheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                keepOnlyBCheckBox.setSelected(false);
            }
        });
        keepOnlyBCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                keepOnlyACheckBox.setSelected(false);
            }
        });
    }

    public void onConfirm() {
        if (keepOnlyACheckBox.isSelected()) {
            result = lhs;
            wasReduced = true;
        } else if (keepOnlyBCheckBox.isSelected()) {
            result = rhs;
            wasReduced = true;
        } else {
            constraint.setBooleanOperator(booleanOperatorChoiceBox.getValue());
            result = constraint;
        }
        stage.close();
    }

    public boolean wasReduced() {
        return wasReduced;
    }

    public Constraint getResult() {
        return result;
    }
}
