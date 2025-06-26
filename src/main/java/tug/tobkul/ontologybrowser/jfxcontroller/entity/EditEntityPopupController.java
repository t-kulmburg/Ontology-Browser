package tug.tobkul.ontologybrowser.jfxcontroller.entity;

import javafx.fxml.FXML;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.Library;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

public class EditEntityPopupController extends EntityPopupController {
    private oSystem system;
    private Entity entity;

    @FXML
    public void initialize() {
        initSuperEntityComboBox();
    }

    public void setLibrarySystemEntity(Library library, oSystem system, Entity entity) {
        libraryChoiceBox.getItems().setAll(library);
        libraryChoiceBox.getSelectionModel().select(library);
        libraryChoiceBox.setDisable(true);

        this.system = system;
        systemChoiceBox.getItems().setAll(system);
        systemChoiceBox.getSelectionModel().select(system);
        systemChoiceBox.setDisable(true);

        superEntityCheckComboBox.getItems().setAll(system.getEntities().stream().filter(e -> !e.getName().equals(entity.getName())).toList());

        this.entity = entity;
        nameField.setText(entity.getName());
        commentField.setText(entity.getComment());

        if (entity.getSuperEntity() != null) {
            superEntityCheckComboBox.getCheckModel().check(entity.getSuperEntity());
        }
    }

    public void onConfirm() {
        if (!checkComboBoxes()) {
            return;
        }

        if (system.getEntities().stream().noneMatch(e -> e.getName().equals(nameField.getText()))
                || entity.getName().equals(nameField.getText())) {
            entity.setName(nameField.getText());
            entity.setComment(commentField.getText());

            if (entity.getSuperEntity() != null) {
                entity.getSuperEntity().removeSubEntity(entity);
            }
            entity.setSuperEntity(superEntityCheckComboBox.getCheckModel().getCheckedItems().isEmpty() ?
                    null : superEntityCheckComboBox.getCheckModel().getCheckedItems().getFirst());
            if (!superEntityCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                superEntityCheckComboBox.getCheckModel().getCheckedItems().getFirst().addSubEntity(entity);
            }

            for (Entity sub : entity.getSubEntities()) {
                sub.setSuperEntity(null);
            }

            nameField.getScene().getWindow().hide();
        } else {
            invalidInputLabel.setText("Name must be unique!");
            setInvalidInputLabelVisibleAndFormat();
        }
    }
}
