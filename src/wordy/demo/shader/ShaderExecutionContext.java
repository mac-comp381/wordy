package wordy.demo.shader;

import wordy.compiler.WordyExecutable;

public interface ShaderExecutionContext extends WordyExecutable.ExecutionContext {
    void set_x(double x);
    void set_y(double y);
    default void set_view_scale(double view_scale) {
        // optional
    }

    double get_red();
    double get_green();
    double get_blue();
    default double get_work_done() {
        return 1;
    }
}
