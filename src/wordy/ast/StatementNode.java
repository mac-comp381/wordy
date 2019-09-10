package wordy.ast;

import java.io.PrintWriter;

import wordy.interpreter.EvaluationContext;

public abstract class StatementNode extends ASTNode {
    public void run(EvaluationContext context) {
        throw new UnsupportedOperationException("not implemented yet for " + getClass());
    }

    public void compileProgram(String className, PrintWriter out) {
        out.println("class " + className + " {");
        out.println("    public void run(EvaluationContext context) {");
        compile(out);
        out.println("    }");
        out.println();
        out.println("    public static class EvaluationContext {");
        for(var variable: findAllVariables()) {
            out.println("        public double " + variable.name + ";");
        }
        out.println("    }");
        out.println("}");
    }
}
