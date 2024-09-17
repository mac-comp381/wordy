package wordy.ast;

import wordy.interpreter.EvaluationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A variable reference (e.g. “x”) in a Wordy abstract syntax tree. Note that this is a variable
 * _usage_; Wordy does not have variable _declarations_.
 * 
 * This expression evaluates to the current value of the variable.
 */
public class VariableNode extends ExpressionNode {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * The name of the variable whose value this expression retrieves.
     */
    public String getName() {
        return name;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return Collections.emptyMap();
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
        return "VariableNode" + describeAttributes();
    }

    @Override
    protected String describeAttributes() {
        return "(name=\"" + name + "\")";
    }

    @Override
    protected double doEvaluate(EvaluationContext context) {
        return context.get(getName());
    }
}
