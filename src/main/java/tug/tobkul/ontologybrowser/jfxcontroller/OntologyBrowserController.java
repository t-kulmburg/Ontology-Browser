package tug.tobkul.ontologybrowser.jfxcontroller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import tug.tobkul.ontologybrowser.jfxcontroller.attribute.AddAttributePopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.attribute.EditAttributePopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.AddConstraintPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.entity.AddEntityPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.entity.EditEntityPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.entity.EntityPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.export.ExportInputModelPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.export.ExportUmlPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.library.AddLibraryPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.library.EditLibraryPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.library.LibraryPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.relation.AddRelationPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.relation.EditRelationPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.relation.RelationPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.system.AddSystemPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.system.EditSystemPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.system.SystemPopupController;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.PdfContentProvider;
import tug.tobkul.ontologybrowser.ontology.model.*;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.constraint.ConstraintHolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class OntologyBrowserController implements Initializable {
    private static final OntologyManager ontologyManager = new OntologyManager();
    private Stage stage;
    private AttributeHolder activeAttributeHolder = null;

    @FXML
    private TextFlow detailsTextFlow;

    @FXML
    private ListView<Library> libraryListView;

    @FXML
    private ListView<oSystem> systemListView;

    @FXML
    private ListView<Entity> entityListView;

    @FXML
    private ListView<Relation> relationListView;

    @FXML
    private ListView<ConstraintHolder> constraintListView;

    @FXML
    private HBox attributesHBox;

    @FXML
    private TextFlow attributesTextFlow;

    @FXML
    private ListView<Attribute> attributesListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initLibraryContextEvents();
        initSystemContextEvents();
        initEntityContextEvents();
        initRelationContextEvents();
        initConstraintContextEvents();
        initAttributeContextEvents();
    }

    private void initLibraryContextEvents() {
        ContextMenu libraryListViewContextMenu = new ContextMenu();

        MenuItem libraryListViewAddItem = new MenuItem("Add...");
        libraryListViewAddItem.setOnAction(event -> {
            try {
                onAddLibrary();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        libraryListViewContextMenu.getItems().add(libraryListViewAddItem);

        MenuItem libraryListViewEditItem = new MenuItem("Edit...");
        libraryListViewEditItem.setOnAction(event -> {
            try {
                onEditLibrary();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        libraryListViewContextMenu.getItems().add(libraryListViewEditItem);

        MenuItem libraryListViewDeleteItem = new MenuItem("Delete...");
        libraryListViewDeleteItem.setOnAction(event -> onDeleteLibrary());
        libraryListViewContextMenu.getItems().add(libraryListViewDeleteItem);

        libraryListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Library selectedItem = libraryListView.getSelectionModel().getSelectedItem();
            libraryListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && libraryListView.getSelectionModel().getSelectedItem() != null) {
                libraryListViewContextMenu.show(libraryListView, event.getScreenX(), event.getScreenY());
            } else {
                libraryListViewContextMenu.hide();
            }
        });
    }

    private void initSystemContextEvents() {
        ContextMenu systemListViewContextMenu = new ContextMenu();
        MenuItem systemListViewAddItem = new MenuItem("Add...");
        systemListViewAddItem.setOnAction(actionEvent -> {
            try {
                onAddSystem();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        systemListViewContextMenu.getItems().add(systemListViewAddItem);

        MenuItem systemListViewEditItem = new MenuItem("Edit...");
        systemListViewEditItem.setOnAction(actionEvent -> {
            try {
                onEditSystem();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        systemListViewContextMenu.getItems().add(systemListViewEditItem);

        MenuItem systemListViewExportPdfItem = new MenuItem("Export as PDF...");
        systemListViewExportPdfItem.setOnAction(event -> {
            try {
                onSaveAsPdf(systemListView.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        systemListViewContextMenu.getItems().add(systemListViewExportPdfItem);

        MenuItem systemListViewDeleteItem = new MenuItem("Delete...");
        systemListViewDeleteItem.setOnAction(event -> onDeleteSystem());
        systemListViewContextMenu.getItems().add(systemListViewDeleteItem);

        systemListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            oSystem selectedItem = systemListView.getSelectionModel().getSelectedItem();
            systemListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && systemListView.getSelectionModel().getSelectedItem() != null) {
                systemListViewContextMenu.show(systemListView, event.getScreenX(), event.getScreenY());
            } else {
                systemListViewContextMenu.hide();
            }
        });
    }

    private void initEntityContextEvents() {
        ContextMenu entityListViewContextMenu = new ContextMenu();

        MenuItem entityListViewAddItem = new MenuItem("Add...");
        entityListViewAddItem.setOnAction(actionEvent -> {
            try {
                onAddEntity();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        entityListViewContextMenu.getItems().add(entityListViewAddItem);

        MenuItem entityListViewEditItem = new MenuItem("Edit...");
        entityListViewEditItem.setOnAction(actionEvent -> {
            try {
                onEditEntity();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        entityListViewContextMenu.getItems().add(entityListViewEditItem);

        MenuItem entityListViewExportPdfItem = new MenuItem("Export as PDF...");
        entityListViewExportPdfItem.setOnAction(event -> {
            try {
                onSaveAsPdf(entityListView.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        entityListViewContextMenu.getItems().add(entityListViewExportPdfItem);

        MenuItem entityListViewDeleteItem = new MenuItem("Delete...");
        entityListViewDeleteItem.setOnAction(event -> onDeleteEntity());
        entityListViewContextMenu.getItems().add(entityListViewDeleteItem);

        entityListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Entity selectedItem = entityListView.getSelectionModel().getSelectedItem();
            entityListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && entityListView.getSelectionModel().getSelectedItem() != null) {
                entityListViewContextMenu.show(entityListView, event.getScreenX(), event.getScreenY());
            } else {
                entityListViewContextMenu.hide();
            }
        });
    }

    private void initRelationContextEvents() {
        ContextMenu relationListViewContextMenu = new ContextMenu();

        MenuItem relationListViewAddItem = new MenuItem("Add...");
        relationListViewAddItem.setOnAction(actionEvent -> {
            try {
                onAddRelation();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        relationListViewContextMenu.getItems().add(relationListViewAddItem);

        MenuItem relationListViewEditItem = new MenuItem("Edit...");
        relationListViewEditItem.setOnAction(actionEvent -> {
            try {
                onEditRelation();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        relationListViewContextMenu.getItems().add(relationListViewEditItem);

        MenuItem relationListViewExportPdfItem = new MenuItem("Export as PDF...");
        relationListViewExportPdfItem.setOnAction(event -> {
            try {
                onSaveAsPdf(relationListView.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        relationListViewContextMenu.getItems().add(relationListViewExportPdfItem);

        MenuItem relationListViewDeleteItem = new MenuItem("Delete...");
        relationListViewDeleteItem.setOnAction(event -> onDeleteRelation());
        relationListViewContextMenu.getItems().add(relationListViewDeleteItem);

        relationListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Relation selectedItem = relationListView.getSelectionModel().getSelectedItem();
            relationListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && relationListView.getSelectionModel().getSelectedItem() != null) {
                relationListViewContextMenu.show(relationListView, event.getScreenX(), event.getScreenY());
            } else {
                relationListViewContextMenu.hide();
            }
        });
    }

    private void initConstraintContextEvents() {
        ContextMenu constraintListViewContextMenu = new ContextMenu();
        MenuItem constraintListViewAddItem = new MenuItem("Add...");
        constraintListViewAddItem.setOnAction(actionEvent -> {
            try {
                onAddConstraint();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        constraintListViewContextMenu.getItems().add(constraintListViewAddItem);

        MenuItem constraintListViewEditItem = new MenuItem("Edit...");
        constraintListViewEditItem.setOnAction(actionEvent -> {
            try {
                onEditConstraint();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        constraintListViewContextMenu.getItems().add(constraintListViewEditItem);

        MenuItem constraintListViewExportPdfItem = new MenuItem("Export as PDF...");
        constraintListViewExportPdfItem.setOnAction(event -> {
            try {
                onSaveAsPdf(constraintListView.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        constraintListViewContextMenu.getItems().add(constraintListViewExportPdfItem);

        MenuItem constraintListViewDeleteItem = new MenuItem("Delete...");
        constraintListViewDeleteItem.setOnAction(event -> onDeleteConstraint());
        constraintListViewContextMenu.getItems().add(constraintListViewDeleteItem);

        constraintListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            ConstraintHolder selectedItem = constraintListView.getSelectionModel().getSelectedItem();
            constraintListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && constraintListView.getSelectionModel().getSelectedItem() != null) {
                constraintListViewContextMenu.show(constraintListView, event.getScreenX(), event.getScreenY());
            } else {
                constraintListViewContextMenu.hide();
            }
        });
    }

    private void initAttributeContextEvents() {
        ContextMenu attributeListViewContextMenu = new ContextMenu();
        MenuItem attributeListViewEditItem = new MenuItem("Edit...");
        attributeListViewEditItem.setOnAction(actionEvent -> {
            try {
                onEditAttribute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        attributeListViewContextMenu.getItems().add(attributeListViewEditItem);

        MenuItem attributeListViewDeleteItem = new MenuItem("Delete...");
        attributeListViewDeleteItem.setOnAction(actionEvent -> {
            onDeleteAttribute();
        });
        attributeListViewContextMenu.getItems().add(attributeListViewDeleteItem);

        attributesListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Attribute selectedItem = attributesListView.getSelectionModel().getSelectedItem();
            attributeListViewEvent(selectedItem);
            if (event.getButton() == MouseButton.SECONDARY && attributesListView.getSelectionModel().getSelectedItem() != null) {
                attributeListViewContextMenu.show(attributesListView, event.getScreenX(), event.getScreenY());
            } else {
                attributeListViewContextMenu.hide();
            }
        });
    }

    private void initializeListViews() {
        libraryListView.setItems(FXCollections.observableList(ontologyManager.getLibraries()));
        systemListView.setItems(null);
        entityListView.setItems(null);
        relationListView.setItems(null);
        constraintListView.setItems(null);
        attributesListView.setItems(null);
        activeAttributeHolder = null;
        detailsTextFlow.getChildren().clear();
        attributesTextFlow.getChildren().clear();
    }

    private void setTitle(String title) {
        stage.setTitle("OntologyBrowser - " + title);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setTitle(ontologyManager.getCurrentFileName());
    }

    private void showErrorPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showDeletionConfirmationPopup(String type, String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the " + type + " " + name + "?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void showExitConfirmationPopup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Do you want to save before exiting?");

        ButtonType saveAndExit = new ButtonType("Save");
        ButtonType exitWithoutSaving = new ButtonType("Don't save");
        ButtonType cancel = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(saveAndExit, exitWithoutSaving, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == saveAndExit) {
                onMenuFileSave();
                stage.close();
            } else if (result.get() == exitWithoutSaving) {
                stage.close();
            }
        }
    }

    private void libraryListViewEvent(Library newValue) {
        if (newValue != null) {
            systemListView.setItems(FXCollections.observableList(newValue.getSystems()));
            newValue.printDetails(detailsTextFlow);
            hideAttributePane();
        }
        entityListView.setItems(null);
        relationListView.setItems(null);
        constraintListView.setItems(null);
    }

    private void systemListViewEvent(oSystem newValue) {
        if (newValue != null) {
            entityListView.setItems(FXCollections.observableList(newValue.getEntities()));
            relationListView.setItems(FXCollections.observableList(newValue.getRelations()));
            constraintListView.setItems(FXCollections.observableList(newValue.getConstraints()));
            newValue.printDetails(detailsTextFlow);
            hideAttributePane();
        } else {
            entityListView.setItems(null);
            relationListView.setItems(null);
            constraintListView.setItems(null);
        }
    }

    private void entityListViewEvent(Entity newValue) {
        if (newValue != null) {
            relationListView.getSelectionModel().clearSelection();
            constraintListView.getSelectionModel().clearSelection();
            newValue.printDetails(detailsTextFlow);
            handleAttributes(newValue);
        }
    }

    private void relationListViewEvent(Relation newValue) {
        if (newValue != null) {
            entityListView.getSelectionModel().clearSelection();
            constraintListView.getSelectionModel().clearSelection();
            newValue.printDetails(detailsTextFlow);
            handleAttributes(newValue);
        }
    }

    private void constraintListViewEvent(ConstraintHolder newValue) {
        if (newValue != null) {
            attributesTextFlow.getChildren().clear();
            entityListView.getSelectionModel().clearSelection();
            relationListView.getSelectionModel().clearSelection();
            newValue.printDetails(detailsTextFlow);
            hideAttributePane();
        }
    }

    private void attributeListViewEvent(Attribute newValue) {
        if (newValue != null) {
            newValue.printDetails(attributesTextFlow);
        }
    }

    private void hideAttributePane() {
        attributesTextFlow.getChildren().clear();
        attributesHBox.setVisible(false);
        activeAttributeHolder = null;
    }

    private void handleAttributes(AttributeHolder attributeHolder) {
        attributesListView.setItems(FXCollections.observableList(attributeHolder.getAttributes()));
        attributesListView.getSelectionModel().clearSelection();
        attributesTextFlow.getChildren().clear();
        activeAttributeHolder = attributeHolder;
        attributesHBox.setVisible(true);
    }

    public void onAddLibrary() throws IOException {
        FXMLLoader loader = new FXMLLoader(LibraryPopupController.class.getResource("libraryPopup.fxml"));
        AddLibraryPopupController controller = new AddLibraryPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOntologyManager(ontologyManager);

        Stage stage = new Stage();
        stage.setTitle("Add Library");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        libraryListView.setItems(FXCollections.observableList(ontologyManager.getLibraries()));
    }

    public void onAddSystem() throws IOException {
        FXMLLoader loader = new FXMLLoader(SystemPopupController.class.getResource("systemPopup.fxml"));
        AddSystemPopupController controller = new AddSystemPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOntologyManagerAndLibraries(ontologyManager);
        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        stage.setTitle("Add System");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            systemListView.setItems(FXCollections.observableList(libraryListView.getSelectionModel().getSelectedItem().getSystems()));
        }
    }

    public void onAddEntity() throws IOException {
        FXMLLoader loader = new FXMLLoader(EntityPopupController.class.getResource("entityPopup.fxml"));
        AddEntityPopupController controller = new AddEntityPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOntologyManagerAndLibraries(ontologyManager);
        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        stage.setTitle("Add Entity");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));

        stage.showAndWait();

        if (systemListView.getSelectionModel().getSelectedItem() != null) {
            entityListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getEntities()));
        }
    }

    public void onAddRelation() throws IOException {
        FXMLLoader loader = new FXMLLoader(RelationPopupController.class.getResource("relationPopup.fxml"));
        AddRelationPopupController controller = new AddRelationPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOntologyManagerAndLibraries(ontologyManager);

        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem());
        }
        if (systemListView.getSelectionModel().getSelectedItem() != null) {
            controller.addEntities(systemListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        stage.setTitle("Add Relation");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));

        stage.showAndWait();

        if (systemListView.getSelectionModel().getSelectedItem() != null) {
            relationListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getRelations()));
        }
    }

    public void onAddConstraint() throws IOException {
        FXMLLoader loader = new FXMLLoader(AddConstraintPopupController.class.getResource("addConstraintPopup.fxml"));
        Parent root = loader.load();

        AddConstraintPopupController controller = loader.getController();
        controller.setOntologyManagerAndLibraries(ontologyManager);

        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Add Constraint");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(
                        AddConstraintPopupController.class.getResource("styles.css"))
                .toExternalForm());

        stage.showAndWait();

        if (systemListView.getSelectionModel().getSelectedItem() != null) {
            constraintListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getConstraints()));
        }
    }

    public void onAddAttribute() throws IOException {
        FXMLLoader loader = new FXMLLoader(AddAttributePopupController.class.getResource("attributePopup.fxml"));
        AddAttributePopupController controller = new AddAttributePopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setAttributeHolder(activeAttributeHolder);

        Stage stage = new Stage();
        stage.setTitle("Add Attribute");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        handleAttributes(activeAttributeHolder);
    }

    public void onMenuFileNew() {
        ontologyManager.closeFile();
        setTitle(ontologyManager.getCurrentFileName());
        initializeListViews();
    }

    public void onMenuFileOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Ontology JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                ontologyManager.openFile(selectedFile);
            } catch (JsonParseException e) {
                String message = "Error: The JSON structure in the file is invalid.\n" +
                        e.getLocalizedMessage() +
                        "Location: Line " + e.getLocation().getLineNr() + ", Column " + e.getLocation().getColumnNr();
                showErrorPopup("Error opening JSON file", message);
            } catch (JsonMappingException e) {
                String message = "Error: The file's structure doesn't match the expected format.\n" +
                        e.getMessage() +
                        "Location: Line " + e.getLocation().getLineNr() + ", Column " + e.getLocation().getColumnNr();
                showErrorPopup("Error opening JSON file", message);
            } catch (IOException e) {
                showErrorPopup("Error opening JSON file", e.getLocalizedMessage());
            }

            setTitle(selectedFile.getName());
        }
        initializeListViews();
    }

    public void onMenuFileImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Ontology JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                ontologyManager.importFile(selectedFile);
            } catch (JsonParseException e) {
                String message = "Error: The JSON structure in the file is invalid.\n" +
                        e.getLocalizedMessage() +
                        "Location: Line " + e.getLocation().getLineNr() + ", Column " + e.getLocation().getColumnNr();
                showErrorPopup("Error opening JSON file", message);
            } catch (JsonMappingException e) {
                String message = "Error: The file's structure doesn't match the expected format.\n" +
                        e.getOriginalMessage() +
                        "Location: Line " + e.getLocation().getLineNr() + ", Column " + e.getLocation().getColumnNr();
                showErrorPopup("Error opening JSON file", message);
            } catch (IOException e) {
                showErrorPopup("Error opening JSON file", e.getLocalizedMessage());
            }
        }
        initializeListViews();
    }

    public void onMenuFileSave() {
        if (ontologyManager.getCurrentFile() != null) {
            try {
                ontologyManager.saveFile();
            } catch (IOException e) {
                showErrorPopup("Error saving JSON file", e.getLocalizedMessage());
            }
        } else {
            onMenuFileSaveAs();
        }
    }

    public void onMenuFileSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".json")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
            }
            try {
                ontologyManager.saveFileAs(selectedFile);
            } catch (IOException e) {
                showErrorPopup("Error saving JSON file", e.getLocalizedMessage());
            }
            setTitle(selectedFile.getName());
        }
    }

    private void onEditLibrary() throws IOException {
        FXMLLoader loader = new FXMLLoader(LibraryPopupController.class.getResource("libraryPopup.fxml"));
        EditLibraryPopupController controller = new EditLibraryPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOntologyManager(ontologyManager);
        controller.setLibrary(libraryListView.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Edit Library");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        libraryListView.setItems(FXCollections.observableList(ontologyManager.getLibraries()));
        libraryListView.getSelectionModel().getSelectedItem().printDetails(detailsTextFlow);
    }

    private void onDeleteLibrary() {
        boolean result = showDeletionConfirmationPopup("Library", libraryListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            libraryListView.getItems().remove(libraryListView.getSelectionModel().getSelectedItem());
        }
    }

    public void onEditSystem() throws IOException {
        FXMLLoader loader = new FXMLLoader(SystemPopupController.class.getResource("systemPopup.fxml"));
        EditSystemPopupController controller = new EditSystemPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setLibrary(libraryListView.getSelectionModel().getSelectedItem());
        controller.setSystem(systemListView.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Edit System");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        systemListView.setItems(FXCollections.observableList(libraryListView.getSelectionModel().getSelectedItem().getSystems()));
        systemListView.getSelectionModel().getSelectedItem().printDetails(detailsTextFlow);
    }

    public void onDeleteSystem() {
        boolean result = showDeletionConfirmationPopup("System", systemListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            systemListView.getItems().remove(systemListView.getSelectionModel().getSelectedItem());
        }
    }

    public void onEditEntity() throws IOException {
        FXMLLoader loader = new FXMLLoader(EntityPopupController.class.getResource("entityPopup.fxml"));
        EditEntityPopupController controller = new EditEntityPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setLibrarySystemEntity(libraryListView.getSelectionModel().getSelectedItem(),
                systemListView.getSelectionModel().getSelectedItem(),
                entityListView.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Edit Entity");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        entityListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getEntities()));
        entityListView.getSelectionModel().getSelectedItem().printDetails(detailsTextFlow);
    }

    public void onDeleteEntity() {
        boolean result = showDeletionConfirmationPopup("Entity", entityListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            entityListView.getItems().remove(entityListView.getSelectionModel().getSelectedItem());
        }
    }

    public void onEditRelation() throws IOException {
        FXMLLoader loader = new FXMLLoader(RelationPopupController.class.getResource("relationPopup.fxml"));
        EditRelationPopupController controller = new EditRelationPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setLibrarySystemRelation(libraryListView.getSelectionModel().getSelectedItem(),
                systemListView.getSelectionModel().getSelectedItem(),
                relationListView.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Edit Relation");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        relationListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getRelations()));
        relationListView.getSelectionModel().getSelectedItem().printDetails(detailsTextFlow);
    }

    public void onDeleteRelation() {
        boolean result = showDeletionConfirmationPopup("Relation", relationListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            relationListView.getItems().remove(relationListView.getSelectionModel().getSelectedItem());
        }
    }

    public void onEditConstraint() throws IOException {
        FXMLLoader loader = new FXMLLoader(AddConstraintPopupController.class.getResource("addConstraintPopup.fxml"));
        Parent root = loader.load();

        AddConstraintPopupController controller = loader.getController();
        controller.setOntologyManagerAndLibraries(ontologyManager);

        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem());
        }
        controller.addConstrainHolderForEdit(constraintListView.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Edit Constraint");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(
                        AddConstraintPopupController.class.getResource("styles.css"))
                .toExternalForm());

        stage.showAndWait();

        constraintListView.setItems(FXCollections.observableList(systemListView.getSelectionModel().getSelectedItem().getConstraints()));
        constraintListView.getSelectionModel().getSelectedItem().printDetails(detailsTextFlow);
    }

    public void onDeleteConstraint() {
        boolean result = showDeletionConfirmationPopup("Constraint", constraintListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            constraintListView.getItems().remove(constraintListView.getSelectionModel().getSelectedItem());
        }
    }

    public void onEditAttribute() throws IOException {
        FXMLLoader loader = new FXMLLoader(EditAttributePopupController.class.getResource("attributePopup.fxml"));
        EditAttributePopupController controller = new EditAttributePopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setAttribute(attributesListView.getSelectionModel().getSelectedItem());
        controller.setAttributeHolder(activeAttributeHolder);

        Stage stage = new Stage();
        stage.setTitle("Edit Attribute");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        attributesListView.setItems(FXCollections.observableList(activeAttributeHolder.getAttributes()));
        attributesListView.getSelectionModel().getSelectedItem().printDetails(attributesTextFlow);
    }

    public void onDeleteAttribute() {
        boolean result = showDeletionConfirmationPopup("Attribute", attributesListView.getSelectionModel().getSelectedItem().getName());
        if (result) {
            attributesListView.getItems().remove(attributesListView.getSelectionModel().getSelectedItem());
        }
        attributesListView.getSelectionModel().clearSelection();
        attributesTextFlow.getChildren().clear();
    }

    public void onMenuExportInputModel() throws IOException {
        FXMLLoader loader = new FXMLLoader(ExportInputModelPopupController.class.getResource("exportInputModelPopup.fxml"));
        Parent root = loader.load();

        ExportInputModelPopupController controller = loader.getController();
        controller.setOntologyManagerAndLibraries(ontologyManager);

        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem(),
                    entityListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Export InputModel");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public void onMenuExportUml() throws IOException {
        FXMLLoader loader = new FXMLLoader(ExportUmlPopupController.class.getResource("exportUmlPopup.fxml"));
        Parent root = loader.load();

        ExportUmlPopupController controller = loader.getController();
        controller.setOntologyManagerAndLibraries(ontologyManager);

        if (libraryListView.getSelectionModel().getSelectedItem() != null) {
            controller.setPreselectedLibrary(libraryListView.getSelectionModel().getSelectedItem());
            controller.addSystemsAndSetPreselectedSystem(
                    libraryListView.getSelectionModel().getSelectedItem(),
                    systemListView.getSelectionModel().getSelectedItem(),
                    entityListView.getSelectionModel().getSelectedItem());
        }

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Export UML Diagram");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public void onSaveAsPdf(PdfContentProvider provider) throws IOException {
        List<String> strings = new ArrayList<>();
        String c = provider.getClass().getSimpleName();
        switch (c) {
            case "ConstraintHolder" -> c = "Constraint";
            case "oSystem" -> c = "System";
        }

        strings.add(c);
        strings.addAll(provider.getPdfStrings());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("pdf files", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }
            PDDocument pdf = new PDDocument();
            PDPage page;
            PDPageContentStream contentStream = null;
            float fontSize = 12;
            int lineCharLimit = 68;
            int pageLineLimit = 41;
            int lineCounter = 0;

            for (String str : strings) {
                List<String> substr = splitLine(str, lineCharLimit);
                for (String s : substr) {
                    if (lineCounter % pageLineLimit == 0) {
                        if (contentStream != null) {
                            contentStream.endText();
                            contentStream.close();
                        }
                        page = new PDPage(PDRectangle.A4);
                        pdf.addPage(page);
                        contentStream = new PDPageContentStream(pdf, page);
                        contentStream.setLeading(fontSize * 1.5f);
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), fontSize);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, PDRectangle.A4.getHeight() - 60);
                    }
                    contentStream.showText(s);
                    contentStream.newLine();
                    lineCounter++;
                }
            }
            if (contentStream != null) {
                contentStream.endText();
                contentStream.close();
            }
            pdf.save(selectedFile.getAbsolutePath());
        }
    }

    private static List<String> splitLine(String str, int limit) {
        List<String> substr = new ArrayList<>();
        int start = 0;
        while (start < str.length()) {
            int end = Math.min(start + limit, str.length());
            int split = end;

            if (end < str.length()) {
                int lastSpace = str.lastIndexOf(' ', end - 1);
                if (lastSpace > start) {
                    split = lastSpace;
                }
            }
            if (start == 0) {
                substr.add(str.substring(start, split));
            } else {
                substr.add(str.substring(start, split).trim());
            }
            start = split;
        }
        return substr;
    }
}