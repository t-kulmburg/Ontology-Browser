package tug.tobkul.ontologybrowser.jfxcontroller.relation;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.ArrayList;

public class AddRelationPopupController extends RelationPopupController {
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
            entityAChoiceBox.getItems().setAll(new ArrayList<>());
            entityBChoiceBox.getItems().setAll(new ArrayList<>());
        });
        systemChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                entityAChoiceBox.getItems().setAll(newValue.getEntities());
                entityBChoiceBox.getItems().setAll(newValue.getEntities());
            } else {
                entityAChoiceBox.getItems().setAll(new ArrayList<>());
                entityBChoiceBox.getItems().setAll(new ArrayList<>());
            }
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

    public void addEntities(oSystem system) {
        if (ontologyManager != null) {
            entityAChoiceBox.getItems().setAll(system.getEntities());
            entityBChoiceBox.getItems().setAll(system.getEntities());
        }
    }

    public void onConfirm() {
        if (!checkInputFields()) {
            setInvalidInputLabelVisible();
            return;
        }
        if (systemChoiceBox.getValue().getRelations().stream().noneMatch(r -> r.getName().equals(nameField.getText()))) {
            systemChoiceBox.getValue().getRelations().add(new Relation(
                    systemChoiceBox.getValue(),
                    entityAChoiceBox.getValue(),
                    entityBChoiceBox.getValue(),
                    cardinalityAField.getText(),
                    cardinalityBField.getText(),
                    nameField.getText(),
                    commentField.getText()
            ));
            nameField.getScene().getWindow().hide();
            return;
        }
        invalidInputLabel.setText("Name must be unique!");
        setInvalidInputLabelVisible();
    }
}
