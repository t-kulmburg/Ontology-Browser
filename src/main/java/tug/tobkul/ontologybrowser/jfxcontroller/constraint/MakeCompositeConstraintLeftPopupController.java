package tug.tobkul.ontologybrowser.jfxcontroller.constraint;

import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;

import java.io.IOException;

public class MakeCompositeConstraintLeftPopupController extends MakeCompositeConstraintPopupController {
    @Override
    public void setExistingConstraint(Constraint c) {
        this.rhs = c;
        Text text = new Text(c.getExpression().replace(" ", "\n"));
        text.setTextAlignment(TextAlignment.CENTER);
        rhsTextFlow.getChildren().add(text);
    }

    @Override
    public void onAddConstraint() throws IOException {
        Constraint oldConstraint = lhs;
        lhs = getSimpleConstraintFromPopup();
        if (oldConstraint != null && lhs == null) {
            lhs = oldConstraint;
            return;
        }
        lhsTextFlow.getChildren().clear();
        if (lhs != null) {
            lhsTextFlow.getChildren().add(new Text(lhs.getExpression().replace(" ", "\n")));
        }
    }
}
