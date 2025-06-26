package tug.tobkul.ontologybrowser.jfxcontroller.attribute;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tug.tobkul.ontologybrowser.ontology.model.AttributeHolder;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AttributePopupController {
    protected AttributeHolder attributeHolder;

    @FXML
    protected ChoiceBox<AttributeType> attributeTypeChoiceBox;

    @FXML
    protected TextField nameField;

    @FXML
    protected TextArea commentField;

    @FXML
    protected Label minLabel;

    @FXML
    protected Label maxLabel;

    @FXML
    protected Label intervalLabel;

    @FXML
    protected TextField minTextField;

    @FXML
    protected TextField maxTextField;

    @FXML
    protected TextField intervalTextField;

    @FXML
    protected VBox enumerationLabels;

    @FXML
    protected TextArea enumerationTextArea;

    @FXML
    protected Label invalidInputLabel;

    @FXML
    public void initialize() {
        attributeTypeChoiceBox.getItems().setAll(Arrays.stream(AttributeType.class.getEnumConstants())
                .collect(Collectors.toList())
        );
        attributeTypeChoiceBox.getSelectionModel().selectFirst();
        attributeTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case BOOL -> {
                        setNumberRangeSettersActive(false);
                        setEnumerationSettersActive(false);
                    }
                    case ENUM -> {
                        setNumberRangeSettersActive(false);
                        setEnumerationSettersActive(true);
                    }
                    case INT -> {
                        setEnumerationSettersActive(false);
                        setNumberRangeSettersActive(true);
                    }
                }
            }
        });
        minTextField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^-?\\d*([.,]\\d*)?$")) {
                return change;
            }
            return null;
        }));
        maxTextField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^-?\\d*([.,]\\d*)?$")) {
                return change;
            }
            return null;
        }));
        intervalTextField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^-?\\d*([.,]\\d*)?$")) {
                return change;
            }
            return null;
        }));
    }

    public void setAttributeHolder(AttributeHolder attributeHolder) {
        this.attributeHolder = attributeHolder;
    }

    protected boolean checkIntFields() {
        if (minTextField.getText().isBlank()) {
            invalidInputLabel.setText("Min missing!");
            invalidInputLabel.setVisible(true);
            return false;
        }
        if (maxTextField.getText().isBlank()) {
            invalidInputLabel.setText("Max missing!");
            invalidInputLabel.setVisible(true);
            return false;
        }
        if (intervalTextField.getText().isBlank()) {
            invalidInputLabel.setText("Interval missing!");
            invalidInputLabel.setVisible(true);
            return false;
        }
        double min = Double.parseDouble(minTextField.getText());
        double max = Double.parseDouble(maxTextField.getText());
        double interval = Double.parseDouble(intervalTextField.getText());
        if (max < min) {
            invalidInputLabel.setText("Maximum can't be < Minimum");
            invalidInputLabel.setVisible(true);
            return false;
        }
        if ((max - min) < interval) {
            invalidInputLabel.setText("(Max-Min) can't be < Interval");
            invalidInputLabel.setVisible(true);
            return false;
        }
        return true;
    }

    protected void setNumberRangeSettersActive(boolean active) {
        minLabel.setVisible(active);
        minLabel.setManaged(active);
        maxLabel.setVisible(active);
        maxLabel.setManaged(active);
        intervalLabel.setVisible(active);
        intervalLabel.setManaged(active);
        minTextField.setVisible(active);
        minTextField.setManaged(active);
        maxTextField.setVisible(active);
        maxTextField.setManaged(active);
        intervalTextField.setVisible(active);
        intervalTextField.setManaged(active);
    }

    protected void setEnumerationSettersActive(boolean active) {
        enumerationLabels.setVisible(active);
        enumerationLabels.setManaged(active);
        enumerationTextArea.setVisible(active);
        enumerationTextArea.setManaged(active);
    }
}
