package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Scenario;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.ArrayList;

public class AddScenarioPopupController extends ScenarioPopupController {
    private OntologyManager ontologyManager;

    @FXML
    public void initialize() {
        libraryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                systemChoiceBox.getItems().setAll(FXCollections.observableList(newValue.getSystems()));
            } else {
                systemChoiceBox.getItems().setAll(new ArrayList<>());
            }
            systemChoiceBox.getSelectionModel().select(null);
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
        }
    }

    public void onConfirm() {
        if (!checkComboBoxes()) {
            return;
        }

        if (systemChoiceBox.getValue().getScenarios().stream()
                .noneMatch(scenario -> scenario.getName().equals(nameField.getText()))) {
            Scenario newScenario = new Scenario(nameField.getText(), commentField.getText());
            systemChoiceBox.getValue().getScenarios().add(newScenario);
            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }

}
