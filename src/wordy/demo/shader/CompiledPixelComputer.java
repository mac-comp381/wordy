package wordy.demo.shader;

import wordy.ast.StatementNode;

class CompiledPixelComputer implements PixelComputer {
    private final CompiledShader compiledShader;
    private final CompiledShader.EvaluationContext context;

    public CompiledPixelComputer(StatementNode program, double viewScale) {
        new CompiledShader();

        context = new CompiledShader.EvaluationContext();
        context.view_scale = viewScale;

        compiledShader = new CompiledShader();
    }

    public double computePixel(double x, double y, ColorComponents result) {
        context.x = x;
        context.y = y;

        compiledShader.run(context);

        result.set(
            context.red,
            context.green,
            context.blue);

        return context.work_done;
    }
}
