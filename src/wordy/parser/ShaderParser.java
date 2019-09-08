package wordy.parser;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.RecoveringParseRunner;
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
import wordy.ast.StatementNode;
import wordy.ast.VariableNode;

/**
 * Parses programs or program framgments in the Enshader language.
 * <p>
 * Large portions adapted from: https://github.com/sirthias/parboiled/tree/master/examples-java/src/main/java/org/parboiled/examples/calculators/
 */
@BuildParseTree
@SuppressWarnings("WeakerAccess")
public class ShaderParser extends BaseParser<ASTNode> {
    public static StatementNode parseProgram(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Program(), EOI), StatementNode.class);
    }

    public static StatementNode parseStatement(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Statement(), EOI), StatementNode.class);
    }

    public static ExpressionNode parseExpression(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Expression(), EOI), ExpressionNode.class);
    }

    private static final ShaderParser INSTANCE = Parboiled.createParser(ShaderParser.class);

    private static <T extends ASTNode> T parse(String input, Rule rule, Class<T> expectedOutput) {
        input = input
            .toLowerCase()
            .replaceAll("\\s+", " ").trim();
        ParsingResult<?> result = new RecoveringParseRunner(rule).run(input);
        if(result.hasErrors())
            throw new ParseException(result);

        var ast = result.parseTreeRoot.getValue();
        if(ast == null)
            throw new ParseException("Could not parse");
        if(!expectedOutput.isAssignableFrom(ast.getClass()))
            throw new ParseException("Expected parser to produce " + expectedOutput + ", but got " + ast.getClass());
        //noinspection unchecked
        return (T) ast;
    }

    // –––––––––––– Grammar rules ––––––––––––

    Rule Program() {
        return Sequence(Block(), EOI);
    }

    Rule Block() {
        Var<List<StatementNode>> list = new Var<>(new ArrayList<>());
        return Sequence(
            OneOrMore(
                Statement(),
                OptionalWhitespace(), ".", OptionalWhitespace(),
                list.get().add((StatementNode) pop())),
            push(new BlockNode(list.get())));
    }

    Rule Statement() {
        return FirstOf(
            Assignment(),
            Conditional());
    }

    Rule Conditional() {
        Var<ConditionalNode.Operator> comparisonOperator = new Var<>();
        Var<StatementNode> ifTrue = new Var<>(), ifFalse = new Var<>();
        return Sequence(
            "if ",
            Expression(),
            FirstOf(
                Sequence("equals ",          comparisonOperator.set(ConditionalNode.Operator.EQUALS)),
                Sequence("is equal to ",     comparisonOperator.set(ConditionalNode.Operator.EQUALS)),
                Sequence("is less than ",    comparisonOperator.set(ConditionalNode.Operator.LESS_THAN)),
                Sequence("is greater than ", comparisonOperator.set(ConditionalNode.Operator.GREATER_THAN))),
            Expression(),
            FirstOf(
                Sequence(
                    "then",
                    OptionalWhitespace(), ":", OptionalWhitespace(),
                    Block(),
                    FirstOf(
                        Sequence(
                            "else",
                            OptionalWhitespace(), ":", OptionalWhitespace(),
                            Block()),
                        push(BlockNode.EMPTY)),
                    "end of conditional"),
                Sequence(
                    "then ", Statement(),
                    FirstOf(
                        Sequence("else ", Statement()),
                        push(BlockNode.EMPTY)))),
            push(new ConditionalNode(
                comparisonOperator.get(),
                (ExpressionNode) pop(3),
                (ExpressionNode) pop(2),
                (StatementNode) pop(1),
                (StatementNode) pop()))
        );
    }

    Rule Assignment() {
        return Sequence(
            "set ",
            Variable(),
            "to ",
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
                    Sequence("plus ",  op.set(BinaryExpressionNode.Operator.ADDITION)),
                    Sequence("minus ", op.set(BinaryExpressionNode.Operator.SUBTRACTION))
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
                    Sequence("times ",      op.set(BinaryExpressionNode.Operator.MULTIPLICATION)),
                    Sequence("divided by ", op.set(BinaryExpressionNode.Operator.DIVISION))
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
                        "to the power of ", ExponentialExpression(),  // note the exponentiation is right-associative
                        push(new BinaryExpressionNode(BinaryExpressionNode.Operator.EXPONENTIATION,
                            (ExpressionNode) pop(1),
                            (ExpressionNode) pop()))),
                    Sequence(
                        "squared", OptionalWhitespace(),
                        push(new BinaryExpressionNode(BinaryExpressionNode.Operator.EXPONENTIATION,
                            (ExpressionNode) pop(),
                            new ConstantNode(2)))))));
    }

    Rule Atom() {
        return FirstOf(Number(), Variable(), Parens());
    }

    Rule Parens() {
        return Sequence("(", Expression(), ") ");
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
            OptionalWhitespace()
        );
    }

    Rule Variable() {
        return Sequence(
            OneOrMore(FirstOf(
                CharRange('a', 'z'),
                CharRange('A', 'Z'),
                "_")),
            push(new VariableNode(matchOrDefault("0"))),
            OptionalWhitespace()
        );
    }

    Rule OptionalWhitespace() {
        return ZeroOrMore(" ");
    }

    Rule Digit() {
        return CharRange('0', '9');
    }
}
