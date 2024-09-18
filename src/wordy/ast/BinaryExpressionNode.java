package wordy.ast;

import static wordy.ast.Utils.orderedMap;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import wordy.interpreter.EvaluationContext;

/**
 * Two expressions joined by an operator (e.g. “x plus y”) in a Wordy abstract syntax tree.
 */
public class BinaryExpressionNode extends ExpressionNode {
    public enum Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EXPONENTIATION
    }

    private final Operator operator;
    private final ExpressionNode lhs, rhs;

    public BinaryExpressionNode(Operator operator, ExpressionNode lhs, ExpressionNode rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    protected double doEvaluate(EvaluationContext context) {
        double lhsValue = lhs.evaluate(context);
        double rhsValue = rhs.evaluate(context);
        return switch (operator) {
            case ADDITION -> lhsValue + rhsValue;
            case DIVISION -> lhsValue / rhsValue;
            case EXPONENTIATION -> Math.pow(lhsValue, rhsValue);
            case MULTIPLICATION -> lhsValue * rhsValue;
            case SUBTRACTION -> lhsValue - rhsValue;
        };
    }

    @Override
    public void compile(PrintWriter out) {
        if (operator == Operator.EXPONENTIATION) {
            out.print("Math.pow(");
            lhs.compile(out);
            out.print(", ");
            rhs.compile(out);
            out.print(")");
        } else {
            out.print("(");
            lhs.compile(out);
            out.print(switch (operator) {
                case ADDITION -> " + ";
                case DIVISION -> " / ";
                case MULTIPLICATION -> " * ";
                case SUBTRACTION -> " - ";
                default -> throw new IllegalArgumentException("Unexpected value: " + operator);
            });
            rhs.compile(out);
            out.print(")");
        }
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
}
