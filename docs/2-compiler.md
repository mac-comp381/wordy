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
      <summary>It’s somehow so simple, I don’t even know what to do.</summary>

      You need to output a Java numeric literal…which is just the number. For example, for the Wordy code `3.14`, you need to output the Java text `3.14`.
    </details>

  - `VariableNode`:
    <details>
      <summary>How do I get the value from the context?</summary>

      Remember that you aren’t actually _running_ the code now; you’re outputting Java code. And remember that in the code you output, there will be a Java variable named `context` that has all of the Wordy program’s variables as Java instance variables.

      Study the example above, in the "Some context" section.
    </details>

  - `BinaryExpressionNode`:
    <details>
      <summary>I think I have it working, but the test wants me to have all these parentheses…?</summary>

      ASTs don’t have parentheses at all. Why? Because an AST is already a tree, and all parentheses do in code is make the tree structure explicit.

      However, you are translating the tree back to text, which means you sometimes need to _reintroduce_ parentheses to preserve the semantics. For example, if you convert `x * (y + z)` to an AST, then emit code with no parentheses, you get `x * y + z`, which has a different meaning.

      So, when do you _need_ parentheses? Too much trouble to figure out! The compiled code does not need to be human-readable, and therefore there is no harm in extra parentheses. The tests thus tell you to make _all_ binary expressions emit parentheses, necessary or not, and you’re thus guaranteed that your AST’s structure is always preserved in the Java code.
    </details>

  - `AssignmentNode`:
    <details>
      <summary>Hint for making your code simple</summary>

      `VariableNode` already knows how to compile `x` to `context.x`. Let `VariableNode` do that work; don’t duplicate the work of printing `"context."` in `AssignmentNode`.
    </details>

  - `BlockNode`:
    <details>
      <summary>What’s up with the curly braces the test wants me to add?</summary>

      Same thing as the often-unnecessary parentheses in `BinaryExpressionNode`: it ensures that you preserve the tree structure.
    </details>

  - `ConditionalNode`:
    <details>
      <summary>Wait, now my output has too many curly braces?!</summary>

      `BlockNode` already emits curly braces, so `ConditionalNode` doesn’t _also_ need to emit them.
    </details>

  - `LoopExitNode`:
    <details>
      <summary>Huh? I thought we were supposed to throw an exception for this one.</summary>

      Throwing an exception is how the Wordy _interpreter_ works. But now we are translating Wordy to Java, and Java already has a magic keyword that means “exit the innermost loop I’m currently inside.” That keyword is `break`.
    </details>

  - `LoopNode`:
    <details>
      <summary>Again with the curly braces…?</summary>

      See hint about braces for `ConditionalNode` above.
    </details>

- Still stuck? **Reach out for help on Slack!**

Don’t forget to **commit and push your work**.


## Now, witness the computational power of this fully armed and operational compiler

- Run `Playground` once again. Switch to the compiler tab, and watch as the Wordy code you type on the left transforms into Java code on the right. As always: experiment!
- Run `ShaderUI` again. It is still using the interpreter. Note the speed. Quit the `ShaderUI` app.
- Open up the source code for `ShaderUI`, and change the `USE_COMPILER` flag to `true`.
- Run `ShaderUI` once again.
- Note the new rendering time in your IDE’s console (`Done rendering (___ms)`). How does it compare to the interpreter?
- Yes, wow. That’s the advantage of a compiled language.
- If you are curious (but only if you are really curious; it’s messy):
    - Look at `CompiledShader` to see how Java code compiles, runs, and communicates with Wordy code.
    - Read `WordyCompiler` to see how an app can compile and runs Java code it just generated from a string.

Did you remember to **commit and push your work**? Double check!


## Congratulations!

Take a moment to check that all the code you wrote is nice and tidy: consistent indentation, reasonable variable names, etc. If it looks good and the tests pass, you’re done! Hooray!

However, there’s a lot more to explore here. If you are interested in learning more about building programming languages, [try some of the bonus challenges](3-bonus.md).
