package wordy.compiler;

import net.openhft.compiler.CompilerUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import wordy.ast.StatementNode;
import wordy.parser.WordyParser;

public class WordyCompiler {
    public static void compile(StatementNode program, String className, PrintWriter out) {
        out.print(
            """
            import wordy.compiler.WordyExecutable;
            
            public class %1$s implements WordyExecutable<%1$s.ExecutionContext> {
                public void run(ExecutionContext context) {
            """.formatted(className)
        );

        program.compile(  // the magic happens here
            new IndentingPrintWriter(out, "        "));

        out.print(
            """
                }
                
                public ExecutionContext createContext() {
                    return new ExecutionContext();
                }

                public static class ExecutionContext implements WordyExecutable.ExecutionContext {
            """
        );
        for(var variable: program.findAllVariables()) {
            out.println("        public double " + variable.name + ";");
        }
        out.print(
            """

                    public boolean hasVariable(String variable) {
            """);
        for(var variable: program.findAllVariables()) {
            out.printf(
                """
                            if ("%1$s".equals(variable)) {
                                return true;
                            }
                """, variable.name);
        }
        out.print(
            """
                        return false;
                    }

                    public Double get(String variable) {
            """);
        for(var variable: program.findAllVariables()) {
            out.printf(
                """
                            if ("%1$s".equals(variable)) {
                                return %1$s;
                            }
                """, variable.name);
        }
        out.print(
            """
                        throw new IllegalArgumentException("No such variable: " + variable);
                    }
            
                    public void set(String variable, Double value) {
            """);
        for(var variable: program.findAllVariables()) {
            out.printf(
                """
                            if ("%1$s".equals(variable)) {
                                %1$s = value;
                                return;
                            }
                """, variable.name);
        }
        out.print(
            """
                        throw new IllegalArgumentException("No such variable: " + variable);
                    }
                }
            }
            """
        );
    }

    public static WordyExecutable<?> compile(StatementNode program, String className) {
        var javaSource = new StringWriter();
        compile(program, className, new PrintWriter(javaSource));
        try {
            //noinspection unchecked
            var compiledClass = (Class<? extends WordyExecutable<?>>)
                CompilerUtils.CACHED_COMPILER.loadFromJava(
                    WordyCompiler.class.getClassLoader(),
                    className,
                    javaSource.toString());
            return compiledClass.getDeclaredConstructor().newInstance();
        } catch(Exception e) {
            throw new CompilationException(e, program, javaSource.toString());
        }
    }
}
