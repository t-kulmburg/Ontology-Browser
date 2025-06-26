package tug.tobkul.ontologybrowser.jfxcontroller.library;

import tug.tobkul.ontologybrowser.ontology.model.Library;

public class EditLibraryPopupController extends LibraryPopupController {
    private Library library;

    public void setLibrary(Library library) {
        this.library = library;
        nameField.setText(library.getName());
        commentField.setText(library.getComment());
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (nameField.getText().isEmpty()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
        } else {
            if (ontologyManager.getLibraries().stream().noneMatch(l -> l.getName().equals(nameField.getText()))
                    || library.getName().equals(nameField.getText())) {
                library.setName(nameField.getText());
                library.setComment(commentField.getText());
                nameField.getScene().getWindow().hide();
            } else {
                invalidInputLabel.setText("Name must be unique!");
                invalidInputLabel.setVisible(true);
            }
        }
    }
}
