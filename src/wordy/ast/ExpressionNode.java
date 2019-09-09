package wordy.ast;

import wordy.interpreter.EvaluationContext;

public abstract class ExpressionNode extends ASTNode {
    public double evaluate(EvaluationContext context) {
        throw new UnsupportedOperationException("not implemented yet for " + getClass());
    }
}
