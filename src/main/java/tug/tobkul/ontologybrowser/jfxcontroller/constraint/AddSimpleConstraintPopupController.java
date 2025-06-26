package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;

public class AddSimpleConstraintPopupController extends SimpleConstraintPopupController {

    public void onConfirm() {
        invalidInputLabel.setVisible(false);
        if (lhs == null || rhs == null) {
            invalidInputLabel.setText("Term missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        if (relationalOperatorChoiceBox.getValue() == null) {
            invalidInputLabel.setText("Operator missing!");
            setInvalidInputLabelVisibleAndFormat();
            return;
        }
        result = new SimpleConstraint(outerSystem, lhs, rhs, relationalOperatorChoiceBox.getValue());
        stage.close();
    }
}
