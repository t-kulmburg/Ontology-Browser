package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.ConstraintHolder;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Scenario;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.QuantifierType;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddConstraintPopupController {
    public final ObjectProperty<ConstraintView> rootConstraintView = new SimpleObjectProperty<>();
    private final List<Quantifier> quantifierList = new ArrayList<>();
    private Stage stage;
    private oSystem system;
    private Scenario scenario;
    private Constraint createdConstraint;
    private ConstraintHolder editConstraintHolder = null;

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
        HighlightManager.clearCurrent();
    }

    @FXML
    private void onNewRootConstraint() throws IOException {
        if (newRootButton.getText().equals("New Root")) {
            // do newRootConstraint logic
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simpleConstraintPopup.fxml"));
            AddSimpleConstraintPopupController controller = new AddSimpleConstraintPopupController();
            loader.setController(controller);

            Parent root = loader.load();
            controller.setOuterSystemAndQuantifiers(system, quantifierList);

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
                addQuantifierExistsButton.setDisable(true);
                addQuantifierForAllButton.setDisable(true);
                newRootButton.setText("Reset");
            }
        } else {
            // do reset logic
            rootConstraintView.set(null);
            createdConstraint = null;
            constraintHBox.getChildren().clear();
            addQuantifierExistsButton.setDisable(false);
            addQuantifierForAllButton.setDisable(false);
            quantifierHBox.getChildren().clear();
            quantifierList.clear();
            newRootButton.setText("New Root");
            newRootButton.setStyle("");
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
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        if (system.getScenarios().stream().flatMap(s -> s.getConstraintHolderList().stream())
                .noneMatch(c -> c.getName().equals(nameField.getText())) ||
                (editConstraintHolder != null && editConstraintHolder.getName().equals(nameField.getText()))) {
            if (editConstraintHolder == null) {
                if (scenario != null) {
                    scenario.addConstraint(new ConstraintHolder(nameField.getText(), commentField.getText(),
                            rootConstraintView.get().getConstraint(), quantifierList));
                }
            } else {
                editConstraintHolder.setName(nameField.getText());
                editConstraintHolder.setComment(commentField.getText());
                editConstraintHolder.setConstraint(rootConstraintView.get().getConstraint());
                editConstraintHolder.setQuantifiers(quantifierList);
            }
            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }

    @FXML
    private void onAddForAll() throws IOException {
        Quantifier quantifier = getQuantifierFromQuantifierPopup(QuantifierType.FOR_ALL);
        if (quantifier == null) {
            return;
        }
        quantifierList.add(quantifier);
        quantifierHBox.getChildren().add(new QuantifierView(quantifier, () -> {
            quantifierList.remove(quantifier);
        }));
    }

    @FXML
    private void onAddExists() throws IOException {
        Quantifier quantifier = getQuantifierFromQuantifierPopup(QuantifierType.EXISTS);
        if (quantifier == null) {
            return;
        }
        quantifierList.add(quantifier);
        quantifierHBox.getChildren().add(new QuantifierView(quantifier, () -> {
            quantifierList.remove(quantifier);
        }));
    }

    private Quantifier getQuantifierFromQuantifierPopup(QuantifierType type) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addQuantifierPopup.fxml"));
        Parent root = loader.load();

        AddQuantifierPopupController controller = loader.getController();
        controller.setLists(system.getRelations(),
                quantifierList.stream().map(Quantifier::getIdentifier).toList());

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        if (controller.getSelectedRelation() == null || controller.getSelectedIdentifier() == null) {
            return null;
        }

        return new Quantifier(type, controller.getSelectedRelation(), controller.getSelectedIdentifier());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSystemAndScenario(oSystem system, Scenario scenario) {
        this.system = system;
        this.scenario = scenario;
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
        newRootButton.setText("Reset");

        for (Quantifier q : constraintHolder.getQuantifiers()) {
            quantifierList.add(q);
            quantifierHBox.getChildren().add(new QuantifierView(q, () -> {
                quantifierList.remove(q);
            }));
        }
        addQuantifierForAllButton.setDisable(true);
        addQuantifierExistsButton.setDisable(true);
        quantifierHBox.setMouseTransparent(true);
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
