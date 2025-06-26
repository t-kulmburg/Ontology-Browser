package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.security.SFile;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExportUmlPopupController {
    private Stage stage;

    @FXML
    private ChoiceBox<Library> libraryChoiceBox;

    @FXML
    private ChoiceBox<oSystem> systemChoiceBox;

    @FXML
    private Label invalidInputLabel;

    private OntologyManager ontologyManager;

    public void setOntologyManagerAndLibraries(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
        libraryChoiceBox.getItems().setAll(ontologyManager.getLibraries());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

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

    public void setPreselectedLibrary(Library library) {
        if (ontologyManager != null) {
            libraryChoiceBox.getSelectionModel().select(library);
        }
    }

    public void addSystemsAndSetPreselectedSystem(Library library, oSystem system, Entity entity) {
        if (ontologyManager != null) {
            systemChoiceBox.getItems().setAll(library.getSystems());
            systemChoiceBox.getSelectionModel().select(system);
        }
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (libraryChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Library missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        if (systemChoiceBox.getValue() == null) {
            invalidInputLabel.setText("System missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }

        String umlString = systemChoiceBox.getValue().getPlantUmlString();
        SourceStringReader reader = new SourceStringReader(umlString);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files", "*.png"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".png")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
            }
            try {
                reader.outputImage(SFile.fromFile(selectedFile));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error saving png file");
                alert.setHeaderText(null);
                alert.setContentText(e.getLocalizedMessage());
                alert.showAndWait();
                return;
            }
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Information");
            info.setHeaderText("UML Diagram generated");
            info.showAndWait();
            stage.close();
        }
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
