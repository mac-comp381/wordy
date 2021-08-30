package wordy.demo.shader;

import wordy.compiler.WordyExecutable;

/**
 * The variables passed to and from a compiled Wordy-based shader.
 */
public interface ShaderExecutionContext extends WordyExecutable.ExecutionContext {
    // –––––– Shader inputs ––––––

    void set_x(double x);
    void set_y(double y);
    default void set_view_scale(double view_scale) {
        // optional
    }

    // –––––– Shader outputs ––––––

    double get_red();
    double get_green();
    double get_blue();
    default double get_work_done() {
        return 1;  // optional
    }
}
