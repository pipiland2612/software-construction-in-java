package expressivo;

import java.util.Objects;

public class Addition implements Expression {
    private final Expression left;
    private final Expression right;

    public Addition(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("(%s + %s)", left.toString(), right.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Addition other))
            return false;
        return this.left.equals(other.left) && this.right.equals(other.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
