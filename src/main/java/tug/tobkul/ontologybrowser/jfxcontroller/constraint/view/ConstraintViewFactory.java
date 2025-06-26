package tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;

import tug.tobkul.ontologybrowser.jfxcontroller.constraint.AddConstraintPopupController;
import tug.tobkul.ontologybrowser.ontology.model.constraint.CompositeConstraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;
import tug.tobkul.ontologybrowser.ontology.model.constraint.SimpleConstraint;

public class ConstraintViewFactory {
    public static ConstraintView create(AddConstraintPopupController c, Constraint constraint) {
        if (constraint instanceof SimpleConstraint sc) {
            return new SimpleConstraintView(c, sc);
        } else if (constraint instanceof CompositeConstraint cc) {
            return new CompositeConstraintView(c, cc);
        } else {
            throw new IllegalArgumentException("Unknown constraintView type");
        }
    }
}

