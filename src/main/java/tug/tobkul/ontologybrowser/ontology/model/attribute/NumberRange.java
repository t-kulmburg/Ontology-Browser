package tug.tobkul.ontologybrowser.ontology.model.attribute;

public class NumberRange {
    private double min;
    private double max;
    private double interval;

    public NumberRange() {
    }

    public NumberRange(double min, double max, double interval) {
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getInterval() {
        return interval;
    }

    public void setInterval(double interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "Range[min=" + min + ", max=" + max + ", interval=" + interval + "]";
    }
}
