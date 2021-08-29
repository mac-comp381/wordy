package wordy.demo.shader;

import wordy.ast.StatementNode;
import wordy.interpreter.EvaluationContext;

/**
 * A shader backed by interpreted Wordy code.
 */
class InterpretedShader implements Shader {
    private final StatementNode program;
    private final EvaluationContext context;

    public InterpretedShader(StatementNode program, double viewScale) {
        this.program = program;

        context = new EvaluationContext();
        context.set("view_scale", viewScale);
    }

    public double computePixelColor(double x, double y, ColorComponents result) {
        context.set("x", x);
        context.set("y", y);

        program.run(context);

        result.set(
            context.get("red"),
            context.get("green"),
            context.get("blue"));

        return context.get("work_done");
    }
}
