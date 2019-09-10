package wordy.demo.shader;

class CompiledShader {
    public void run(EvaluationContext context) {
        context.red = context.x;
        context.green = context.y;
        context.blue = Math.hypot(context.x, context.y);
    }

    public static class EvaluationContext {
        public double x, y, view_scale;
        public double red, green, blue, work_done;
    }
}
