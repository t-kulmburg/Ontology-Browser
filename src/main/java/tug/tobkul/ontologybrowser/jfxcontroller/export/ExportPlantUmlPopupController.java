package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.security.SFile;

import java.io.File;
import java.io.IOException;

public class ExportPlantUmlPopupController extends ExportUmlPopupController {

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
}
