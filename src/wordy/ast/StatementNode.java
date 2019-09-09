package wordy.ast;

import wordy.interpreter.EvaluationContext;

public abstract class StatementNode extends ASTNode {
    public void run(EvaluationContext context) {
        throw new UnsupportedOperationException("not implemented yet for " + getClass());
    }
}
