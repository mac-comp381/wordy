package enshader.parser;

import java.util.Objects;

public class AssignmentStatement extends StatementNode {
    public final VariableNode variable;
    public final ExpressionNode expression;

    public AssignmentStatement(VariableNode variable, ExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        AssignmentStatement that = (AssignmentStatement) o;
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
