package tug.tobkul.ontologybrowser.jfxcontroller.system;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tug.tobkul.ontologybrowser.ontology.model.Library;

public abstract class SystemPopupController {
    @FXML
    protected ChoiceBox<Library> libraryChoiceBox;

    @FXML
    protected TextField nameField;

    @FXML
    protected TextArea commentField;

    @FXML
    protected Label invalidInputLabel;
}
