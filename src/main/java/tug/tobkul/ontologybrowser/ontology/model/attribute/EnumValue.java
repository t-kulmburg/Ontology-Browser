package tug.tobkul.ontologybrowser.ontology.model.attribute;

import java.util.List;

public class EnumValue extends AttributeValue<List<String>> {
    public EnumValue() {
    }

    public EnumValue(List<String> value) {
        super(value);
    }

    @Override
    public List<String> getPossibleValueList() {
        return this.value;
    }
}
