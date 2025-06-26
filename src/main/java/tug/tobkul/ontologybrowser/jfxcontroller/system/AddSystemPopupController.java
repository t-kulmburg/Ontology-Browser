package tug.tobkul.ontologybrowser.jfxcontroller.system;

import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class AddSystemPopupController extends SystemPopupController {
    public void setOntologyManagerAndLibraries(OntologyManager ontologyManager) {
        libraryChoiceBox.getItems().setAll(ontologyManager.getLibraries());

    }

    public void setPreselectedLibrary(Library library) {
        libraryChoiceBox.getSelectionModel().select(library);
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (libraryChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Library missing!");
            invalidInputLabel.setVisible(true);
        } else if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
        } else {
            if (libraryChoiceBox.getValue().getSystems().stream().noneMatch(s -> s.getName().equals(nameField.getText()))) {
                libraryChoiceBox.getValue().getSystems().add(new oSystem(nameField.getText(), commentField.getText()));
                nameField.getScene().getWindow().hide();
            } else {
                invalidInputLabel.setText("Name must be unique!");
                invalidInputLabel.setVisible(true);
            }
        }
    }
}
