package wordy.ast;

import wordy.interpreter.EvaluationContext;

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
