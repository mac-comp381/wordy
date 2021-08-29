package wordy.demo.shader;

import wordy.ast.StatementNode;
import wordy.compiler.WordyCompiler;
import wordy.compiler.WordyExecutable;

/**
 * A shader backed by compiled Wordy code.
 */
class CompiledShader implements Shader {
    private final WordyExecutable<ShaderExecutionContext> logic;
    private final ShaderExecutionContext context;

    public CompiledShader(StatementNode program, double viewScale) {
        logic = WordyCompiler.compile(program, "CompiledShaderLogic", ShaderExecutionContext.class);
        context = logic.createContext();
        context.set_view_scale(viewScale);
    }

    public double computePixelColor(double x, double y, ColorComponents result) {
        context.set_x(x);
        context.set_y(y);

        logic.run(context);

        result.set(
            context.get_red(),
            context.get_green(),
            context.get_blue());

        return context.get_work_done();
    }
}
