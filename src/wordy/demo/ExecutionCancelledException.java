package wordy.demo;

/**
 * Used by both Playground and ShaderUI to halt execution of a Wordy program early in response to
 * user input. (This is essential for the Playground, whose interpreter would otherwise hang
 * indefinitely as soon as the user types syntactically valid code that forms an infinite loop.)
 */
public class ExecutionCancelledException extends RuntimeException {
}
