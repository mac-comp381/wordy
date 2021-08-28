package wordy.ast;

import wordy.interpreter.EvaluationContext;

public abstract class ExpressionNode extends ASTNode {
    public final double evaluate(EvaluationContext context) {
        context.trace(this, EvaluationContext.Tracer.Phase.STARTED);
        Double result = null;
        try {
            result = doEvaluate(context);
            return result;
        } finally {
            context.trace(this, EvaluationContext.Tracer.Phase.COMPLETED, result);
        }
    }

    public double doEvaluate(EvaluationContext context) {
        throw new UnsupportedOperationException("Interpreter not implemented yet for " + getClass().getSimpleName());
    }
}
