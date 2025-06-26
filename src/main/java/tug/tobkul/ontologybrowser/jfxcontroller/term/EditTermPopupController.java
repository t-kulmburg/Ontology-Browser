package tug.tobkul.ontologybrowser.jfxcontroller.term;

import tug.tobkul.ontologybrowser.ontology.model.constraint.term.*;

public class EditTermPopupController extends TermPopupController {
    public void setTermForEdit(Term term) {
        if (term instanceof ParameterTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.PARAMETER);
            parameterTypeEntityChoiceBox.getSelectionModel().select(((ParameterTerm) term).getParameter().getEntity());
            parameterTypeAttributeChoiceBox.getSelectionModel().select(((ParameterTerm) term).getParameter().getAttribute());
        } else if (term instanceof ValueTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.VALUE);
            valueTypeValueField.setText(((ValueTerm) term).getValue());
        } else if (term instanceof ArithmeticParameterTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.A_PARAMETER);
            arithmeticParameterTypeEntityChoiceBoxLhs.getSelectionModel().select(((ArithmeticParameterTerm) term).getLhs().getEntity());
            arithmeticParameterTypeAttributeChoiceBoxLhs.getSelectionModel().select(((ArithmeticParameterTerm) term).getLhs().getAttribute());
            arithmeticParameterTypeEntityChoiceBoxRhs.getSelectionModel().select(((ArithmeticParameterTerm) term).getRhs().getEntity());
            arithmeticParameterTypeAttributeChoiceBoxRhs.getSelectionModel().select(((ArithmeticParameterTerm) term).getRhs().getAttribute());
        } else if (term instanceof ArithmeticValueTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.A_VALUE);
            arithmeticValueTypeEntityChoiceBox.getSelectionModel().select(((ArithmeticValueTerm) term).getLhs().getEntity());
            arithmeticValueTypeAttributeChoiceBox.getSelectionModel().select(((ArithmeticValueTerm) term).getLhs().getAttribute());
            arithmeticValueTypeValueField.setText(((ArithmeticValueTerm) term).getRhs());
        }
    }
}
