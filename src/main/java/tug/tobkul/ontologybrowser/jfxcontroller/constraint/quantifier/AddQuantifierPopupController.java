package tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Relation;

import java.util.List;
import java.util.stream.IntStream;

public class AddQuantifierPopupController {

    @FXML
    private ComboBox<Relation> relationComboBox;

    @FXML
    private ComboBox<String> identifierComboBox;

    @FXML
    private Label invalidInputLabel;

    public void setLists(List<Relation> relations, List<String> usedIdentifiers) {
        relationComboBox.getItems().setAll(relations);

        identifierComboBox.getItems()
                .addAll(IntStream.rangeClosed('a', 'z').mapToObj(i -> String.valueOf((char) i)).toList());

        identifierComboBox.setCellFactory(callback -> new ListCell<>() {
            @Override
            protected void updateItem(String identifier, boolean empty) {
                super.updateItem(identifier, empty);
                if (identifier == null || empty) {
                    setText(null);
                } else {
                    setText(identifier);

                    if (usedIdentifiers.contains(identifier)) {
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

    public Relation getSelectedRelation() {
        return relationComboBox.getSelectionModel().getSelectedItem();
    }

    public String getSelectedIdentifier() {
        return identifierComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML

    public void onConfirm() {
        relationComboBox.getScene().getWindow().hide();
    }
}
