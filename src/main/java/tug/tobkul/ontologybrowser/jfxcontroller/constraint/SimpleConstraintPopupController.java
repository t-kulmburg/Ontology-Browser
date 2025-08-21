package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.term.AddTermPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.term.EditTermPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.term.TermPopupController;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.relational.RelationalOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ArithmeticParameterTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ArithmeticValueTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.ParameterTerm;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.Term;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.IOException;

public abstract class SimpleConstraintPopupController {
    protected Stage stage;
    protected oSystem outerSystem;
    protected Term lhs;
    protected Term rhs;
    protected SimpleConstraint result;
    protected AttributeType attributeType;

    @FXML
    protected ChoiceBox<RelationalOperator> relationalOperatorChoiceBox;

    @FXML
    protected TextFlow lhsTextFlow;
    @FXML
    protected TextFlow rhsTextFlow;

    @FXML
    protected Label attributeTypeLabel;
    @FXML
    protected Label invalidInputLabel;

    @FXML
    public void initialize() {
        // lhs
        ContextMenu lhsTextFlowContextMenu = new ContextMenu();
        MenuItem lhsTextFlowEditItem = new MenuItem("Edit...");
        lhsTextFlowEditItem.setOnAction(actionEvent -> {
            try {
                onEditTermA();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem lhsTextFlowClearItem = new MenuItem("Clear");
        lhsTextFlowClearItem.setOnAction(actionEvent -> onClearTermA());
        lhsTextFlowContextMenu.getItems().addAll(lhsTextFlowEditItem, lhsTextFlowClearItem);
        lhsTextFlow.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                lhsTextFlowContextMenu.show(lhsTextFlow, event.getScreenX(), event.getScreenY());
            } else {
                lhsTextFlowContextMenu.hide();
            }
        });
        // rhs
        ContextMenu rhsTextFlowContextMenu = new ContextMenu();
        MenuItem rhsTextFlowEditItem = new MenuItem("Edit...");
        rhsTextFlowEditItem.setOnAction(actionEvent -> {
            try {
                onEditTermB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem rhsTextFlowClearItem = new MenuItem("Clear");
        rhsTextFlowClearItem.setOnAction(actionEvent -> onClearTermB());
        rhsTextFlowContextMenu.getItems().addAll(rhsTextFlowEditItem, rhsTextFlowClearItem);
        rhsTextFlow.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                rhsTextFlowContextMenu.show(rhsTextFlow, event.getScreenX(), event.getScreenY());
            } else {
                rhsTextFlowContextMenu.hide();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOuterSystem(oSystem outerSystem) {
        this.outerSystem = outerSystem;
    }

    public SimpleConstraint getResult() {
        return result;
    }

    public void onAddTermA() throws IOException {
        Term oldTerm = lhs;
        lhs = getTermFromPopup(true, null);
        if (oldTerm != null && lhs == null) {
            lhs = oldTerm;
            return;
        }
        lhsTextFlow.getChildren().clear();
        if (lhs != null) {
            setRelationalOperatorChoiceBoxFromTerm(lhs);
            lhsTextFlow.getChildren().add(new Text(lhs.getExpression()));
        }
    }

    public void onAddTermB() throws IOException {
        Term oldTerm = rhs;
        rhs = getTermFromPopup(false, null);
        if (oldTerm != null && rhs == null) {
            rhs = oldTerm;
            return;
        }
        rhsTextFlow.getChildren().clear();
        if (rhs != null) {
            setRelationalOperatorChoiceBoxFromTerm(rhs);
            rhsTextFlow.getChildren().add(new Text(rhs.getExpression()));
        }
    }

    private void onEditTermA() throws IOException {
        if (lhs == null) {
            return;
        }
        Term oldTerm = lhs;
        lhs = getTermFromPopup(true, lhs);
        if (oldTerm != null && lhs == null) {
            lhs = oldTerm;
            return;
        }
        lhsTextFlow.getChildren().clear();
        if (lhs != null) {
            setRelationalOperatorChoiceBoxFromTerm(lhs);
            lhsTextFlow.getChildren().add(new Text(lhs.getExpression()));
        }
    }

    private void onEditTermB() throws IOException {
        if (rhs == null) {
            return;
        }
        Term oldTerm = rhs;
        rhs = getTermFromPopup(false, rhs);
        if (oldTerm != null && rhs == null) {
            rhs = oldTerm;
            return;
        }
        rhsTextFlow.getChildren().clear();
        if (rhs != null) {
            setRelationalOperatorChoiceBoxFromTerm(rhs);
            rhsTextFlow.getChildren().add(new Text(rhs.getExpression()));
        }
    }

    private void onClearTermA() {
        lhs = null;
        lhsTextFlow.getChildren().clear();
        if (rhs == null) {
            relationalOperatorChoiceBox.getItems().clear();
            attributeType = null;
            attributeTypeLabel.setText("None");
        }
    }

    private void onClearTermB() {
        rhs = null;
        rhsTextFlow.getChildren().clear();
        if (lhs == null) {
            relationalOperatorChoiceBox.getItems().clear();
            attributeType = null;
            attributeTypeLabel.setText("None");
        }
    }

    private Term getTermFromPopup(boolean isLhs, Term currentTerm) throws IOException {
        FXMLLoader loader = new FXMLLoader(TermPopupController.class.getResource("termPopup.fxml"));
        TermPopupController controller;
        if (currentTerm != null) {
            controller = new EditTermPopupController();
        } else {
            controller = new AddTermPopupController();
        }
        loader.setController(controller);
        Parent root = loader.load();

        if (isLhs && this.rhs != null) {
            controller.setAttributeTypeFilter(this.attributeType);
        } else if (!isLhs && this.lhs != null) {
            controller.setAttributeTypeFilter(this.attributeType);
        }
        controller.setEntitiesFromOuterSystem(outerSystem);
        if (currentTerm != null) {
            ((EditTermPopupController) controller).setTermForEdit(currentTerm);
        }

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        if((currentTerm != null)){
            popupStage.setTitle("Edit Term");
        } else {
            popupStage.setTitle("Add Term");
        }
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));
        popupStage.setX(stage.getX() + 20);
        popupStage.setY(stage.getY() + 20);

        popupStage.showAndWait();

        return controller.getResult();
    }

    protected void setRelationalOperatorChoiceBoxFromTerm(Term term) {
        if (term instanceof ParameterTerm) {
            attributeType = ((ParameterTerm) term).getParameter().getType();
        } else if (term instanceof ArithmeticParameterTerm) {
            attributeType = ((ArithmeticParameterTerm) term).getLhs().getType();
        } else if (term instanceof ArithmeticValueTerm) {
            attributeType = ((ArithmeticValueTerm) term).getLhs().getType();
        }
        if (attributeType != null) {
            attributeTypeLabel.setText(attributeType.name());
            setRelationalOperatorChoiceBoxFromAttributeType(attributeType);
        }
    }

    private void setRelationalOperatorChoiceBoxFromAttributeType(AttributeType type) {
        RelationalOperator prev = relationalOperatorChoiceBox.getSelectionModel().getSelectedItem();
        relationalOperatorChoiceBox.getItems().clear();
        relationalOperatorChoiceBox.getItems().setAll(RelationalOperator.getOperators(type));
        relationalOperatorChoiceBox.getSelectionModel().select(prev);
    }

    protected void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
