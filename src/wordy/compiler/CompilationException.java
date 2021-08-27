package wordy.compiler;

import wordy.ast.StatementNode;

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
