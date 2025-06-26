package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.scene.text.Text;
import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;

public class EditSimpleConstraintPopupController extends SimpleConstraintPopupController {
    public void setConstraintForEdit(SimpleConstraint constraint) {
        result = constraint;
        //lhs
        lhs = constraint.getLhs();
        lhsTextFlow.getChildren().clear();
        if (lhs != null) {
            lhsTextFlow.getChildren().add(new Text(lhs.getExpression()));
            setRelationalOperatorChoiceBoxFromTerm(lhs);
            relationalOperatorChoiceBox.getSelectionModel().select(constraint.getRelationalOperator());
        }
        //rhs
        rhs = constraint.getRhs();
        rhsTextFlow.getChildren().clear();
        if (rhs != null) {
            rhsTextFlow.getChildren().add(new Text(rhs.getExpression()));
        }
    }

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
        result.setLhs(lhs);
        result.setRhs(rhs);
        result.setRelationalOperator(relationalOperatorChoiceBox.getValue());
        stage.close();
    }
}
