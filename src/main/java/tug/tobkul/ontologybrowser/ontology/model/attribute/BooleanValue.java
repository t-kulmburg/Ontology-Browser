package tug.tobkul.ontologybrowser.ontology.model.attribute;

import java.util.ArrayList;
import java.util.List;

public class BooleanValue extends AttributeValue<Boolean> {
    public BooleanValue() {
    }

    public BooleanValue(Boolean value) {
        super(value);
    }

    @Override
    public List<String> getPossibleValueList() {
        return new ArrayList<>(List.of("true", "false"));
    }
}
