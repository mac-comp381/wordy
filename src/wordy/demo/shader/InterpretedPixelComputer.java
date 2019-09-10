package wordy.demo.shader;

import wordy.ast.StatementNode;
import wordy.interpreter.EvaluationContext;

class InterpretedPixelComputer implements PixelComputer {
    private final StatementNode program;
    private final EvaluationContext context;

    public InterpretedPixelComputer(StatementNode program, double viewScale) {
        this.program = program;

        context = new EvaluationContext();
        context.set("view_scale", viewScale);
    }

    public double computePixel(double x, double y, ColorComponents result) {
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
