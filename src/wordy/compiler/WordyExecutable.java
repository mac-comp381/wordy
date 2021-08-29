package wordy.compiler;

import wordy.ast.StatementNode;

/**
 * A compiled Wordy program. To run it, use the createContext() method to create an object that will
 * hold the values of the program’s variables, then run the program with run().
 * 
 * @see WordyCompiler#compile(StatementNode, String, Class) for detailed usage instructions 
 */
public interface WordyExecutable<Context extends WordyExecutable.ExecutionContext> {
    Context createContext();

    void run(Context context);

    interface ExecutionContext { }
}
