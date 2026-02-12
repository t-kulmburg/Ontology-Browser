package tug.tobkul.ontologybrowser.jfxcontroller.term;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tug.tobkul.ontologybrowser.ontology.model.attribute.AttributeType;
import tug.tobkul.ontologybrowser.ontology.model.constraint.operator.ArithmeticOperator;
import tug.tobkul.ontologybrowser.ontology.model.constraint.parameter.Parameter;
import tug.tobkul.ontologybrowser.ontology.model.constraint.quantifier.Quantifier;
import tug.tobkul.ontologybrowser.ontology.model.constraint.term.*;
import tug.tobkul.ontologybrowser.ontology.model.oSystem;

import java.util.List;

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
    protected ChoiceBox<Parameter> parameterTypeParameterChoiceBox;
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
    protected ChoiceBox<Parameter> arithmeticParameterTypeParameterChoiceBoxLhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeTypeTextFlowLhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeValuesTextFlowLhs;
    @FXML
    protected ChoiceBox<ArithmeticOperator> arithmeticParameterTypeArithmeticOperatorChoiceBox;
    @FXML
    protected ChoiceBox<Parameter> arithmeticParameterTypeParameterChoiceBoxRhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeTypeTextFlowRhs;
    @FXML
    protected TextFlow arithmeticParameterTypeAttributeValuesTextFlowRhs;

    // TermType 'arithmeticValue'
    @FXML
    protected GridPane arithmeticValueTypeGridPane;
    @FXML
    protected ChoiceBox<Parameter> arithmeticValueTypeParameterChoiceBox;
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
        parameterTypeParameterChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        parameterTypeAttributeTypeTextFlow.getChildren()
                                .setAll(new Text(newValue.getType().toString()));
                        parameterTypeAttributeValuesTextFlow.getChildren()
                                .setAll(newValue.getAttribute().getValue().getPossibleValueList().stream()
                                        .map(s -> new Text(s + "\n")).toList());
                    } else {
                        parameterTypeAttributeTypeTextFlow.getChildren().clear();
                        parameterTypeAttributeValuesTextFlow.getChildren().clear();
                    }
                });
    }

    private void initArithmeticParameterType() {
        arithmeticParameterTypeArithmeticOperatorChoiceBox.getItems().addAll(ArithmeticOperator.values());
        // parameter lhs
        arithmeticParameterTypeParameterChoiceBoxLhs.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        arithmeticParameterTypeAttributeTypeTextFlowLhs.getChildren()
                                .setAll(new Text(newValue.getType().toString()));
                        arithmeticParameterTypeAttributeValuesTextFlowLhs.getChildren()
                                .setAll(newValue.getAttribute().getValue().getPossibleValueList().stream()
                                        .map(s -> new Text(s + "\n")).toList());
                    } else {
                        arithmeticParameterTypeAttributeTypeTextFlowLhs.getChildren().clear();
                        arithmeticParameterTypeAttributeValuesTextFlowLhs.getChildren().clear();
                    }
                });
        // parameter rhs
        arithmeticParameterTypeParameterChoiceBoxRhs.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        arithmeticParameterTypeAttributeTypeTextFlowRhs.getChildren()
                                .setAll(new Text(newValue.getType().toString()));
                        arithmeticParameterTypeAttributeValuesTextFlowRhs.getChildren()
                                .setAll(newValue.getAttribute().getValue().getPossibleValueList().stream()
                                        .map(s -> new Text(s + "\n")).toList());
                    } else {
                        arithmeticParameterTypeAttributeTypeTextFlowRhs.getChildren().clear();
                        arithmeticParameterTypeAttributeValuesTextFlowRhs.getChildren().clear();
                    }
                });
    }

    private void initArithmeticValueType() {
        arithmeticValueTypeArithmeticOperatorChoiceBox.getItems().addAll(ArithmeticOperator.values());
        // parameter
        arithmeticValueTypeParameterChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        arithmeticValueTypeAttributeTypeTextFlow.getChildren()
                                .setAll(new Text(newValue.getType().toString()));
                        arithmeticValueTypeAttributeValuesTextFlow.getChildren()
                                .setAll(newValue.getAttribute().getValue().getPossibleValueList().stream()
                                        .map(s -> new Text(s + "\n")).toList());
                    } else {
                        arithmeticValueTypeAttributeTypeTextFlow.getChildren().clear();
                        arithmeticValueTypeAttributeValuesTextFlow.getChildren().clear();
                    }
                });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEntitiesFromOuterSystem(oSystem outerSystem, List<Quantifier> quantifierList) {
        if (attributeType == null) {
            parameterTypeParameterChoiceBox.getItems()
                    .setAll(outerSystem.getParameterListForConstraint(quantifierList));
        } else {
            parameterTypeParameterChoiceBox.getItems()
                    .setAll(outerSystem.getParameterListForConstraint(quantifierList).stream()
                            .filter(parameter -> parameter.getType().equals(attributeType)).toList());
        }
        if (attributeType == null || attributeType == AttributeType.INT) {
            arithmeticParameterTypeParameterChoiceBoxLhs.getItems()
                    .setAll(outerSystem.getParameterListForConstraint(quantifierList).stream()
                            .filter(parameter -> parameter.getType().equals(AttributeType.INT)).toList());
            arithmeticParameterTypeParameterChoiceBoxRhs.getItems()
                    .setAll(outerSystem.getParameterListForConstraint(quantifierList).stream()
                            .filter(parameter -> parameter.getType().equals(AttributeType.INT)).toList());
            arithmeticValueTypeParameterChoiceBox.getItems()
                    .setAll(outerSystem.getParameterListForConstraint(quantifierList).stream()
                            .filter(parameter -> parameter.getType().equals(AttributeType.INT)).toList());
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
                if (parameterTypeParameterChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Parameter missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ParameterTerm(parameterTypeParameterChoiceBox.getValue());
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
                if (arithmeticParameterTypeParameterChoiceBoxLhs.getValue() == null ||
                        arithmeticParameterTypeParameterChoiceBoxRhs.getValue() == null) {
                    invalidInputLabel.setText("Parameter missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ArithmeticParameterTerm(arithmeticParameterTypeParameterChoiceBoxLhs.getValue(),
                        arithmeticParameterTypeParameterChoiceBoxRhs.getValue(),
                        arithmeticParameterTypeArithmeticOperatorChoiceBox.getValue());
                stage.close();
            }
            case A_VALUE -> {
                if (arithmeticValueTypeParameterChoiceBox.getValue() == null) {
                    invalidInputLabel.setText("Parameter missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                if (arithmeticValueTypeValueField.getText() == null) {
                    invalidInputLabel.setText("Value missing!");
                    setInvalidInputLabelVisibleAndFormat();
                    return;
                }
                this.result = new ArithmeticValueTerm(arithmeticValueTypeParameterChoiceBox.getValue(),
                        arithmeticValueTypeValueField.getText(),
                        arithmeticValueTypeArithmeticOperatorChoiceBox.getValue());
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
