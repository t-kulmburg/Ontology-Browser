package tug.tobkul.ontologybrowser.jfxcontroller.attribute;

import tug.tobkul.ontologybrowser.ontology.model.attribute.*;

import java.util.Arrays;

public class AddAttributePopupController extends AttributePopupController {

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (nameField.getText().isBlank()) {
            invalidInputLabel.setText("Name missing!");
            invalidInputLabel.setVisible(true);
            return;
        }
        if (attributeHolder.getAttributes().stream().anyMatch(a -> a.getName().equals(nameField.getText()))) {
            invalidInputLabel.setText("Name must be\nunique!");
            invalidInputLabel.setVisible(true);
            return;
        }

        switch (attributeTypeChoiceBox.getValue()) {
            case BOOL -> {
                attributeHolder.addAttribute(new Attribute(
                        nameField.getText(),
                        commentField.getText(),
                        AttributeType.BOOL,
                        new BooleanValue()
                ));
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
            case ENUM -> {
                if (enumerationTextArea.getText().isBlank()) {
                    invalidInputLabel.setText("Values missing!");
                    invalidInputLabel.setVisible(true);
                    return;
                }
                attributeHolder.addAttribute(new Attribute(
                        nameField.getText(),
                        commentField.getText(),
                        AttributeType.ENUM,
                        new EnumValue(Arrays.asList(enumerationTextArea.getText().split("\\R")))
                ));
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
            case INT -> {
                if (!checkIntFields()) {
                    return;
                }
                attributeHolder.addAttribute(new Attribute(
                        nameField.getText(),
                        commentField.getText(),
                        AttributeType.INT,
                        new NumberRangeValue(new NumberRange(Double.parseDouble(minTextField.getText()),
                                Double.parseDouble(maxTextField.getText()),
                                Double.parseDouble(intervalTextField.getText())))
                ));
                attributeTypeChoiceBox.getScene().getWindow().hide();
            }
        }
    }
}
