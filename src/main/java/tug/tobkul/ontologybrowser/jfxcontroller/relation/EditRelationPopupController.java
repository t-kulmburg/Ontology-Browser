package tug.tobkul.ontologybrowser.jfxcontroller.relation;

import javafx.fxml.FXML;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.Relation;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class EditRelationPopupController extends RelationPopupController {

    private Relation relation;

    @FXML
    public void initialize() {
    }

    public void setLibrarySystemRelation(Library library, oSystem system, Relation relation) {
        libraryChoiceBox.getItems().setAll(library);
        libraryChoiceBox.getSelectionModel().select(library);
        libraryChoiceBox.setDisable(true);

        systemChoiceBox.getItems().setAll(system);
        systemChoiceBox.getSelectionModel().select(system);
        systemChoiceBox.setDisable(true);

        entityAChoiceBox.getItems().setAll(system.getEntities());
        entityAChoiceBox.getSelectionModel().select(relation.getEntityA());

        entityBChoiceBox.getItems().setAll(system.getEntities());
        entityBChoiceBox.getSelectionModel().select(relation.getEntityB());

        cardinalityAField.setText(relation.getCardinalityMin());
        cardinalityBField.setText(relation.getCardinalityMax());

        this.relation = relation;
        nameField.setText(relation.getName());
        commentField.setText(relation.getComment());
    }

    public void onConfirm() {
        if (!checkInputFields()) {
            setInvalidInputLabelVisible();
            return;
        }
        if (systemChoiceBox.getValue().getRelations().stream().noneMatch(r -> r.getName().equals(nameField.getText()))
                || relation.getName().equals(nameField.getText())) {
            relation.setEntityA(entityAChoiceBox.getValue());
            relation.setEntityB(entityBChoiceBox.getValue());
            relation.setCardinalityMin(cardinalityAField.getText());
            relation.setCardinalityMax(cardinalityBField.getText());
            relation.setName(nameField.getText());
            relation.setComment(commentField.getText());
            nameField.getScene().getWindow().hide();
            return;
        }
        invalidInputLabel.setText("Name must be unique!");
        setInvalidInputLabelVisible();
    }
}
