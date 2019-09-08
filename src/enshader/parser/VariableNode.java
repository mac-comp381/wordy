package enshader.parser;

import java.util.Objects;

public class VariableNode extends ExpressionNode {
    public final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        VariableNode that = (VariableNode) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VariableNode{name='" + name + '\'' + '}';
    }
}
