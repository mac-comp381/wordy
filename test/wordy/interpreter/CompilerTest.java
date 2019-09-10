package wordy.interpreter;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import wordy.ast.ASTNode;
import wordy.ast.ConstantNode;
import wordy.ast.VariableNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wordy.parser.WordyParser.parseExpression;
import static wordy.parser.WordyParser.parseProgram;
import static wordy.parser.WordyParser.parseStatement;

public class CompilerTest {
    @Test
    void compileConstant() {
        assertCompilationEquals("-34.13", new ConstantNode(-34.13));
    }

    @Test
    void compileVariable() {
        assertCompilationEquals("context.foo", new VariableNode("foo"));
    }

    @Test
    void compileBinaryExpression() {
        assertCompilationEquals("(context.x + 1.0)", parseExpression("x plus 1"));
        assertCompilationEquals("(2.0 - (context.x + 3.0))", parseExpression("2 minus (x plus 3)"));
        assertCompilationEquals("((2.0 - context.x) + 3.0)", parseExpression("2 minus x plus 3"));
        assertCompilationEquals("((1.0 * 2.0) / 3.0)", parseExpression("1 times 2 divided by 3"));
        assertCompilationEquals(
            "(Math.pow(context.x,3.0)+Math.pow(context.y,2.0))",
            parseExpression("x to the power of 3 plus y squared"));
    }

    @Test
    void compileAssignment() {
        assertCompilationEquals("context.foo = 10.0;", parseStatement("set foo to 10"));
        assertCompilationEquals(
            "context.foo = (context.bar - 10.0);",
            parseStatement("set foo to bar minus 10"));
    }

    @Test
    void compileBlock() {
        assertCompilationEquals(
            "{ context.x = 17.0; context.y = Math.pow(context.x, 2.0); }",
            parseProgram("set x to 17. set y to x squared."));
    }

    @Test
    void compileConditional() {
        assertCompilationEquals(
            "if(context.x < 12.0) context.a = context.x; else context.b = 0.0;",
            parseStatement("if x is less than 12 then set a to x else set b to 0"));
        assertCompilationEquals(
            "if(context.x < 12.0) context.a = context.x; else {}",
            parseStatement("if x is less than 12 then set a to x"));
        assertCompilationEquals(
            "if(context.x < 12.0) {"
                + " context.a = context.x; context.b = 1.0; }"
                + " else { context.a = 1.0; context.b = context.x; }",
            parseStatement(
                "if x is less than 12 then:"
                + " set a to x. set b to 1."
                + " else: set a to 1. set b to x."
                + " end of conditional"));
    }

    @Test
    void compileLoopExit() {
        assertCompilationEquals(
            "break;",
            parseStatement("exit loop"));
    }

    @Test
    void compileLoop() {
        assertCompilationEquals(
            "while(true) { context.x = (context.x + 1.0); }",
            parseStatement("loop: set x to x plus 1. end of loop"));
    }

    // ––––––– Helpers –––––––

    private void assertCompilationEquals(String expected, ASTNode ast) {
        var result = new StringWriter();
        var out = new PrintWriter(result);
        ast.compile(out);
        assertEquals(normalizeWhitespace(expected), normalizeWhitespace(result.toString()));
    }

    private String normalizeWhitespace(String code) {
        return code
            .replaceAll("([^A-Za-z]|^)\\s+", "$1")
            .replaceAll("\\s+([^A-Za-z]|^)", "$1")
            .replaceAll("\\s+", " ");
    }
}
