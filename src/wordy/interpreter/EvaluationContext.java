package wordy.interpreter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import wordy.ast.ASTNode;

public class EvaluationContext {
    private final Map<String,Double> variables = new LinkedHashMap<>();
    private final Tracer tracer;

    public EvaluationContext(Tracer tracer) {
        this.tracer = tracer;
    }

    public EvaluationContext() {
        this((node, ctx, phase, result) -> { });
    }

    public double get(String name) {
        Double result = variables.get(name);
        return (result == null) ? 0 : result;
    }

    public void set(String name, double value) {
        variables.put(name, value);
    }

    public Map<String, Double> allVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public void trace(ASTNode astNode, Tracer.Phase phase) {
        tracer.traceNode(astNode, this, phase, null);
    }

    public void trace(ASTNode astNode, Tracer.Phase phase, Object result) {
        tracer.traceNode(astNode, this, phase, result);
    }

    public interface Tracer {
        void traceNode(ASTNode astNode, EvaluationContext evaluationContext, Phase phase, Object result);

        enum Phase { STARTED, COMPLETED }
    }
}
