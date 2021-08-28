package wordy.compiler;

import net.openhft.compiler.CompilerUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import wordy.ast.StatementNode;
import wordy.demo.shader.ShaderExecutionContext;
import wordy.parser.WordyParser;

public class WordyCompiler {
    public static void compile(StatementNode program, String className, String contextInterfaceName, PrintWriter out) {
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

                public static class ExecutionContext implements %s {
            """.formatted(contextInterfaceName)
        );
        for(var variable: program.findAllVariables()) {
            out.println("        private double " + variable.name + ";");
        }
        out.println();
        for(var variable: program.findAllVariables()) {
            out.println(
                """
                        public double get_%1$s() {
                            return %1$s;
                        }

                        public void set_%1$s(double %1$s) {
                            this.%1$s = %1$s;
                        }
                """.formatted(variable.name)
            );
        }
        out.print(
            """
                }
            }
            """
        );
    }

    public static <Context extends WordyExecutable.ExecutionContext> WordyExecutable<Context> compile(
        StatementNode program,
        String className,
        Class<Context> executionContextInterface
    ) {
        var javaSource = new StringWriter();
        compile(program, className, executionContextInterface.getCanonicalName(), new PrintWriter(javaSource));
        try {
            //noinspection unchecked
            var compiledClass = (Class<? extends WordyExecutable<Context>>)
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
