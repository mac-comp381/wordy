package enshader.ast;

import java.util.Objects;

public class AssignmentNode extends StatementNode {
    public final VariableNode variable;
    public final ExpressionNode expression;

    public AssignmentNode(VariableNode variable, ExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        AssignmentNode that = (AssignmentNode) o;
        return variable.equals(that.variable)
            && expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, expression);
    }

    @Override
    public String toString() {
        return "AssignmentStatement{"
            + "variable='" + variable + '\''
            + ", expression=" + expression
            + '}';
    }
}
