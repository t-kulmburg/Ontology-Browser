package tug.tobkul.ontologybrowser.jfxcontroller.library;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;

public abstract class LibraryPopupController {
    protected OntologyManager ontologyManager;

    @FXML
    protected TextField nameField;

    @FXML
    protected TextArea commentField;

    @FXML
    protected Label invalidInputLabel;

    public void setOntologyManager(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }
}
