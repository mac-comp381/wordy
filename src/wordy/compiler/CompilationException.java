package wordy.compiler;

import wordy.ast.StatementNode;

/**
 * An error that occurred duration compilation of a Wordy program _after_ parsing. This usually
 * indicates the the generated Java code was invalid.
 */
public class CompilationException extends RuntimeException {
    public CompilationException(Exception e, StatementNode wordySource, String javaSource) {
        super(
            "Unable to compile Wordy source code: " + e
            + "\n━━━━━━━━━━ WORDY AST ━━━━━━━━━━━\n"
            + wordySource.dump()
            + "\n━━━━━━ COMPILED JAVA CODE ━━━━━━\n"
            + javaSource
            + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
