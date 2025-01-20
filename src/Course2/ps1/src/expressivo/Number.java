package expressivo;

import java.util.Objects;

public class Number implements Expression {
    private final double value;

    public Number(double value) {
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Wrong params for number");
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Number other)) return false;
        return Double.compare(value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Expression differentiate(String variable) {
        return new Number(0);
    }
}
