package wordy.ast;

import java.util.Map;
import java.util.Objects;

import static wordy.ast.Utils.orderedMap;

public class AssignmentNode extends StatementNode {
    public final VariableNode variable;
    public final ExpressionNode expression;

    public AssignmentNode(VariableNode variable, ExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return orderedMap(
            "variable", variable,
            "expression", expression);
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
