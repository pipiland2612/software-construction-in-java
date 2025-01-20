package expressivo;

import java.util.Map;
import java.util.Objects;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        if (name == null || name.isEmpty() || !name.matches("[A-Za-z]+"))
            throw new IllegalArgumentException("Wrong param for variable");
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable other)) return false;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public Expression differentiate(String variable) {
        return variable.equals(this.name) ? new Number(1) : new Number(0);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        if (environment.containsKey(this.name)) {
            double value = environment.get(this.name);
            return new Number(value);
        }
        return this;
    }

    @Override
    public Expression subtitute(Map<String, Double> environment) {
        if (environment.containsKey(this.name)) {
            double value = environment.get(this.name);
            return new Number(value);
        }
        return this;
    }
}
