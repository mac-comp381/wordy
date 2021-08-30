# Bonus challenges

Wordy is a great starting point for all kinds of exciting explorations into programming language implementation. I think parts 0-2 are _plenty_ of work for one homework assignment, but if you are curious about this topic and want to learn more, here are some suggestions for further exploration.

To be clear: these are not for extra credit; I will not grade them if you do them. However, I will happily help you with them! And who knows? They might turn into a final project for this class, or an independent study, or an honors project, or a career, or just a lot of fun with code. Follow your curiosity. Curiosity is magic.

If you try any of these challenges, two requests:

1. Please **make sure your code still works for the original assignment,** and all the tests for parts 1 and 2 still pass without any modifications to the tests. I strongly suggest **creating a git branch for your bonus challenges** (ask for help if you haven’t done that before). That will give you freedom to experiment wildly, with confidence that you’re not messing up your HW submission.
2. Tell me about what you’ve tried! I probably won’t notice your extra branch on my own, but I am eager to hear about your explorations.

Here are some suggestions for fun things. I encourage you to invent your own too!

## Relatively easy

- Change the words Wody uses to a different language, or something silly. For example:
    - `Si x est moins que 1, sortie de boucle.`
    - `❓x 🤌 1 ➡️ 🌱🔁 💙` (“If x is smaller than 1 then leave loop.”)
    - `Yarrr, if x be smallarrr than 1, aye, exit tha loop, ya scurrrvy dog.`
- Add a new type of operator, e.g. `x modulo y` or (slightly more challenging) `negative x`.

## Somewhat harder

- Add support for comments.
- Add while loops.
- Add an optimizer that transforms an AST to a simpler AST while preserving semantics.

  You can do this by writing many separate transformers that each detect just _one_ very simple optimization. For example:

    - `x plus 0` → `x`
    - `x minus x` → `0` (It's OK to ignore floating point [NaN](https://en.wikipedia.org/wiki/NaN) and its weirdness for this exercise.)
    - `Set x to x` → `BlockNode[]` (other optimizations will then remove the empty statement, if it’s possible to remove it)
    - Detect conditionals that do not use variables, and are thus always true or always false: `If 1 < 2 then <A> else <B>` → `<B>`
    - Remove empty statements from BlockNode parents: `BlockNode[<A>, BlockNode[], <B>]` → `BlockNode[<A>, <B>]`
    - Un-nest single-child blocks: `BlockNode[BlockNode[<A>]]` → `BlockNode[<A>]`
    - What others can you think of?

  Then repeateadly scan the whole AST for optimizations, over and over, until no more optimizations match. For example, the transformations above would do this:

    - `If x minus (x plus 0) < y minus y then set x to x plus 0 else exit loop. Set y to x plus 1.`
    - `If x minus x < y minus y then set x to x plus 0 else exit loop. Set y to x plus 1.`
    - `If 0 < y minus y then set x to x plus 0 else exit loop. Set y to x plus 1.`
    - `If 0 < 0 then set x to x plus 0 else exit loop. Set y to x plus 1.`
    - `Set x to x plus 0. Set y to x plus 1.`
    - `Set x to x. Set y to x plus 1.`
    - `Set y to x plus 1.`

  How would you write tests for this?

## Surprisingly difficult

- Add support for full boolean expressions.
- Add support for types other than double-precision floating point.
- Add support for function calls.
- Add support for lists.
- Write an interactive source debugger for the Wordy interpreter that lets you step through the code one AST node at a time.

## Exactly as hard as it sounds

- Compile Wordy to Java bytecode instead of Java source code.
- Compile Wordy to raw assembly using LLVM.

Foonote: Macalester used to have a Compilers class where we studied some of this stuff in more depth, but unfortunately we don’t have enough instructors to teach it regularly! Alas. It was a great class. Still, you can get a taste of some of what we did — and more! — with these challenges.
