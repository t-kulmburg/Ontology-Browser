package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import tug.tobkul.ontologybrowser.ontology.graph.GraphMLBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ExportGraphMLUmlPopupController extends ExportUmlPopupController {
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
        GraphMLBuilder builder;
        String uml;
        try {
            builder = new GraphMLBuilder(systemChoiceBox.getValue());
            uml = builder.build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            invalidInputLabel.setText("Could not determine root!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("graphml files", "*.graphml"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".graphml")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".graphml");
            }
            try {
                Files.write(selectedFile.toPath(), uml.getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                showErrorPopup("Error saving graphml file", null, e.getLocalizedMessage());
                return;
            }

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Information");
            info.setHeaderText("GraphML file generated");
            info.setContentText("Note:\nThe positioning and sizing of the nodes is done with constant values. Manual " +
                    "adaptation of width or coordinates may be required!");
            info.showAndWait();
            stage.close();
        }
    }

    private void showErrorPopup(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
