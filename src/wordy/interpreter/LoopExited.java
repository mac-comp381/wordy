package wordy.interpreter;

/**
 * Causes the Wordy interpreter to exit the current innermost nested loop.
 * Thrown by {@link wordy.ast.LoopExitNode}.
 */
public class LoopExited extends RuntimeException {
}
