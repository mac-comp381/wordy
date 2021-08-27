package wordy.ast;

import java.io.PrintWriter;

import wordy.interpreter.EvaluationContext;

public abstract class StatementNode extends ASTNode {
    public void run(EvaluationContext context) {
        throw new UnsupportedOperationException("Interpreter not implemented yet for " + getClass().getSimpleName());
    }
}
