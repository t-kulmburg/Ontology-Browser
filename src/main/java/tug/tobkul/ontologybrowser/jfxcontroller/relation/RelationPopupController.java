package tug.tobkul.ontologybrowser.jfxcontroller.relation;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public abstract class RelationPopupController {
    @FXML
    protected ChoiceBox<Library> libraryChoiceBox;

    @FXML
    protected ChoiceBox<oSystem> systemChoiceBox;

    @FXML
    protected ChoiceBox<Entity> entityAChoiceBox;

    @FXML
    protected ChoiceBox<Entity> entityBChoiceBox;

    @FXML
    protected TextField cardinalityAField;

    @FXML
    protected TextField cardinalityBField;

    @FXML
    protected TextField nameField;

    @FXML
    protected TextArea commentField;

    @FXML
    protected Label invalidInputLabel;

    protected boolean checkInputFields() {
        invalidInputLabel.setVisible(false);
        if (libraryChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Library Missing!");
            return false;
        } else if (systemChoiceBox.getValue() == null) {
            invalidInputLabel.setText("System Missing!");
            return false;
        } else if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name Missing!");
            return false;
        } else if (entityAChoiceBox.getValue() == null || entityBChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Entity Missing!");
            return false;
        } else if (cardinalityAField.getText() == null || cardinalityBField.getText() == null) {
            invalidInputLabel.setText("Cardinality Missing!");
            return false;
        } else if (!cardinalityAField.getText().matches("\\d+") || !cardinalityBField.getText().matches("\\d+")) {
            invalidInputLabel.setText("Cardinalities must be numerical!");
            return false;
        } else if (entityAChoiceBox.getValue() == entityBChoiceBox.getValue()) {
            invalidInputLabel.setText("Entities must differ!");
            return false;
        }
        return true;
    }

    protected void setInvalidInputLabelVisible() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
