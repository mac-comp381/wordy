package enshader.ast;

import java.util.Objects;

public final class ConstantNode extends ExpressionNode {
    public final double value;

    public ConstantNode(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        ConstantNode other = (ConstantNode) o;
        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ConstantNode{value=" + value + '}';
    }
}
