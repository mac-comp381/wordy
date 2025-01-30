package wordy.interpreter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import wordy.ast.ASTNode;
import wordy.ast.BlockNode;
import wordy.ast.FunctionNode;

/**
 * Holds the values of a Wordy program’s variables before, during, and after interpreted execution.
 * {@link wordy.ast.VariableNode}s read and write from this context.
 *
 * Also allows an optional Tracer, which receives notifications about the interpreter’s progress.
 * Tracing allows a debug UI to log program execution, and allows a UI to cancel execution early
 * by throwing an exception.
 *
 * @see wordy.compiler.WordyExecutable.ExecutionContext for the compiler counterpart to this class
 */
public class EvaluationContext {
    private final Map<String,Double> variables = new LinkedHashMap<>();
    private final Map<String,FunctionNode> functions = new LinkedHashMap<>();
    private final Tracer tracer;

    public EvaluationContext(Tracer tracer) {
        this.tracer = tracer;
    }

    public EvaluationContext() {
        this((node, ctx, phase, result) -> { });
    }

    /**
     * Returns the current value of the variable with the given name.
     */
    public double get(String name) {
        Double result = variables.get(name);
        return (result == null) ? 0 : result;
    }

    /**
     * Returns the current node correspond to the function with the given name.
     */
    public FunctionNode retrieve(String name) {
        FunctionNode result = functions.get(name);
        return (result == null) ? new FunctionNode(" ", BlockNode.EMPTY) : result;
    }

    /**
     * Changes the current value of the variable with the given name.
     */
    public void set(String name, double value) {
        variables.put(name, value);
    }

    /**
     * Changes the current node of the function with the given name.
     */
    public void define(String name, FunctionNode definition) {
        functions.put(name, definition);
    }   

    public Map<String, Double> allVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, FunctionNode> allFunctions() {
        return Collections.unmodifiableMap(functions);
    }

    public void trace(ASTNode astNode, Tracer.Phase phase) {
        tracer.traceNode(astNode, this, phase, null);
    }

    public void trace(ASTNode astNode, Tracer.Phase phase, Object result) {
        tracer.traceNode(astNode, this, phase, result);
    }

    /**
     * Receives notification about the progress of the Wordy interpreter as it executes a program.
     */
    public interface Tracer {
        void traceNode(ASTNode astNode, EvaluationContext evaluationContext, Phase phase, Object result);

        enum Phase { STARTED, COMPLETED }
    }
}
