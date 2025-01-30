package wordy.ast;

import static wordy.ast.Utils.orderedMap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import wordy.interpreter.EvaluationContext;

public class FunctionCallNode extends ExpressionNode {
    private final String name;
    private EvaluationContext functionContext = new EvaluationContext();

    public FunctionCallNode(String name) {
        this.name = name;
        functionContext.set("result", 0);
    }

    @Override
    protected double doEvaluate(EvaluationContext context) {
        Map<String, Double> globalVars = context.allVariables();
        Map<String, FunctionNode> globalFunctions = context.allFunctions();
        for (String key : globalVars.keySet()) {
            functionContext.set(key, globalVars.get(key));
        }
        for (String key : globalFunctions.keySet()) {
            if (key != name) {
                functionContext.define(key, globalFunctions.get(key));
            }
        }
        context.retrieve(name).call(functionContext);
        return functionContext.get("result");
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return Collections.emptyMap();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        FunctionCallNode that = (FunctionCallNode) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, functionContext);
    }

    @Override
    public void compile(PrintWriter out) {
        // Generate the method call
        out.print(name + "()"); // Assume no parameters
    }
    
    // @Override
    // public void compile(PrintWriter out) {
    //     out.print(name + "()");
    // }
}

