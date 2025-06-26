package tug.tobkul.ontologybrowser.jfxcontroller.system;

import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class EditSystemPopupController extends SystemPopupController {
    private Library library;
    private oSystem system;

    public void setLibrary(Library library) {
        this.library = library;
        libraryChoiceBox.getItems().setAll(library);
        libraryChoiceBox.getSelectionModel().select(library);
        libraryChoiceBox.setDisable(true);
    }

    public void setSystem(oSystem system) {
        this.system = system;
        nameField.setText(system.getName());
        commentField.setText(system.getComment());
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
        } else {
            if (library.getSystems().stream().noneMatch(s -> s.getName().equals(nameField.getText()))
                    || system.getName().equals(nameField.getText())) {
                system.setName(nameField.getText());
                system.setComment(commentField.getText());
                nameField.getScene().getWindow().hide();
            } else {
                invalidInputLabel.setText("Name must be unique!");
                invalidInputLabel.setVisible(true);
            }
        }
    }
}
