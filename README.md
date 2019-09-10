# Wordy

A toy programming language. We will do science to it.

## The activity

1. Read about the Wordy language below.
2. Set up:
    - Clone (don’t just download! clone!) this repository.
    - Open (don't import! open!) the project folder in IntelliJ.
    - In the Project view in IntelliJ, you should see the `test` directory showing up in green. Right-click it and choose “Run All Tests.” You should see one group of tests, WordyParserTest, and they should all pass.
    - If this doesn’t work, stop and get help before proceeding!
3. Play with the Wordy IDE and study the AST model:
    - In IntelliJ, expand `src/wordy/demo`, right-click the `Playground` class, and choose “Run Playgroun.main()”. After a long Java moment, you should see a user interface with code on the right and an AST on on the left.
    - Spend some time experimenting with making changes to the code and seeing how they affect the AST on the right. Get a feel for the structure of the AST.
    - Spend some time skimming the classes in `src/wordy/ast`. Understand how they implement the AST.
    - If you are curious how the AST is generated, look in `src/wordy/parser`. However, this is for your curiosity only; you **do not need to study the parser code** for this activity.
4. Implement a Wordy interpreter:
    - I will do a little demo at the projector to set up this section.
    - Get the interpreter starter code: `git merge interpreter`
    - You should now see `InterpreterTest` in `test/wordy`. All of its tests should fail. If you don’t see it (or the tests somehow pass!) then stop and get help.
    - Make the tests in `InterpreterTest` pass. I strongly recommend that you get them passing **one at a time**, in the order they appear in the file, testing each one before moving to the next.
5. Try your interpreter:
    - Look in `wordy/demo/shader` for the `ShaderUI` class. Run it! You should get a window filled with colorful gradients.
    - ShaderUI reads a Wordy program that calculates the color of each pixel on the screen. You can find it in `res/ripples.wordy`.
    - There is another program in that folder that computes the Mandelbrot set. To run it:
        - Edit `ShaderUI`.
        - Look for the `main` method, about 30 lines in.
        - Comment out the line that specifies `ripples.wordy`, and uncomment the line that specifies `mandel.wordy`.
        - Run it again! Fractals! Your interpreter is doing the computations that create the fractal. Yay you!
        - Click to zoom in on the fractal! But … oooo, that's slow. Quit and relaunch ShaderUI, and note the rendering time it logs in the console.
6. Implement a Wordy compiler:
    - Again, I will do a little demo to contextualize this work.
    - Commit your interpreter implementation if you haven't already.
    - Get the compiler starter code: `git merge interpreter` (You might get a merge conflict. If so, resolve it, then rerun All Tests to make sure everything is still working.)
    - The merge you just did switches ShaderUI over to stop using the Wordy source code in `res`, and start using Wordy code that has been compiled to Java. That code lives in `src/wordy/demo/shader/CompiledShader.java` — but that's just a placeholder for now.
    - You will now see a `CompilerTest` class in your test folder. Once again, make its tests pass, one at a time.
    - When it's working, launch `Playground` again. You will now see a tab for compiled code. Experiment with looking at your Wordy code compiled to Java.
    - Now it’s time to put your compiler to use! Copy the entire contents of `res/mandel.wordy` into the Playground to compile it.
    - Replace the contents of `src/wordy/demo/shader/CompiledShader.java` with the compiler output you copied from the Playground.
    - Rename the class `CompiledShader`.
    - Now run ShaderUI again. If everything worked, you’ll get _fast_ fractals!

## About the language

Wordy uses English words throughout its syntax. It supports simple arithmetic expressions, loops, and conditionals.

Wordy is intentially a very simple language. It supports a single type of value: 64-bit floating point numbers. A program operates on a set of named variables in a “context,” taking its input from any variables already present, and leaving its output as variables set at termination. Variables do not need to be declared, and all variables are zero by default.

There are no other types other than double, including boolean. Wordy's conditional allow a single comparison between two expressions; programs must construct “and” and “or” by using nested conditionals. The language has no traversible data structures, and is not Turing complete.

## Sample code

Compute the nth Fibonacci number, where `n` is provided as input:

```
Set a to 1.
Set b to 1.
Set count to n.
Loop:
    If count is less than 1 then exit loop.
    Set next to a plus b.
    Set a to b.
    Set b to next.
    Set count to count minus 1.
End of loop.
```

Compute a Mandelbrot orbit `count` at (`cr`, `ci`), assuming the count is infinite if it exceeds `max_iterations`:

```
Set zr to 0.
Set zi to 0.
Loop:
    If count is greater than max_iterations then:
        Set count to 1 divided by 0.
        Exit loop.
    End of conditional.
    If zr squared plus zi squared is greater than 4 then exit loop.
    Set new_zr to zr squared minus zi squared plus cr.
    Set zi to 2 times zr times zi plus ci.
    Set zr to new_zr.
    Set count to count plus 1.
End of loop.
```

## Language Grammar

This is an informal grammar of the language. To help with readability, this grammar omits spaces. Details are in [the full parser](blob/master/src/wordy/parser/WordyParser.java).

```regex
Program →
    Block EOF

Block →
    (Statement ".")+

Statement →
    Assignment | Conditional | Loop | LoopExit

Conditional →
    "if" 
    Expression
    ("equals" | "is equal to" | "is less than" | "is greater than")
    Expression
    "then"
    (
        (":" Block ("else" ":" Block)? "end of conditional")
        | (Statement ("else" Statement)?)
    )

Loop →
    "loop" ":"
    Block
    "end of loop"

LoopExit →
    "exit loop"

Assignment →
    "set" Variable "to" Expression

Expression →
    AdditiveExpression

AdditiveExpression →
    MultiplicativeExpression (("plus" | "minus") MultiplicativeExpression)*

MultiplicativeExpression →
    ExponentialExpression (("times" | "divided by") ExponentialExpression)*

ExponentialExpression →
    Atom (("to the power of" ExponentialExpression) | "squared")*

Atom →
    Number | Variable | Parens

Parens →
    "(" Expression ")"

Number →
    "-"? Digit+ ("." Digit+)?

Variable →
    ([a-z] | [A-Z] | "_")+

Digit →
    [0-9]
```
