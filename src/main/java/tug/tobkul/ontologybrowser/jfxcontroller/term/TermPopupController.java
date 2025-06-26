package tug.tobkul.ontologybrowser.jfxcontroller.term;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.ontology.model.Entity;
import tug.tobkul.ontologybrowser.ontology.model.attribute.Attribute;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.ArithmeticOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.*;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.ArrayList;

public abstract class TermPopupController {
    protected Stage stage;
    protected Term result;
    protected AttributeType attributeType;

    @FXML
    protected ChoiceBox<TermType> termTypeChoiceBox;
    @FXML
    protected Label invalidInputLabel;

    // TermType 'parameter'
    @FXML
    protected GridPane parameterTypeGridPane;
    @FXML
    protected ChoiceBox<Entity> parameterTypeEntityChoiceBox;
    @FXML
    protected ChoiceBox<Attribute> parameterTypeAttributeChoiceBox;
    @FXML
    protected TextFlow parameterTypeAttributeTypeTextFlow;
    @FXML
    protected TextFlow parameterTypeAttributeValuesTextFlow;

    // TermType 'value'
    @FXML
    protected GridPane valueTypeGridPane;
    @FXML
    protected TextField valueTypeValueField;

    // TermType 'arithmeticParameter'
    @FXML
    protected GridPane arithmeticParameterTypeGridPane;
    @FXML
    protected ChoiceBox<Entity> arithmeticParameterTypeEntityChoiceBoxLhs;
    @FXML
    protected ChoiceBox<Attribute> arithmeticParameterTypeAttributeChoiceBoxLhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeTypeTextFlowLhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeValuesTextFlowLhs;
    @FXML
    protected ChoiceBox<ArithmeticOperator> arithmeticParameterTypeArithmeticOperatorChoiceBox;
    @FXML
    protected ChoiceBox<Entity> arithmeticParameterTypeEntityChoiceBoxRhs;
    @FXML
    protected ChoiceBox<Attribute> arithmeticParameterTypeAttributeChoiceBoxRhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeTypeTextFlowRhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeValuesTextFlowRhs;

    // TermType 'arithmeticValue'
    @FXML
    protected GridPane arithmeticValueTypeGridPane;
    @FXML
    protected ChoiceBox<Entity> arithmeticValueTypeEntityChoiceBox;
    @FXML
    protected ChoiceBox<Attribute> arithmeticValueTypeAttributeChoiceBox;
    @FXML
    protected TextFlow arithmeticValueTypeAttributeTypeTextFlow;
    @FXML
    protected TextFlow arithmeticValueTypeAttributeValuesTextFlow;
    @FXML
    protected ChoiceBox<ArithmeticOperator> arithmeticValueTypeArithmeticOperatorChoiceBox;
    @FXML
    protected TextField arithmeticValueTypeValueField;

    @FXML
    public void initialize() {
        initTypeSelector();
        initParameterType();
        initArithmeticParameterType();
        initArithmeticValueType();
    }

    private void initTypeSelector() {
        termTypeChoiceBox.getItems().addAll(TermType.values());
        termTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            parameterTypeGridPane.setVisible(newValue.equals(TermType.PARAMETER));
            valueTypeGridPane.setVisible(newValue.equals(TermType.VALUE));
            arithmeticParameterTypeGridPane.setVisible(newValue.equals(TermType.A_PARAMETER));
            arithmeticValueTypeGridPane.setVisible(newValue.equals(TermType.A_VALUE));
        });
        termTypeChoiceBox.getSelectionModel().selectFirst();
    }

    private void initParameterType() {
        parameterTypeEntityChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (attributeType == null) {
                    parameterTypeAttributeChoiceBox.getItems().setAll(newValue.getAttributes());
                } else {
                    parameterTypeAttributeChoiceBox.getItems().setAll(newValue.getAttributes().stream().filter(attribute -> attribute.getType().equals(attributeType)).toList());
                }
            } else {
                parameterTypeAttributeChoiceBox.getItems().setAll(new ArrayList<>());
            }
            parameterTypeAttributeChoiceBox.getSelectionModel().select(null);
        });
        parameterTypeAttributeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                parameterTypeAttributeTypeTextFlow.getChildren().setAll(new Text(newValue.getType().toString()));
                parameterTypeAttributeValuesTextFlow.getChildren().setAll(newValue.getValue().getPossibleValueList().stream().map(s -> new Text(s + "\n")).toList());
            } else {
                parameterTypeAttributeTypeTextFlow.getChildren().clear();
                parameterTypeAttributeValuesTextFlow.getChildren().clear();
            }
        });
    }

    private void initArithmeticParameterType() {
        arithmeticParameterTypeArithmeticOperatorChoiceBox.getItems().addAll(ArithmeticOperator.values());
        // parameter lhs
        arithmeticParameterTypeEntityChoiceBoxLhs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticParameterTypeAttributeChoiceBoxLhs.getItems().setAll(
                        newValue.getAttributes().stream().filter(attribute -> attribute.getType().equals(AttributeType.INT))
                                .toList() // for arithmetic operations only numbers are allowed
                );
            } else {
                arithmeticParameterTypeAttributeChoiceBoxLhs.getItems().setAll(new ArrayList<>());
            }
            arithmeticParameterTypeAttributeChoiceBoxLhs.getSelectionModel().select(null);
        });
        arithmeticParameterTypeAttributeChoiceBoxLhs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticParameterTypeAttributeTypeTextFlowLhs.getChildren().setAll(new Text(newValue.getType().toString()));
                arithmeticParameterTypeAttributeValuesTextFlowLhs.getChildren().setAll(newValue.getValue().getPossibleValueList().stream().map(s -> new Text(s + "\n")).toList());
            } else {
                arithmeticParameterTypeAttributeTypeTextFlowLhs.getChildren().clear();
                arithmeticParameterTypeAttributeValuesTextFlowLhs.getChildren().clear();
            }
        });
        // parameter rhs
        arithmeticParameterTypeEntityChoiceBoxRhs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticParameterTypeAttributeChoiceBoxRhs.getItems().setAll(
                        newValue.getAttributes().stream().filter(attribute -> attribute.getType().equals(AttributeType.INT))
                                .toList() // for arithmetic operations only numbers are allowed
                );
            } else {
                arithmeticParameterTypeAttributeChoiceBoxRhs.getItems().setAll(new ArrayList<>());
            }
            arithmeticParameterTypeAttributeChoiceBoxRhs.getSelectionModel().select(null);
        });
        arithmeticParameterTypeAttributeChoiceBoxRhs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticParameterTypeAttributeTypeTextFlowRhs.getChildren().setAll(new Text(newValue.getType().toString()));
                arithmeticParameterTypeAttributeValuesTextFlowRhs.getChildren().setAll(newValue.getValue().getPossibleValueList().stream().map(s -> new Text(s + "\n")).toList());
            } else {
                arithmeticParameterTypeAttributeTypeTextFlowRhs.getChildren().clear();
                arithmeticParameterTypeAttributeValuesTextFlowRhs.getChildren().clear();
            }
        });
    }

    private void initArithmeticValueType() {
        arithmeticValueTypeArithmeticOperatorChoiceBox.getItems().addAll(ArithmeticOperator.values());
        // parameter
        arithmeticValueTypeEntityChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticValueTypeAttributeChoiceBox.getItems().setAll(
                        newValue.getAttributes().stream().filter(attribute -> attribute.getType().equals(AttributeType.INT))
                                .toList()); // for arithmetic operations only numbers are allowed
            } else {
                arithmeticValueTypeAttributeChoiceBox.getItems().setAll(new ArrayList<>());
            }
            arithmeticValueTypeAttributeChoiceBox.getSelectionModel().select(null);
        });
        arithmeticValueTypeAttributeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                arithmeticValueTypeAttributeTypeTextFlow.getChildren().setAll(new Text(newValue.getType().toString()));
                arithmeticValueTypeAttributeValuesTextFlow.getChildren().setAll(newValue.getValue().getPossibleValueList().stream().map(s -> new Text(s + "\n")).toList());
            } else {
                arithmeticValueTypeAttributeTypeTextFlow.getChildren().clear();
                arithmeticValueTypeAttributeValuesTextFlow.getChildren().clear();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEntitiesFromOuterSystem(oSystem outerSystem) {
        if (attributeType == null) {
            parameterTypeEntityChoiceBox.getItems().setAll(outerSystem.getEntities().stream().filter(entity ->
                    !entity.getAttributes().isEmpty()).toList());
        } else {
            parameterTypeEntityChoiceBox.getItems().setAll(outerSystem.getEntities().stream().filter(entity ->
                    !entity.getAttributes().isEmpty()).filter(
                    entity -> entity.getAttributes().stream().anyMatch(
                            attribute -> attribute.getType().equals(attributeType))).toList()
            );
        }
        if (attributeType == null || attributeType == AttributeType.INT) {
            arithmeticParameterTypeEntityChoiceBoxLhs.getItems().setAll(outerSystem.getEntities().stream().filter(entity ->
                    entity.getAttributes().stream().anyMatch(attribute -> attribute.getType().equals(AttributeType.INT))).toList());
            arithmeticParameterTypeEntityChoiceBoxRhs.getItems().setAll(outerSystem.getEntities().stream().filter(entity ->
                    entity.getAttributes().stream().anyMatch(attribute -> attribute.getType().equals(AttributeType.INT))).toList());
            arithmeticValueTypeEntityChoiceBox.getItems().setAll(outerSystem.getEntities().stream().filter(entity ->
                    entity.getAttributes().stream().anyMatch(attribute -> attribute.getType().equals(AttributeType.INT))).toList());
        }
    }

    public void setAttributeTypeFilter(AttributeType type) {
        this.attributeType = type;
    }

    public Term getResult() {
        return result;
    }

    @FXML
    private void onConfirm() {
        invalidInputLabel.setVisible(false);
        switch (termTypeChoiceBox.getSelectionModel().getSelectedItem()) {
            case PARAMETER -> {
                if (parameterTypeEntityChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Entity missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                if (parameterTypeAttributeChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Attribute missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ParameterTerm(new Parameter(
                        parameterTypeEntityChoiceBox.getValue(),
                        parameterTypeAttributeChoiceBox.getValue()
                ));
                stage.close();
            }
            case VALUE -> {
                if (valueTypeValueField.getText() == null) {
                    invalidInputLabel.setText("Value missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ValueTerm(valueTypeValueField.getText());
                stage.close();
            }
            case A_PARAMETER -> {
                if (arithmeticParameterTypeEntityChoiceBoxLhs.getValue() == null ||
                        arithmeticParameterTypeEntityChoiceBoxRhs.getValue() == null) {
                    invalidInputLabel.setText("Entity missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                if (arithmeticParameterTypeAttributeChoiceBoxLhs.getValue() == null ||
                        arithmeticParameterTypeAttributeChoiceBoxRhs.getValue() == null) {
                    invalidInputLabel.setText("Attribute missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ArithmeticParameterTerm(
                        new Parameter(
                                arithmeticParameterTypeEntityChoiceBoxLhs.getValue(),
                                arithmeticParameterTypeAttributeChoiceBoxLhs.getValue()
                        ),
                        new Parameter(
                                arithmeticParameterTypeEntityChoiceBoxRhs.getValue(),
                                arithmeticParameterTypeAttributeChoiceBoxRhs.getValue()
                        ),
                        arithmeticParameterTypeArithmeticOperatorChoiceBox.getValue()
                );
                stage.close();
            }
            case A_VALUE -> {
                if (arithmeticValueTypeEntityChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Entity missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                if (arithmeticValueTypeAttributeChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Attribute missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                if (arithmeticValueTypeValueField.getText() == null) {
                    invalidInputLabel.setText("Value missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ArithmeticValueTerm(
                        new Parameter(
                                arithmeticValueTypeEntityChoiceBox.getValue(),
                                arithmeticValueTypeAttributeChoiceBox.getValue()
                        ),
                        arithmeticValueTypeValueField.getText(),
                        arithmeticValueTypeArithmeticOperatorChoiceBox.getValue()
                );
                stage.close();
            }
        }
    }

    public void setInvalidInputLabelVisibleAndFormat() {
        invalidInputLabel.setVisible(true);
        invalidInputLabel.applyCss();
        invalidInputLabel.layout();
    }
}
