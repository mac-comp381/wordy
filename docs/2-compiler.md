# Part 2: Implement a Wordy compiler

For this final phase of the homework, you will implement a Wordy-to-Java compiler. Like the interpreter, the compiler will recursively walk the Wordy AST. However, unlike the interpreter, the compiler _does not actually run the Wordy code_. Instead, it outputs Java source code, which a Java development environment could then compile and run…if you wanted it to.

In other words, the way you run a Wordy program using the **interpreter** is like this:

    Wordy code → Wordy interpreter

…but the way you run a Wordy program using the **compiler** is like this:

    Wordy code → Wordy compiler → Java compiler → run Java bytecode

Isn’t that second way more complicated? What is the advantage? Ah, just wait!


## Some context

This part of the assignment will require implementing the `compile()` method for each concrete AST class, much as you overrode `doRun()` and `doExecute()` to implement the interpreter. The difference is that this time, the methods you’re writing will not actually _run_ any of the code; their only job is to _generate text_. (Remember, you are translating Wordy code into Java code.)

There are two things that will help you understand how to write this code:

1. You will generate the code using a `PrintWriter` variable named `out`. How do you use it? It looks a lot like using `System.out`: there are `print()` and `println()` methods. For example, you can say `out.println("some string literal")` or `out.print(someVariable)`. The difference is that instead of printing to standard out (i.e. the thing that shows up in the ouput console in your IDE), you are “printing” to a `String`.

2. All the Java code you’re generating with the code you add will be the body of a single method, inside a larger Java class. The code to generate that larger class is already implemented for you. You can see it in `WordyCompiler` if you want all the ugly details, but there is really just one thing you must understand: your generated Java code will have access to a Java variable named `context`, and it will have one Java instance variable for each variable your Wordy code mentions. Thus the Wordy variable `x` becomes `context.x` in the compiled output.

Putting it all together, suppose you have this Wordy code as input:

    Set x to y plus 1.

Your `compile(PrintWriter out)` methods on various AST nodes would use calls that look like `out.print(" + ");` (for example) to generate Java code something like this:

    context.x = (context.y + 1.0);

…which the already-provided compiler code will then put inside a Java class like this:

```java
import wordy.compiler.WordyExecutable;

public class MyProgram implements WordyExecutable<MyProgram.ExecutionContext> {
    public void run(ExecutionContext context) {
        context.x = (context.y + 1.0);   // ← here is what you generated
    }

    public ExecutionContext createContext() {
        return new ExecutionContext();
    }

    public static class ExecutionContext implements MyProgramContext {
        private double x;
        private double y;

        // other methods omitted for brevity
    }
}
```

…which other Java code could use by creating an `ExecutionContext` object, setting `y` to something, calling the `run()` method, and then reading `x`.

Phew! Overwhelming? Make sense? Post questions on Slack.

If you’re lost, remember the big picture: you are translating Wordy code to Java code. For each kind of Wordy AST node, you need to figure out how to express the same semantics using Java syntax.


## Implementing the compiler

Again, unit tests will guide you:

- Open `CompilerTest` (in `test/wordy/compiler`), and delete the line near the top that says `@Disabled`.
- Run all tests again. You should now see all the compilers tests failing.
- Again, I recommend making the tests pass **one at a time, in the order they appear in the test code**.
- Here are some **hints for specific AST node types**, if you want them. (Give yourself a little time to wrestle first, but don’t hesitate to use the hints whenever you feel stuck!)

  - `ConstantNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `VariableNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `BinaryExpressionNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `AssignmentNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `BlockNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `ConditionalNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `LoopExitNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

  - `LoopNode`:
    <details>
      <summary>?</summary>

      </details>
    </details>

- Still stuck? **Reach out for help on Slack!**

Don’t forget to **commit and push your work**.


## Now, witness the computational power of this fully armed and operational compiler

- Run `Playground` once again. Switch to the compiler tab, and watch as the Wordy code you type on the left transforms into Java code on the right. As always: experiment!
- Run `ShaderUI` again. It is still using the interpreter. Note the speed. Quit the `ShaderUI` app.
- Open up the source code for `ShaderUI`, and change the `USE_COMPILER` flag to `true`.
- Run `ShaderUI` once again.
- Note the new rendering time in your IDE’s console (`Done rendering (___ms)`). How does it compare to the interpreter?
- If you are curious (but only if you are really curious; it’s messy):
    - Look at `CompiledShader` to see how Java code compiles, runs, and communicates with Wordy code.
    - Read `WordyCompiler` to see how an app can compile and runs Java code it just generated from a string.

Did you remember to **commit and push your work**? Double check!


## Congratulations!

Take a moment to check that all the code you wrote is nice and tidy: consistent indentation, reasonable variable names, etc. If it looks good and the tests pass, you’re done! Hooray!

However, there’s a lot more to explore here. If you are interested in learning more about building programming languages, [try some of the bonus challenges](3-bonus.md).
