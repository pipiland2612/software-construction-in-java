package expressivo;

import java.util.Map;
import java.util.Objects;

public class Sum implements Expression {
    private final Expression left;
    private final Expression right;

    public Sum(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("(%s + %s)", left.toString(), right.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sum other))
            return false;
        return this.left.equals(other.left) && this.right.equals(other.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public Expression differentiate(String variable) {
        return new Sum(left.differentiate(variable), right.differentiate(variable));
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression leftSubtitute = left.subtitute(environment);
        Expression rightSubtitute = right.subtitute(environment);

        if (leftSubtitute instanceof Number && rightSubtitute instanceof Number) {
            double leftValue = ((Number) leftSubtitute).getValue();
            double rightValue = ((Number) rightSubtitute).getValue();
            return new Number(leftValue + rightValue);
        }

        return new Sum(leftSubtitute, rightSubtitute);
    }

    public Expression subtitute(Map<String, Double> environment) {
        return new Sum(left.subtitute(environment), right.subtitute(environment));
    }
}
