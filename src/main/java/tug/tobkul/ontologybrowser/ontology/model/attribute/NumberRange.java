package tug.tobkul.ontologybrowser.ontology.model.attribute;

public class NumberRange {
    private int min;
    private int max;
    private int interval;

    public NumberRange() {
    }

    public NumberRange(int min, int max, int interval) {
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "Range[min=" + min + ", max=" + max + ", interval=" + interval + "]";
    }
}
