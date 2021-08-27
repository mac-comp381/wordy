package wordy.demo.shader;

import java.util.Map;

import wordy.ast.StatementNode;
import wordy.compiler.WordyCompiler;
import wordy.compiler.WordyExecutable;

class CompiledPixelComputer implements PixelComputer {
    private final WordyExecutable compiledShader;
    private final WordyExecutable.ExecutionContext context;

    public CompiledPixelComputer(StatementNode program, double viewScale) {
        compiledShader = WordyCompiler.compile(program, "CompiledShader");
        context = compiledShader.createContext();
        if (context.hasVariable("view_scale")) {
            context.set("view_scale", viewScale);
        }
    }

    public double computePixel(double x, double y, ColorComponents result) {
        context.set("x", x);
        context.set("y", y);

        compiledShader.runUnsafe(context);

        result.set(
            context.get("red"),
            context.get("green"),
            context.get("blue"));

        return context.hasVariable("work_done")
            ? context.get("work_done")
            : 1.0;
    }
}
