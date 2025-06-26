package tug.tobkul.ontologybrowser.jfxcontroller.attribute;

import tug.tobkul.ontologybrowser.ontology.model.attribute.*;

import java.util.Arrays;

public class EditAttributePopupController extends AttributePopupController {
    private Attribute attribute;

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
        attributeTypeChoiceBox.getSelectionModel().select(attribute.getType());
        nameField.setText(attribute.getName());
        commentField.setText(attribute.getComment());
        switch (attribute.getType()) {
            case BOOL -> {
            }
            case ENUM -> {
                setEnumerationSettersActive(true);
                EnumValue v = (EnumValue) attribute.getValue();
                enumerationTextArea.setText(String.join("\n", v.getValue()));
            }
            case INT -> {
                setNumberRangeSettersActive(true);
                NumberRangeValue v = (NumberRangeValue) attribute.getValue();
                minTextField.setText(String.valueOf(v.getValue().getMin()));
                maxTextField.setText(String.valueOf(v.getValue().getMax()));
                intervalTextField.setText(String.valueOf(v.getValue().getInterval()));
            }
        }
    }

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
            return;
        }
        if (attributeHolder.getAttributes().stream().anyMatch(a -> a.getName().equals(nameField.getText()))
                && !attribute.getName().equals(nameField.getText())) {
            invalidInputLabel.setText("Name must be\nunique!");
            invalidInputLabel.setVisible(true);
            return;
        }

        switch (attributeTypeChoiceBox.getValue()) {
            case BOOL -> {
                attribute.setName(nameField.getText());
                attribute.setComment(commentField.getText());
                attribute.setType(AttributeType.BOOL);
                attribute.setValue(new BooleanValue());
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
            case ENUM -> {
                if (enumerationTextArea.getText().isBlank()) {
                    invalidInputLabel.setText("Values missing!");
                    invalidInputLabel.setVisible(true);
                    return;
                }
                attribute.setName(nameField.getText());
                attribute.setComment(commentField.getText());
                attribute.setType(AttributeType.ENUM);
                attribute.setValue(new EnumValue(Arrays.asList(enumerationTextArea.getText().split("\\R"))));
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
            case INT -> {
                if (!checkIntFields()) {
                    return;
                }
                attribute.setName(nameField.getText());
                attribute.setComment(commentField.getText());
                attribute.setType(AttributeType.INT);
                attribute.setValue(new NumberRangeValue(
                        new NumberRange(Double.parseDouble(minTextField.getText()),
                                Double.parseDouble(maxTextField.getText()),
                                Double.parseDouble(intervalTextField.getText()))
                ));
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
        }
    }
}
