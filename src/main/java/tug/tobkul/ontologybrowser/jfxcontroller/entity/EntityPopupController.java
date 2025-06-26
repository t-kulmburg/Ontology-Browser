package tug.tobkul.ontologybrowser.jfxcontroller.entity;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public abstract class EntityPopupController {
    @FXML
    protected ChoiceBox<Library> libraryChoiceBox;

    @FXML
    protected ChoiceBox<oSystem> systemChoiceBox;

    @FXML
    protected CheckComboBox<Entity> superEntityCheckComboBox;

    @FXML
    protected TextField nameField;

    @FXML
    protected TextArea commentField;

    @FXML
    protected Label invalidInputLabel;

    protected void initSuperEntityComboBox() {
        superEntityCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Entity>) change -> {
            while (change.next()) {
                if (change.getList().size() > 1 && !change.getAddedSubList().isEmpty()) {
                    Entity checkedEntity = change.getAddedSubList().getFirst();
                    superEntityCheckComboBox.getCheckModel().clearChecks();
                    superEntityCheckComboBox.getCheckModel().check(checkedEntity);
                }
            }
        });
    }

    protected boolean checkComboBoxes() {
        invalidInputLabel.setVisible(false);
        if (libraryChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Library missing!");
            setInvalidInputLabelVisibleAndFormat();
            return false;
        }
        if (systemChoiceBox.getValue() == null) {
            invalidInputLabel.setText("System missing!");
            setInvalidInputLabelVisibleAndFormat();
            return false;
        }
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            setInvalidInputLabelVisibleAndFormat();
            return false;
        }
        return true;
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
