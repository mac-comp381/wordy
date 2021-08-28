package wordy.demo.shader;

import java.util.Map;

import wordy.ast.StatementNode;
import wordy.compiler.WordyCompiler;
import wordy.compiler.WordyExecutable;

class CompiledPixelComputer implements PixelComputer {
    private final WordyExecutable<ShaderExecutionContext> compiledShader;
    private final ShaderExecutionContext context;

    public CompiledPixelComputer(StatementNode program, double viewScale) {
        compiledShader = WordyCompiler.compile(program, "CompiledShader", ShaderExecutionContext.class);
        context = compiledShader.createContext();
        context.set_view_scale(viewScale);
    }

    public double computePixel(double x, double y, ColorComponents result) {
        context.set_x(x);
        context.set_y(y);

        compiledShader.run(context);

        result.set(
            context.get_red(),
            context.get_green(),
            context.get_blue());

        return context.get_work_done();
    }
}
