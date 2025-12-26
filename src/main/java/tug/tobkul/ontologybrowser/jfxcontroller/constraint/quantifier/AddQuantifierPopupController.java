package tug.tobkul.ontologybrowser.jfxcontroller.constraint.quantifier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import tug.tobkul.ontologybrowser.ontology.model.Entity;

import java.util.List;

public class AddQuantifierPopupController {

    @FXML
    private ComboBox<Entity> entityComboBox;

    @FXML
    private Label invalidInputLabel;

    public void setEntities(List<Entity> entities, List<Entity> usedEntities) {
        entityComboBox.getItems().setAll(entities);

        entityComboBox.setCellFactory(callback -> new ListCell<>() {
            @Override
            protected void updateItem(Entity entity, boolean empty) {
                super.updateItem(entity, empty);
                if (entity == null || empty) {
                    setText(null);
                } else {
                    setText(entity.getName());

                    if (usedEntities.contains(entity)) {
                        setDisable(true);
                        setOpacity(0.4);
                    }
                }
            }
        });
    }

    public Entity getSelectedEntity(){
        return entityComboBox.getSelectionModel().getSelectedItem();
    }

    public void onConfirm(){
        entityComboBox.getScene().getWindow().hide();
    }
}
