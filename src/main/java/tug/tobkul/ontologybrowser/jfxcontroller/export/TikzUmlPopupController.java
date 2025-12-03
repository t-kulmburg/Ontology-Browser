package tug.tobkul.ontologybrowser.jfxcontroller.export;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TikzUmlPopupController {
    private Stage stage;
    @FXML
    private TextArea umlTextArea;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUmlTextArea(String umlString) {
        umlTextArea.setText(umlString);
    }

    public void onConfirm() {
        stage.close();
    }
}
