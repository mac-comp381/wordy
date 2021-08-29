package wordy.parser;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;

import java.util.ArrayList;
import java.util.List;

import wordy.ast.ASTNode;
import wordy.ast.AssignmentNode;
import wordy.ast.BinaryExpressionNode;
import wordy.ast.BlockNode;
import wordy.ast.ConditionalNode;
import wordy.ast.ConstantNode;
import wordy.ast.ExpressionNode;
import wordy.ast.LoopExitNode;
import wordy.ast.LoopNode;
import wordy.ast.StatementNode;
import wordy.ast.VariableNode;

/**
 * Parses programs or program fragments in the Wordy language.
 * <p>
 * This parser uses the Parboiled parser eDSL. The source code here functions as a definition of the
 * Wordy language grammar.
 * <p>
 * Large portions adapted from: https://github.com/sirthias/parboiled/tree/master/examples-java/src/main/java/org/parboiled/examples/calculators/
 */
@BuildParseTree
@SuppressWarnings("WeakerAccess")  // parboiled code generation requires greater visibility
public class WordyParser extends BaseParser<ASTNode> {
    public static StatementNode parseProgram(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Program(), EOI), StatementNode.class);
    }

    public static StatementNode parseStatement(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Statement(), EOI), StatementNode.class);
    }

    public static ExpressionNode parseExpression(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Expression(), EOI), ExpressionNode.class);
    }

    private static final WordyParser INSTANCE = Parboiled.createParser(WordyParser.class);

    private static <T extends ASTNode> T parse(String input, Rule rule, Class<T> expectedOutput) {
        input = input
            .toLowerCase()
            .replaceAll("\\s", " ");
        ParsingResult<T> result = new ReportingParseRunner<T>(rule).run(input);
        if(result.hasErrors())
            throw new ParseException(result);

        var ast = result.parseTreeRoot.getValue();
        if(ast == null)
            throw new ParseException("Could not parse");
        if(!expectedOutput.isAssignableFrom(ast.getClass()))
            throw new ParseException("Expected parser to produce " + expectedOutput + ", but got " + ast.getClass());
        return ast;
    }

    // –––––––––––– Grammar rules ––––––––––––

    Rule Program() {
        return Sequence(OptionalSpace(), Block(), EOI);
    }

    Rule Block() {
        Var<List<StatementNode>> list = new Var<>(new ArrayList<>());
        return Sequence(
            OneOrMore(
                Statement(),
                OptionalSurroundingSpace("."),
                list.get().add((StatementNode) pop())),
            push(new BlockNode(list.get())));
    }

    Rule Statement() {
        return FirstOf(
            Assignment(),
            Conditional(),
            Loop(),
            LoopExit());
    }

    Rule Conditional() {
        Var<ConditionalNode.Operator> comparisonOperator = new Var<>();
        return Sequence(
            KeyPhrase("if"), 
            Expression(),
            FirstOf(
                Sequence(KeyPhrase("equals"),          comparisonOperator.set(ConditionalNode.Operator.EQUALS)),
                Sequence(KeyPhrase("is equal to"),     comparisonOperator.set(ConditionalNode.Operator.EQUALS)),
                Sequence(KeyPhrase("is less than"),    comparisonOperator.set(ConditionalNode.Operator.LESS_THAN)),
                Sequence(KeyPhrase("is greater than"), comparisonOperator.set(ConditionalNode.Operator.GREATER_THAN))),
            Expression(),
            FirstOf(
                Sequence(
                    KeyPhrase("then"),
                    OptionalSurroundingSpace(":"),
                    Block(),
                    FirstOf(
                        Sequence(
                            KeyPhrase("else"),
                            OptionalSurroundingSpace(":"),
                            Block()),
                        push(BlockNode.EMPTY)),
                    KeyPhrase("end of conditional")),
                Sequence(
                    KeyPhrase("then"), 
                    Statement(),
                    FirstOf(
                        Sequence(KeyPhrase("else"), Statement()),
                        push(BlockNode.EMPTY)))),
            push(new ConditionalNode(
                comparisonOperator.get(),
                (ExpressionNode) pop(3),
                (ExpressionNode) pop(2),
                (StatementNode) pop(1),
                (StatementNode) pop())));
    }

    Rule Loop() {
        return Sequence(
            KeyPhrase("loop"),
            OptionalSurroundingSpace(":"),
            Block(),
            KeyPhrase("end of loop"),
            push(new LoopNode((StatementNode) pop())));
    }

    Rule LoopExit() {
        return Sequence(KeyPhrase("exit loop"), push(new LoopExitNode()));
    }

    Rule Assignment() {
        return Sequence(
            KeyPhrase("set"), 
            Variable(),
            KeyPhrase("to"), 
            Expression(),
            push(new AssignmentNode((VariableNode) pop(1), (ExpressionNode) pop()))
        );
    }

    Rule Expression() {
        return AdditiveExpression();
    }

    Rule AdditiveExpression() {
        var op = new Var<BinaryExpressionNode.Operator>();
        return Sequence(
            MultiplicativeExpression(),
            ZeroOrMore(
                FirstOf(
                    Sequence(KeyPhrase("plus"),  op.set(BinaryExpressionNode.Operator.ADDITION)),
                    Sequence(KeyPhrase("minus"), op.set(BinaryExpressionNode.Operator.SUBTRACTION))
                ),
                MultiplicativeExpression(),

                push(new BinaryExpressionNode(op.get(), (ExpressionNode) pop(1), (ExpressionNode) pop()))
            )
        );
    }

    Rule MultiplicativeExpression() {
        var op = new Var<BinaryExpressionNode.Operator>();
        return Sequence(
            ExponentialExpression(),
            ZeroOrMore(
                FirstOf(
                    Sequence(KeyPhrase("times"),      op.set(BinaryExpressionNode.Operator.MULTIPLICATION)),
                    Sequence(KeyPhrase("divided by"), op.set(BinaryExpressionNode.Operator.DIVISION))
                ),
                ExponentialExpression(),

                push(new BinaryExpressionNode(op.get(), (ExpressionNode) pop(1), (ExpressionNode) pop()))
            )
        );
    }

    @SuppressWarnings("InfiniteRecursion")  // parboiled instruments this so it doesn't actually infinitely recurse
    Rule ExponentialExpression() {
        return Sequence(
            Atom(),
            ZeroOrMore(
                FirstOf(
                    Sequence(
                        KeyPhrase("to the power of"), 
                        ExponentialExpression(),  // note the exponentiation is right-associative
                        push(new BinaryExpressionNode(BinaryExpressionNode.Operator.EXPONENTIATION,
                            (ExpressionNode) pop(1),
                            (ExpressionNode) pop()))),
                    Sequence(
                        KeyPhrase("squared"), 
                        push(new BinaryExpressionNode(BinaryExpressionNode.Operator.EXPONENTIATION,
                            (ExpressionNode) pop(),
                            new ConstantNode(2)))))));
    }

    Rule Atom() {
        return FirstOf(Number(), Variable(), Parens());
    }

    Rule Parens() {
        return Sequence(
            OptionalSurroundingSpace("("),
            Expression(),
            OptionalSurroundingSpace(")"));
    }

    Rule Number() {
        return Sequence(
            // we use another Sequence in the "Number" Sequence so we can easily access the input text matched
            // by the three enclosed rules with "match()" or "matchOrDefault()"
            Sequence(
                Optional('-'),
                OneOrMore(Digit()),
                Optional('.', OneOrMore(Digit()))
            ),

            // the matchOrDefault() call returns the matched input text of the immediately preceding rule
            // or a default string (in this case if it is run during error recovery (resynchronization))
            push(new ConstantNode(Double.parseDouble(matchOrDefault("0")))),
            OptionalSpace()
        );
    }

    Rule Variable() {
        return Sequence(
            OneOrMore(FirstOf(
                CharRange('a', 'z'),
                CharRange('A', 'Z'),
                "_")),
            push(new VariableNode(matchOrDefault("0"))),
            OptionalSpace()
        );
    }

    Rule KeyPhrase(String phrase) {
        List<Object> rules = new ArrayList<>();
        for(var word: phrase.split(" ")) {
            rules.add(word);
            rules.add(OptionalSpace());
        }
        return Sequence(rules.toArray());
    }

    Rule Space() {
        return OneOrMore(" ");  // parse() normalizes all whitespace chars to spaces
    }

    Rule OptionalSpace() {
        return ZeroOrMore(" ");
    }

    Rule OptionalSurroundingSpace(Object content) {
        return Sequence(OptionalSpace(), content, OptionalSpace());
    }

    Rule Digit() {
        return CharRange('0', '9');
    }
}
