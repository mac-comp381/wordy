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
            import java.util.Map;
            import java.util.LinkedHashMap;
            
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

                    public Map<String,Double> toMap() {
                        Map<String,Double> result = new LinkedHashMap<>();
            """);
        for(var variable: program.findAllVariables()) {
            out.printf("            result.put(\"%1$s\", %1$s);\n", variable.name);
        }
        out.print(
            """
                        return result;
                    }
            
                    public void updateFromMap(Map<String,Double> values) {
            """);
        for(var variable: program.findAllVariables()) {
            out.printf(
                """
                            if (values.containsKey("%1$s"))
                                %1$s = values.get("%1$s");
                """, variable.name);
        }
        out.print(
            """
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

    public static void main(String[] args) throws Exception {
        var executable = compile(WordyParser.parseProgram("Set x to y plus 2."), "TestProgram");
        var context = executable.createContext();
        context.updateFromMap(Map.of("y", 7.0));
        executable.runUnsafe(context);
        System.out.println("––––––––––––––––––––––");
        System.out.println(context.toMap());
        System.out.println("––––––––––––––––––––––");
    }
}
