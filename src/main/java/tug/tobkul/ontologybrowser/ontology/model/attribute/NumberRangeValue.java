package tug.tobkul.ontologybrowser.ontology.model.attribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberRangeValue extends AttributeValue<NumberRange> {
    public NumberRangeValue() {
    }

    public NumberRangeValue(NumberRange value) {
        super(value);
    }

    @Override
    public List<String> getPossibleValueList() {
        List<String> possibleValues = new ArrayList<>();
        BigDecimal min = BigDecimal.valueOf(value.getMin());
        BigDecimal max = BigDecimal.valueOf(value.getMax());
        BigDecimal interval = BigDecimal.valueOf(value.getInterval());
        BigDecimal temp = min;
        while (temp.compareTo(max) <= 0) {
            possibleValues.add(String.valueOf(temp.intValue()));
            temp = temp.add(interval);
        }
        return possibleValues;
    }
}
