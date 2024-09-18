package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import static wordy.ast.Utils.orderedMap;
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
        double lhsValue = lhs.evaluate(context);
        double rhsValue = rhs.evaluate(context);
        switch(operator) {
            case ADDITION:
                return lhsValue + rhsValue;
            case SUBTRACTION:
                return lhsValue - rhsValue;
            case MULTIPLICATION:
                return lhsValue * rhsValue;
            case DIVISION:
                return lhsValue / rhsValue;
            case EXPONENTIATION:
                return Math.pow(lhsValue, rhsValue);
            default:
                throw new AssertionError("Unknown operator: " + operator);
        }
    }

    @Override
    public void compile(PrintWriter out) {
        if (operator == Operator.EXPONENTIATION) {
            out.print("Math.pow(");
            lhs.compile(out);
            out.print(", ");
            if (rhs instanceof ConstantNode && ((ConstantNode) rhs).getValue() == 2.0) {
                out.print("2.0");
            } else {
                rhs.compile(out);
            }
            out.print(")");
        } else {
            out.print("(");
            lhs.compile(out);
            switch (operator) {
                case ADDITION:
                    out.print(" + ");
                    break;
                case SUBTRACTION:
                    out.print(" - ");
                    break;
                case MULTIPLICATION:
                    out.print(" * ");
                    break;
                case DIVISION:
                    out.print(" / ");
                    break;
            }
            rhs.compile(out);
            out.print(")");
        }
    }



}
