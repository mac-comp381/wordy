package wordy.ast;

import wordy.interpreter.EvaluationContext;

/**
 * A Wordy abstract syntax subtree that will evaluate to a specific value when the program runs.
 * 
 * Wordy only supports double-precision floating point expressions; all ExpressionNodes evaluate
 * to a double.
 */
public abstract class StatementNode extends ASTNode {
    public final void run(EvaluationContext context) {
        context.trace(this, EvaluationContext.Tracer.Phase.STARTED);
        try {
            doRun(context);
        } finally {
            context.trace(this, EvaluationContext.Tracer.Phase.COMPLETED);
        }
    }

    public void doRun(EvaluationContext context) {
        throw new UnsupportedOperationException("Interpreter not implemented yet for " + getClass().getSimpleName());
    }
}
