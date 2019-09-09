package wordy.interpreter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import wordy.ast.ASTNode;

public class EvaluationContext {
    private final Map<String,Double> variables = new LinkedHashMap<>();
    private final BiConsumer<ASTNode, EvaluationContext> tracer;

    public EvaluationContext(BiConsumer<ASTNode, EvaluationContext> tracer) {
        this.tracer = tracer;
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

    public void trace(ASTNode astNode) {
        tracer.accept(astNode, this);
    }
}
