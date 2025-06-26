package tug.tobkul.ontologybrowser.jfxcontroller.library;

import tug.tobkul.ontologybrowser.ontology.model.Library;

public class AddLibraryPopupController extends LibraryPopupController {
    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (nameField.getText().isEmpty()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
        } else {
            if (ontologyManager.getLibraries().stream().noneMatch(l -> l.getName().equals(nameField.getText()))) {
                ontologyManager.addLibrary(new Library(nameField.getText(), commentField.getText()));
                nameField.getScene().getWindow().hide();
            } else {
                invalidInputLabel.setText("Name must be unique!");
                invalidInputLabel.setVisible(true);
            }
        }
    }
}
