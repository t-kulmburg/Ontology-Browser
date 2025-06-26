package tug.tobkul.ontologybrowser.jfxcontroller.entity;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.ArrayList;

public class AddEntityPopupController extends EntityPopupController {
    private OntologyManager ontologyManager;

    @FXML
    public void initialize() {
        initSuperEntityComboBox();

        libraryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                systemChoiceBox.getItems().setAll(FXCollections.observableList(newValue.getSystems()));
            } else {
                systemChoiceBox.getItems().setAll(new ArrayList<>());
            }
            systemChoiceBox.getSelectionModel().select(null);
            superEntityCheckComboBox.getCheckModel().clearChecks();
            superEntityCheckComboBox.getItems().setAll(new ArrayList<>());
        });
    }

    public void setOntologyManagerAndLibraries(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
        libraryChoiceBox.getItems().setAll(ontologyManager.getLibraries());
    }

    public void setPreselectedLibrary(Library library) {
        if (ontologyManager != null) {
            libraryChoiceBox.getSelectionModel().select(library);
        }
    }

    public void addSystemsAndSetPreselectedSystem(Library library, oSystem system) {
        if (ontologyManager != null) {
            systemChoiceBox.getItems().setAll(library.getSystems());
            systemChoiceBox.getSelectionModel().select(system);
            if (system != null) {
                superEntityCheckComboBox.getItems().setAll(system.getEntities());
            }
        }
    }

    public void onConfirm() {
        if (!checkComboBoxes()) {
            return;
        }

        if (systemChoiceBox.getValue().getEntities().stream().noneMatch(entity -> entity.getName().equals(nameField.getText()))) {
            Entity newEntity = new Entity(nameField.getText(), commentField.getText(),
                    superEntityCheckComboBox.getCheckModel().getCheckedItems().isEmpty() ? null : superEntityCheckComboBox.getCheckModel().getCheckedItems().getFirst());
            systemChoiceBox.getValue().getEntities().add(newEntity);
            if (!superEntityCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                superEntityCheckComboBox.getCheckModel().getCheckedItems().getFirst().addSubEntity(newEntity);
            }

            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }
}
