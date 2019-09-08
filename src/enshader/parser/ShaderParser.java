package enshader.parser;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Parses programs or program framgments in the Enshader language.
 * <p>
 * Large portions adapted from: https://github.com/sirthias/parboiled/tree/master/examples-java/src/main/java/org/parboiled/examples/calculators/
 */
@BuildParseTree
@SuppressWarnings("WeakerAccess")
public class ShaderParser extends BaseParser<ASTNode> {
    public static StatementNode parseStatement(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Statement(), EOI), StatementNode.class);
    }

    public static ExpressionNode parseExpression(String input) {
        return parse(input, INSTANCE.Sequence(INSTANCE.Expression(), EOI), ExpressionNode.class);
    }

    private static final ShaderParser INSTANCE = Parboiled.createParser(ShaderParser.class);

    private static <T extends ASTNode> T parse(String input, Rule rule, Class<T> expectedOutput) {
        input = input.replaceAll("\\s+", " ").trim();
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

    protected Rule Program() {
        return Sequence(ZeroOrMore(Statement()), EOI);
    }

    protected Rule Statement() {
        return Sequence(Expression(), ".");
    }

    protected Rule Expression() {
        var op = new Var<BinaryExpressionNode.Operator>();
        return Sequence(
            Term(),
            ZeroOrMore(
                FirstOf(
                    Sequence("plus ",  op.set(BinaryExpressionNode.Operator.ADDITION)),
                    Sequence("minus ", op.set(BinaryExpressionNode.Operator.SUBTRACTION))
                ),
                Term(),

                push(new BinaryExpressionNode(op.get(), pop(1), pop()))
            )
        );
    }

    protected Rule Term() {
        var op = new Var<BinaryExpressionNode.Operator>();
        return Sequence(
            Atom(),
            ZeroOrMore(
                FirstOf(
                    Sequence("times ",      op.set(BinaryExpressionNode.Operator.MULTIPLICATION)),
                    Sequence("divided by ", op.set(BinaryExpressionNode.Operator.DIVISION))
                ),
                Atom(),

                push(new BinaryExpressionNode(op.get(), pop(1), pop()))
            )
        );
    }

    protected Rule Atom() {
        return FirstOf(Number(), Variable(), Parens());
    }

    protected Rule Parens() {
        return Sequence("(", Expression(), ") ");
    }

    protected Rule Number() {
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
            WhiteSpace()
        );
    }

    protected Rule Variable() {
        return Sequence(
            OneOrMore(FirstOf(
                CharRange('a', 'z'),
                CharRange('A', 'Z'),
                "_")),
            push(new VariableNode(matchOrDefault("0"))),
            WhiteSpace()
        );
    }

    protected Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f\r\n"));
    }

    protected Rule Digit() {
        return CharRange('0', '9');
    }
}
