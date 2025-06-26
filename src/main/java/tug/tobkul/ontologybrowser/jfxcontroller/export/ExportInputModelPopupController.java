package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.inputmodel.InputModelGenerator;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ExportInputModelPopupController {
    private Stage stage;

    @FXML
    private ChoiceBox<Library> libraryChoiceBox;

    @FXML
    private ChoiceBox<oSystem> systemChoiceBox;

    @FXML
    private TextField nameField;

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
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }

        InputModelGenerator inputModelGenerator = new InputModelGenerator(nameField.getText(), systemChoiceBox.getValue());

        long startTime = System.nanoTime();
        String model = inputModelGenerator.generate();
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1000000.0;
        if (model == null) {
            System.out.println("Could not generate inputModel");
            return;
        }


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt files", "*.txt"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }
            try {
                Files.write(selectedFile.toPath(), model.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                showErrorPopup("Error saving text file", null, e.getLocalizedMessage());
                return;
            }
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Information");
            info.setHeaderText("InputModel generated");
            String message = String.format("Root Entity:\t\t%s\nRuntime:\t\t%.2f ms (%d ns)", inputModelGenerator.getRootEntity().getName(), durationMs, durationNs);
            info.setContentText(message);
            info.showAndWait();
            stage.close();
        }
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }

    private void showErrorPopup(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
