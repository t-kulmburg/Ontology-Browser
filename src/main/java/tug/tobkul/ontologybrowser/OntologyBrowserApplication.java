package tug.tobkul.ontologybrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.OntologyBrowserController;

import java.io.IOException;
import java.util.Objects;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.Taskbar.Feature;

public class OntologyBrowserApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OntologyBrowserController.class.getResource("ontologyBrowser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
        stage.getIcons().add(icon);

        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(getClass().getResource("icon.png"));
                taskbar.setIconImage(dockIcon);
            }
        }

        OntologyBrowserController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            controller.showExitConfirmationPopup();
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main() {
        launch();
    }
}