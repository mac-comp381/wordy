# Part 1: Implement a Wordy interpreter

For this phase of the homework, you will implement a Wordy interpreter. The interpreter will work by recursively walking the Wordy AST: each AST node knows how to do its own job, and will call its children when it’s time for them to do their jobs.

This will involve implementing the `doEvaluate` method for subclasses of `ExpressionNode`, and the `doRun` method for subclasses of `StatementNode`.
<details>
  <summary>If you are curious: Why are there two different methods to override for expressions versus statements?</summary>

  Expressions evaluate to a value when the code runs, i.e. they pass a value up the tree. Statements do not output a value; they just run.

  The Wordy AST draws this distinction in its class hierarchy to help keep you from making mistakes. You can’t accidentally make a statement evaluate to something because the return type of `doRun` is `void`. Conversely, you can’t accidentally make an expression _not_ return a value, because the return type of `doEvaluate` is `double`.

  Not all ASTs for all languages draw this distinctions. In some languages, _everything_ is an expression. And some ASTs might use a type system to handle this (“a void-returning node”) instead of using separate method definitions. This is just how Wordy does it.
</details>
<details>
  <summary>If you are curious: Why is there both a `run` and a `doRun` method? An `evaluate` and `doEvaluate`?</summary>

  The `run` and `evaluate` methods do work common to all statements and expressions: they report execution practice back to a `Tracer`, which allows the playground to show you how the program executed. They call the `doRun` and `doEvaluate` method to do the work that is specific to each kind of node.
 
  So, for example, if `a` is the parent of `b` in the AST, then you get this call structure:
 
     a.run() → a.doRun() → b.run() → b.doRun()
</details>
<br>


## Implementing the interpreter

- This project includes unit tests that will guide you. Open `InterpreterTest` (in `test/wordy/interpreter`), and delete the line near the top that says `@Disabled`.
- Run all tests again. You should now see all the interpreter tests failing. (Remember, when doing test-driven development: red first, then green!)
- I recommend making the tests pass **one at a time, in the order they appear in the test code** (i.e. starting with `evaluateConstant`). They are ordered so as to guide you along a good implementation path.
- For each test that fails, **look at the error message**. It will tell you which AST class does not yet have the method necessary for it to support interpreted execution.
- Many of the methods will be quite easy to implement — even a single line! Don’t second-guess yourself. Sometimes simple is correct.
- Here are some **hints for specific AST node types**, if you want them. (Give yourself a little time to wrestle first, but don’t hesitate to use the hints whenever you feel stuck!)

  - `ConstantNode`:
    <details>
      <summary>Wait, what method am I supposed to implement?</summary>

      What kind of class is this: a `StatementNode` or an `ExpressionNode`? Check the instructions at the top of part 1 again. What method do you implement for this kind of node?
      
      - <details>
        <summary>Nope, I need more of a hint than that / need help with the syntax.</summary>

        Override `doEvaluate` from the superclass. Your IDE can help you fill it in. It will look like this:
        ```java
        @Override
        protected double doEvaluate(EvaluationContext context) {
            // your implementation goes here
        }
        ```
      </details>
    </details>
    <details>
      <summary>What is it supposed to return?</summary>

      Well, for example, the ConstantNode `2` should _always_ return the number 2 when it’s evaluated. What instance variable holds the 2? (If `2` appears in Wordy code in the playground, what does the corresponding AST node look like? Where does the `2` show up in the AST view?)
    </details>

  - `VariableNode`:
    <details>
      <summary>Where do I get the variable’s value?</summary>

      The `EvaluationContext` class holds the current values of variables. Give it a variable name, and it will give you its current value.
    </details>

  - `BinaryExpressionNode`:
    <details>
      <summary>Where do I get the two values I’m supposed to add / subtract / whatever?</summary>

      You first need to _evaluate_ the left hand side, then _evaluate_ the right hand side, then combine the results.
    </details>

  - `AssignmentNode`:
    <details>
      <summary>What do I do with the `expression`?</summary>

      You need to evaluate it, just like the BinaryExpressionNode.
    </details>
    <details>
      <summary>What do I do with the `variable`?</summary>

      You need to ask it for its name. Contexts look up variables by name.
    </details>
    <details>
      <summary>OK, but how do I _set_ it? `VariableNode` doesn’t have a `setValue()` method.</summary>

      You don’t _want_ to change the `VariableNode`. That is part of the program. It never changes once the AST is created. Remember, the AST _is the program_, just in another form beside text.

      You don’t want to change the program itself. You want to change the _current value_ of the variable. What object holds the current values of the variables? (When you had to _get_ the current value of a variable before, where did you get it from?)
    </details>

  - `BlockNode`:
    <details>
      <summary>Wait…is it really that simple?</summary>

      If your test passes, then yes, it really is. This should take only 2 or 3 short lines of code.
    </details>
    <details>
      <summary>Simple?! It’s not simple! What am I even supposed to do??</summary>

      Remember, “simple” does not mean “easy!” 

      You are supposed to run each of the child statements, in order.

      It may not be easy to see, but the code will be simple when you are done.
    </details>
    <details>
      <summary>I see what to do, but can you remind me of the for loop syntax in Java?</summary>

      ```
      for (var item : listOfItems) {
        ...
      }
      ```
    </details>

  - `ConditionalNode`:
    <details>
      <summary>This one is daunting. Can you sketch it out?</summary>

      Puzzle it over a bit first. You already have experience with all the building blocks you need from the items above.

      - <details>
        <summary>When you’ve puzzled a bit, and you are ready for the sketch:</summary>

        - Evaluate the left and right hand expressions. (This will look a lot like `BinaryExpressionNode`.)
        - Compare them according to the comparison operator. (This will also look a lot like `BinaryExpressionNode`.)
        - Based on the result of the comparison, run exactly one of either the true branch or the false branch.
      </details>
    </details>
    <details>
      <summary>What if there’s no `else` clause? Does that require special handling?</summary>

      Take a look at the AST for `If 1 < 2 then set x to 1.` in the playground. There is no else clause in that statement. What does that `ConditionalNode` look like? _Does_ it require special handling?
    </details>

  - `LoopExitNode`:
    <details>
      <summary>What is this even supposed to do? I don't get it.</summary>

      Read the Javadoc for the `LoopExitNode` class. It tells you what to do, pretty much in so many words.
    </details>
    <details>
      <summary>I read that, but I don’t know what it _means._ What is an exception?</summary>

      In Java, exceptions are how code reports errors. When an error happens, code “throws” an exception, and it immediately exits the block you’re inside, maybe the whole method, maybe the method that called it and the method that called that method and…on up the chain until it encounters a “try / catch” block that matches the exception.

      The Wordy interpreter uses (arguably misuses) this mechanism to exit whatever loop we’re inside: `LoopExitNode` throws a `LoopExited` exception, and `LoopNode` catches it. That gives us a way of teleporting outside whatever loop we’re inside, no matter how deep the recursion has gone.
    </details>
    <details>
      <summary>What is the Java syntax for throwing an exception?</summary>

      `throw new SomeExceptionType()`, where `SomeExceptionType` is an exception class.
    </details>

  - `LoopNode`:
    <details>
      <summary>I don’t know where to start. Can you sketch this out for me?</summary>

      Remember, the Wordy interpreter uses exceptions to exit loops. So the structure is something like this:
      ```
      infinite loop:
        run the loop body
      but when there’s a LoopExited exception:
        we’re done
      ```
    </details>
    <details>
      <summary>What is the Java syntax for catching exceptions?</summary>

      ```java
      try {
        // lots of stuff
      } catch (SomeExceptionType e) {
        // handle the exception
      }
      ```
    </details>
    <details>
      <summary>But wait, how do I handle a `LoopExited` exception? What do I need to do when I catch it?</summary>

      Nothing!

      Just make sure you catch it _outside_ the loop.
    </details>

- Still stuck? **Reach out for help on Slack!**
- When all the tests pass, change `ExpressionNode.doEvalue()` and `StatementNode.doRun()` to be abstract methods.
  <details>
    <summary>Um…what’s the Java syntax for abstract methods again?</summary>

    For example:

    ```java
    protected abstract double doEvaluate(EvaluationContext context);
    ```

    (Note the semicolon at the end, instead of braces.)
  </details>

  Making these methods abstract means that subclasses _have_ to implement them. This prompts the authors of any new future types of AST nodes to think about how to support interpretation.

Don’t forget to **commit and push your work**.


## Enjoy the fruits of your labor

- Run `Playground` again. Switch to the interpreter tab, and see the results of your hard work in action!
  - Once again, it is a good idea to start with a very simple program such as `Set x to 1.`, understand each line of the interpreter output, then try more complex examples.
  - Experiment!
- Now run the `ShaderUI`:
  - In VS Code and IntelliJ, it will be another option in the same drop-down menu where you previously selected Playground.
  - From the command line, you can run it with `gradle shader`.
- You should get a window filled with colorful ripples.
- Where did the ripples come from? ShaderUI reads a Wordy program that calculates the color of an individual pixel. You can find it in `res/ripples.wordy`. ShaderUI runs that Wordy program over and over, once for each pixel in the image. (Read the Javadoc for the `Shader` interface if you are curious to learn more.)
- There is another Wordy program in that folder that computes the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set). To run it:
    - Edit the `ShaderUI` class (in `src/wordy/demo/shader`).
    - Look for the `main` method, line 36 or thereabouts.
    - Comment out the line that specifies `ripples.wordy`, and uncomment the line that specifies `mandel.wordy`.
    - Run it again! Fractals! Your interpreter is doing the computations that create the fractal. Yay you!
    - Click to zoom in on the fractal! But…oooo, that's slow.
    - Quit and relaunch ShaderUI, and note the rendering time it logs in the console for the initial view of the Mandelbrot set (`Done rendering (___ms)`). Write that number down.

Do you see how incredibly cool this is?
<details>
  <summary>I guess? Not really. How cool is it?</summary>

  When you started this phase of the assigment, Wordy code was _just a bunch of data_: some text, a tree.

  You made it a program. A program that _actually runs_. Your computer is now running code in a new programming language it could not run before because _you made it do that_.

  That is incredibly cool.
</details>

Ready for further adventure? Proceed to [Part 2](2-compiler.md).
