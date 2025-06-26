package tug.tobkul.ontologybrowser.jfxcontroller.constraint.view;

import javafx.scene.Node;
import tug.tobkul.ontologybrowser.ontology.model.constraint.Constraint;

import java.io.IOException;

public interface ConstraintView {
    Node getView();

    Constraint getConstraint();

    void setParent(CompositeConstraintView parent);

    void openEditPopup() throws IOException;

    void makeCompositeRight() throws IOException;

    void makeCompositeLeft() throws IOException;
}
