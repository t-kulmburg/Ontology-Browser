package tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;

import java.util.List;

public class AddQuantifierPopupController {

    @FXML
    private ComboBox<Relation> relationComboBox;

    @FXML
    private Label invalidInputLabel;

    public void setRelations(List<Relation> relations, List<Relation> usedRelations) {
        relationComboBox.getItems().setAll(relations);

        relationComboBox.setCellFactory(callback -> new ListCell<>() {
            @Override
            protected void updateItem(Relation relation, boolean empty) {
                super.updateItem(relation, empty);
                if (relation == null || empty) {
                    setText(null);
                } else {
                    setText(relation.getEntityString());

                    if (usedRelations.contains(relation)) {
                        setDisable(true);
                        setOpacity(0.4);
                    }
                }
            }
        });

        relationComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Relation relation) {
                return relation == null ? "" : relation.getEntityString();
            }

            @Override
            public Relation fromString(String string) {
                return null; // usually not needed
            }
        });
    }

    public Relation getSelectedRelation(){
        return relationComboBox.getSelectionModel().getSelectedItem();
    }

    public void onConfirm(){
        relationComboBox.getScene().getWindow().hide();
    }
}
