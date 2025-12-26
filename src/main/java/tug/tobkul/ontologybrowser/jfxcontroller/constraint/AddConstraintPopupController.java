package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier.AddQuantifierPopupController;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier.view.QuantifierView;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.view.ConstraintView;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.view.ConstraintViewFactory;
import tug.tobkul.ontologybrowser.jfxcontroller.constraint.view.HighlightManager;
import tug.tobkul.ontologybrowser.ontology.OntologyManager;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.ConstraintHolder;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.QuantifierType;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddConstraintPopupController {
    private Stage stage;
    private OntologyManager ontologyManager;
    private Constraint createdConstraint;
    private ConstraintHolder editConstraintHolder = null;
    private List<Entity> quantifierUsedEntities = new ArrayList<>();
    private List<Quantifier> quantifiers = new ArrayList<>();

    public final ObjectProperty<ConstraintView> rootConstraintView = new SimpleObjectProperty<>();

    @FXML
    private ChoiceBox<Library> libraryChoiceBox;
    @FXML
    private ChoiceBox<oSystem> systemChoiceBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentField;

    @FXML
    private HBox constraintHBox;

    @FXML
    private HBox quantifierHBox;

    @FXML
    private Label invalidInputLabel;

    @FXML
    private Button newRootButton;

    @FXML
    private Button addQuantifierForAllButton;

    @FXML
    private Button addQuantifierExistsButton;

    @FXML
    private void initialize() {
        libraryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                systemChoiceBox.getItems().setAll(FXCollections.observableList(newValue.getSystems()));
            } else {
                systemChoiceBox.getItems().setAll(new ArrayList<>());
            }
            systemChoiceBox.getSelectionModel().select(null);
        });
        HighlightManager.clearCurrent();
    }

    @FXML
    private void onNewRootConstraint() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("simpleConstraintPopup.fxml"));
        AddSimpleConstraintPopupController controller = new AddSimpleConstraintPopupController();
        loader.setController(controller);

        Parent root = loader.load();
        controller.setOuterSystem(systemChoiceBox.getValue());

        Stage popupStage = new Stage();
        controller.setStage(popupStage);
        popupStage.setTitle("Add Simple Constraint");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));
        popupStage.setX(stage.getX() + 20);
        popupStage.setY(stage.getY() + 20);

        popupStage.showAndWait();
        createdConstraint = controller.getResult();
        if (createdConstraint != null) {
            ConstraintView rootView = ConstraintViewFactory.create(this, createdConstraint);
            rootConstraintView.set(rootView);
            constraintHBox.getChildren().clear();
            constraintHBox.getChildren().add(rootView.getView());
            newRootButton.setDisable(true);
            addQuantifierExistsButton.setDisable(true);
            addQuantifierForAllButton.setDisable(true);
        }
    }

    @FXML
    private void onEdit() throws IOException {
        ConstraintView selected = HighlightManager.getSelectedView();
        if (selected != null) {
            selected.openEditPopup();
        }
    }

    @FXML
    private void onExtendLeft() throws IOException {
        ConstraintView selected = HighlightManager.getSelectedView();
        if (selected != null) {
            selected.makeCompositeLeft();
        }
    }

    @FXML
    private void onExtendRight() throws IOException {
        ConstraintView selected = HighlightManager.getSelectedView();
        if (selected != null) {
            selected.makeCompositeRight();
        }
    }

    @FXML
    private void onConfirm() {
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
        if (systemChoiceBox.getValue().getConstraints().stream().noneMatch(c -> c.getName().equals(nameField.getText()))
                || (editConstraintHolder != null && editConstraintHolder.getName().equals(nameField.getText()))) {
            if (editConstraintHolder == null) {
                systemChoiceBox.getValue().getConstraints().add(new ConstraintHolder(
                        nameField.getText(),
                        commentField.getText(),
                        rootConstraintView.get().getConstraint(),
                        quantifiers
                ));
            } else {
                editConstraintHolder.setName(nameField.getText());
                editConstraintHolder.setComment(commentField.getText());
                editConstraintHolder.setConstraint(rootConstraintView.get().getConstraint());
                editConstraintHolder.setQuantifiers(quantifiers);
            }
            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }

    @FXML
    private void onAddForAll() throws IOException {
        Entity selectedEntity = getEntityFromQuantifierPopup();
        if(selectedEntity == null){
            return;
        }
        Quantifier quantifier = new Quantifier(QuantifierType.FOR_ALL, selectedEntity);
        quantifiers.add(quantifier);
        quantifierHBox.getChildren().add(new QuantifierView(quantifier, () -> {
            quantifiers.remove(quantifier);
            quantifierUsedEntities.remove(quantifier.getEntity());
        }));
        quantifierUsedEntities.add(selectedEntity);
    }

    @FXML
    private void onAddExists() throws IOException {
        Entity selectedEntity = getEntityFromQuantifierPopup();
        if(selectedEntity == null){
            return;
        }

        Quantifier quantifier = new Quantifier(QuantifierType.EXISTS, selectedEntity);
        quantifiers.add(quantifier);
        quantifierHBox.getChildren().add(new QuantifierView(quantifier, () -> {
            quantifiers.remove(quantifier);
            quantifierUsedEntities.remove(quantifier.getEntity());
        }));
        quantifierUsedEntities.add(selectedEntity);
    }

    private Entity getEntityFromQuantifierPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addQuantifierPopup.fxml"));
        Parent root = loader.load();

        AddQuantifierPopupController controller = loader.getController();
        controller.setEntities(systemChoiceBox.getValue().getEntities(), quantifierUsedEntities);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return  controller.getSelectedEntity();
    }

    public void setOntologyManagerAndLibraries(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
        libraryChoiceBox.getItems().setAll(ontologyManager.getLibraries());
    }

    public void setPreselectedLibrary(Library library) {
        if (ontologyManager != null) {
            libraryChoiceBox.getSelectionModel().select(library);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void addConstrainHolderForEdit(ConstraintHolder constraintHolder) {
        editConstraintHolder = constraintHolder;
        nameField.setText(constraintHolder.getName());
        commentField.setText(constraintHolder.getComment());

        ConstraintView rootView = ConstraintViewFactory.create(this, constraintHolder.getConstraint());
        rootConstraintView.set(rootView);
        constraintHBox.getChildren().clear();
        constraintHBox.getChildren().add(rootView.getView());
        newRootButton.setStyle("-fx-background-color: lightgrey; -fx-opacity: 0.7;");

        for (Quantifier q : constraintHolder.getQuantifiers()){
            quantifiers.add(q);
            quantifierHBox.getChildren().add(new QuantifierView(q, () -> {
                quantifiers.remove(q);
                quantifierUsedEntities.remove(q.getEntity());
            }));
            quantifierUsedEntities.add(q.getEntity());
        }
        addQuantifierForAllButton.setDisable(true);
        addQuantifierExistsButton.setDisable(true);
    }

    public void addSystemsAndSetPreselectedSystem(Library library, oSystem system) {
        if (ontologyManager != null) {
            systemChoiceBox.getItems().setAll(library.getSystems());
            systemChoiceBox.getSelectionModel().select(system);
        }
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
