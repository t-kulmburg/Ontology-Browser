package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;

import java.io.IOException;

public class MakeCompositeConstraintRightPopupController extends MakeCompositeConstraintPopupController {
    @Override
    public void setExistingConstraint(Constraint c) {
        this.lhs = c;
        Text text = new Text(c.getExpression().replace(" ", "\n"));
        text.setTextAlignment(TextAlignment.CENTER);
        lhsTextFlow.getChildren().add(text);
    }

    @Override
    public void onAddConstraint() throws IOException {
        Constraint oldConstraint = rhs;
        rhs = getSimpleConstraintFromPopup();
        if (oldConstraint != null && rhs == null) {
            rhs = oldConstraint;
            return;
        }
        rhsTextFlow.getChildren().clear();
        if (rhs != null) {
            rhsTextFlow.getChildren().add(new Text(rhs.getExpression().replace(" ", "\n")));
        }
    }
}
