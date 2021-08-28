package wordy.compiler;

import java.util.Map;

public interface WordyExecutable<Context extends WordyExecutable.ExecutionContext> {
    Context createContext();

    void run(Context context);

    interface ExecutionContext { }
}
