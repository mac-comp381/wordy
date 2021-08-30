# The Wordy Programming language

Wordy is a tiny programming language suitable for exploration of basic language implementation principle. It is a Paul Cantrell invention specifically for Macalester’s Programming Languages course.

Wordy uses English words throughout its syntax. It supports simple arithmetic expressions, loops, and conditionals.

Wordy is intentionally a very simple language. It supports only a single type: 64-bit floating point numbers\*. A program operates on a set of named variables in a “context,” taking its input from any variables already present, and leaving its output as variables set at termination. Variables do not need to be declared, and all variables are zero by default.

> \* Wait, aren’t floating point numbers dangerous, [full of rounding pitfalls and wildly counterintuitive behavior](https://stackoverflow.com/questions/10371857/is-floating-point-addition-and-multiplication-associative)? Yes, they are, but don’t worry: Wordy is just a toy language. No _actual_ programming language in widespread practical use for critical applications would ever [do something as ridiculous as making floating point its default or only numeric type](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Numbers_and_dates). Perish the thought.

There are no other types other than double, including boolean. Wordy's conditionals allow a single comparison between two expressions; programs must construct “and” and “or” by using nested conditionals. The language has no traversible data structures, has no functions, and is not Turing complete.

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

This is an informal grammar of the language. To help with readability, the grammar here omits spaces. Details are in [the full parser](../src/wordy/parser/WordyParser.java).

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
