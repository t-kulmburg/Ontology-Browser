package tug.tobkul.ontologybrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.OntologyBrowserController;

import java.io.IOException;

public class OntologyBrowserApplication extends Application {

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OntologyBrowserController.class.getResource("ontologyBrowser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
//        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
//        stage.getIcons().add(icon);
//
//        if (Taskbar.isTaskbarSupported()) {
//            var taskbar = Taskbar.getTaskbar();
//            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
//                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//                var dockIcon = defaultToolkit.getImage(getClass().getResource("icon.png"));
//                taskbar.setIconImage(dockIcon);
//            }
//        }

        OntologyBrowserController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            boolean cancelled;
            try {
                cancelled = controller.showSaveConfirmationWasCancelled("Exit");
            } catch (IOException e) {
                throw new RuntimeException("Error checking for unsaved changes: " + e);
            }
            if (!cancelled) {
                stage.close();
            }
        });

        stage.setScene(scene);
        stage.show();
    }
}