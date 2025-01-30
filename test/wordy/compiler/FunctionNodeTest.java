package wordy.compiler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import wordy.ast.BlockNode;
import wordy.ast.FunctionCallNode;
import wordy.ast.FunctionNode;

public class FunctionNodeTest {

    public static void main(String[] args) {
        // testFunctionNodeCompilation();
    }

    // public static void testFunctionNodeCompilation() {
    //     FunctionCallNode functionCall = new FunctionCallNode("doSomething");

    //     BlockNode body = new BlockNode();

    //     FunctionNode functionNode = new FunctionNode("myFunction", body);

    //     StringWriter stringWriter = new StringWriter();
    //     PrintWriter writer = new PrintWriter(stringWriter);
    //     functionNode.compile(writer);
    //     writer.flush();

    //     // expect to see
    //     String expected = "function myFunction() {doSomething();}";

    
    //     if (stringWriter.toString().trim().equals(expected)) {
    //         System.out.println("FunctionNode compile test passed!");
    //     } else {
    //         System.err.println("FunctionNode compile test failed!");
    //         System.err.println("Expected: " + expected);
    //         System.err.println("Actual: " + stringWriter.toString().trim());
    //     }
    // }
}




