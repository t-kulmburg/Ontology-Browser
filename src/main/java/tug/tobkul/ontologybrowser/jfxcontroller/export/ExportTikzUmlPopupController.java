package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ExportTikzUmlPopupController extends ExportUmlPopupController{
    public void onConfirm() throws IOException {
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

        String umlString = systemChoiceBox.getValue().getTikzUmlString();

        FXMLLoader loader = new FXMLLoader(TikzUmlPopupController.class.getResource("tikzUmlPopup.fxml"));
        Parent root = loader.load();

        TikzUmlPopupController controller = loader.getController();
        controller.setUmlTextArea(umlString);

        Stage resultStage = new Stage();
        controller.setStage(resultStage);
        resultStage.setTitle("Tikz UML");
        resultStage.setResizable(false);
        resultStage.initModality(Modality.APPLICATION_MODAL);
        resultStage.setScene(new Scene(root));
        resultStage.showAndWait();
        stage.close();
    }
}
