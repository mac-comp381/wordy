# Part 0: Understand your starting point

First, read about the [Wordy programming language](wordy.md), studying the sample code and the grammar so that you get a sense of what features the language has.

Next, play with the Wordy Playground (see Project Setup instructions above for how to run it):

- You will see some Wordy code in the panel on the left, and the corresponding AST in the panel on the right.
- Try editing the code on the left. The AST will update automatically whenever the code is syntactically valid. If the code has a syntax error (i.e. does not parse), then the playground will show a red highlight.
- Try trimming the the code down to a single simple statement such as `Set x to 1.`, and study and understand each node of the AST on the right. Then build up more complex programs, and understand how the AST changes. Some good examples to try:
  - `Set x to 1 plus 2.`
  - `Set y to 3. Set x to y plus 1.`
  - `Set x to y plus 2 times z.`
  - `Set x to y times 2 plus z.`
  - `Set x to y times (2 plus z).` (Where did the parentheses go? Why don’t they appear in the AST?)
  - `If x is less than y then set z to 0 else set z to 1.` (What are the parts of a ConditionalNode? What is the role of each one of the four children?)
  - `If x is less than y then set z to 0.` (Now there is no `else` clause, so why is there still an `ifFalse` child? What does it contain? Why?)
  - `If x is less than y then: Set z to 0. End of conditional.` (The colon after `then` allows the conditional to contain multiple statements, up until `End of conditional.` However, this Wordy code means exactly the same thing as the previous item. Why does it produce a different AST? Does different AST necessarily mean different semantics?)
  - `2 squared.` (What’s particularly interesting about this one?)
- Come up with your own experiments. Explore. Get a feel for the structure of the AST.
- Now click on the **Interpreted** and **Compiled** tabs. Both of them show an error message, saying they are node implemented yet. That is what you are going to do for this assignment!

Now spend some time studying the Wordy AST source code:

- Look through the classes in the `ast` package (in `src/wordy/ast`). Study how this code implements in Java the various AST nodes you were looking at in the playground.
- Pay attention to the instance variables on each node.
- Pay attention to the class inheritance structure. Which classes are `abstract`? Those classes represent different categories of nodes. Which nodes are in each category? (You might want to diagram the type hierarchy on a sheet of paper.) Each abstract class has methods that apply only to nodes in that category. Why does each abstract class have the methods it has?

If you are curious:

- Study `WordyParserTest`. How does it work? What kinds of things is it testing for?
- Study `WordyParser` (in `src/wordy/parser`). This is the class that turns Wordy source code (i.e. text) into a Wordy AST. Does this code look strange?

  This class uses the [parboiled](https://github.com/sirthias/parboiled/wiki) library. The code you are looking at is a description of the Wordy _grammar_, but it does not contain a _parser_ (i.e. the code that applies the grammar to text input). Instead, parboiled _generates_ a parser from this class’s grammar description. This code is an example of **declarative programming** and also of an **eDSL**, both of which we will learn about later in this course.

  **Don’t worry: you do not need to understand this code to do this homework!** This is just something to look at if you are feeling curious. It gives a window into a style of Java code that is probably unlike what you have encountered before. All these languages we study are much bigger than just what we see in class.
- You can study the `Playground` code, but I don’t particularly recommend it. That code does a lot of work, and very little of that work is interesting. It mostly illustrates why people don’t use Java Swing more out in industry (and why we made [Kilt Graphics](https://github.com/mac-comp127/kilt-graphics) instead of making you learn Swing in COMP 127).

Ready to jump in? Proceed to [Part 1](1-interpreter.md).
