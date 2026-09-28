package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import wordy.interpreter.EvaluationContext;

import static wordy.ast.Utils.orderedMap;

/**
 * Two expressions joined by an operator (e.g. “x plus y”) in a Wordy abstract syntax tree.
 */
public class BinaryExpressionNode extends ExpressionNode {
    public enum Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EXPONENTIATION
    }

    private final Operator operator;
    private final ExpressionNode lhs, rhs;

    // Cheat and use a map to translate wordy operators to Java operators 
    // Pretty sure this is cheating, anyway
    private static final Map<Operator, BiFunction<Double, Double, Double>> OPERATOR_FUNCTIONS = Map.of(
        Operator.ADDITION, (a, b) -> a + b,
        Operator.SUBTRACTION, (a, b) -> a - b,
        Operator.MULTIPLICATION, (a, b) -> a * b,
        Operator.DIVISION, (a, b) -> a / b,
        Operator.EXPONENTIATION, (a, b) -> Math.pow(a, b)
    );

    private String getOperatorSymbol() {
        // Need a symbol for the operator
        switch (operator) {
            case ADDITION:
                return "+";
            case SUBTRACTION:
                return "-";
            case MULTIPLICATION:
                return "*";
            case DIVISION:
                return "/";
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }

    public BinaryExpressionNode(Operator operator, ExpressionNode lhs, ExpressionNode rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return orderedMap(
            "lhs", lhs,
            "rhs", rhs);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        BinaryExpressionNode that = (BinaryExpressionNode) o;
        return this.operator == that.operator
            && this.lhs.equals(that.lhs)
            && this.rhs.equals(that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, lhs, rhs);
    }

    @Override
    public String toString() {
        return "BinaryExpressionNode{"
            + "operator=" + operator
            + ", lhs=" + lhs
            + ", rhs=" + rhs
            + '}';
    }

    @Override
    protected String describeAttributes() {
        return "(operator=" + operator + ')';
    }

    @Override
    protected double doEvaluate(EvaluationContext context) {
        // Wait, what are our operands? Evaluate them.
        double leftValue = lhs.evaluate(context);
        double rightValue = rhs.evaluate(context);

        // Now apply the operator 
        return OPERATOR_FUNCTIONS.get(operator).apply(leftValue, rightValue);
    }

    @Override
    public void compile(PrintWriter out) {
    // exponentiation is special
    if (operator == Operator.EXPONENTIATION) {
        out.print("Math.pow(");
        lhs.compile(out);  // Compile the left-hand side
        out.print(", ");
        rhs.compile(out);  // Compile the right-hand side
        out.print(")");
    } else {
        // everything else just print parentheses and the operator symbol
        out.print("(");  // Opening parenthesis for the expression
        lhs.compile(out);  // Compile the left-hand side
        out.print(" " + getOperatorSymbol() + " ");  // Insert the operator symbol
        rhs.compile(out);  // Compile the right-hand side
        out.print(")");  // Closing parenthesis for the expression
    }
}

}
