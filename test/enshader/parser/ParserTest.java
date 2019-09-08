package enshader.parser;

import org.junit.jupiter.api.Test;
import org.parboiled.Parboiled;

import static enshader.parser.ShaderParser.parseExpression;
import static enshader.parser.ShaderParser.parseStatement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserTest {

    @Test
    void testNumbers() {
        assertEquals(new ConstantNode(37),    parseExpression("37"));
        assertEquals(new ConstantNode(37.2),  parseExpression("37.2"));
        assertEquals(new ConstantNode(-37.2), parseExpression("-37.2"));
        assertParseError(() -> parseExpression("-37..2"));
    }

    @Test
    void testOperators() {
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.ADDITION,
                new ConstantNode(1),
                new ConstantNode(2)),
            parseExpression("1 plus 2"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.SUBTRACTION,
                new ConstantNode(1),
                new ConstantNode(2)),
            parseExpression("1 minus 2"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.MULTIPLICATION,
                new ConstantNode(1),
                new ConstantNode(2)),
            parseExpression("1 times 2"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.DIVISION,
                new ConstantNode(1),
                new ConstantNode(2)),
            parseExpression("1 divided by 2"));
    }

    @Test
    void testPrecedence() {
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.ADDITION,
                new BinaryExpressionNode(
                    BinaryExpressionNode.Operator.SUBTRACTION,
                    new ConstantNode(1),
                    new ConstantNode(2)),
                new ConstantNode(3)),
            parseExpression("1 minus 2 plus 3"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.SUBTRACTION,
                new ConstantNode(1),
                new BinaryExpressionNode(
                    BinaryExpressionNode.Operator.DIVISION,
                    new ConstantNode(2),
                    new ConstantNode(3))),
            parseExpression("1 minus 2 divided by 3"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.DIVISION,
                new BinaryExpressionNode(
                    BinaryExpressionNode.Operator.SUBTRACTION,
                    new ConstantNode(1),
                    new ConstantNode(2)),
                new ConstantNode(3)),
            parseExpression("(1 minus 2) divided by 3"));
    }

    @Test
    void testVariables() {
        assertEquals(new VariableNode("x"), parseExpression("x"));
        assertEquals(new VariableNode("snake_and_camelcase_ok"), parseExpression("snake_and_camelCase_OK"));
        assertEquals(
            new BinaryExpressionNode(
                BinaryExpressionNode.Operator.ADDITION,
                new VariableNode("x"),
                new VariableNode("y")),
            parseExpression("x plus y"));
    }

    @Test
    void testAssignmentStatement() {
        assertEquals(
            new AssignmentStatement(
                new VariableNode("sprongle"),
                new ConstantNode(-3)),
            parseStatement("set sprongle to -3."));
        assertEquals(
            new AssignmentStatement(
                new VariableNode("sprongle"),
                new BinaryExpressionNode(
                    BinaryExpressionNode.Operator.ADDITION,
                    new VariableNode("zoink"),
                    new ConstantNode(7))),
            parseStatement("set sprongle to zoink plus 7."));
        assertParseError(() -> parseExpression("set sprongle to -3"));  // missing period
    }

    private void assertParseError(Runnable parseAction) {
        try {
            parseAction.run();
            fail("Expected test to raise exception");
        } catch(ParseException e) {
            // success!
        }
    }
}
