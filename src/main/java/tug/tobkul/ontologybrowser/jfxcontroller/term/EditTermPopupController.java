package tug.tobkul.ontologybrowser.jfxcontroller.term;

import tug.tobkul.ontologybrowser.ontology.model.constraint.term.*;

public class EditTermPopupController extends TermPopupController {
    public void setTermForEdit(Term term) {
        if (term instanceof ParameterTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.PARAMETER);
            parameterTypeParameterChoiceBox.getSelectionModel().select(((ParameterTerm) term).getParameter());
        } else if (term instanceof ValueTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.VALUE);
            valueTypeValueField.setText(((ValueTerm) term).getValue());
        } else if (term instanceof ArithmeticParameterTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.A_PARAMETER);
            arithmeticParameterTypeParameterChoiceBoxLhs.getSelectionModel()
                    .select(((ArithmeticParameterTerm) term).getLhs());
            arithmeticParameterTypeParameterChoiceBoxRhs.getSelectionModel()
                    .select(((ArithmeticParameterTerm) term).getRhs());
        } else if (term instanceof ArithmeticValueTerm) {
            termTypeChoiceBox.getSelectionModel().select(TermType.A_VALUE);
            arithmeticValueTypeParameterChoiceBox.getSelectionModel().select(((ArithmeticValueTerm) term).getLhs());
            arithmeticValueTypeValueField.setText(((ArithmeticValueTerm) term).getRhs());
        }
    }
}
