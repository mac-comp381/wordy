package wordy.ast;

import java.util.Map;
import java.util.Objects;

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
        if (operator.name().equals("ADDITION")) {
            return lhs.evaluate(context) + rhs.evaluate(context);
        } else if(operator.name().equals("SUBTRACTION")) {
            return lhs.evaluate(context) - rhs.evaluate(context);
        } else if(operator.name().equals("MULTIPLICATION")) {
            return lhs.evaluate(context) * rhs.evaluate(context);
        } else if(operator.name().equals("DIVISION")) {
            return lhs.evaluate(context) / rhs.evaluate(context);
        } else if(operator.name().equals("EXPONENTIATION")) {
            double result = lhs.evaluate(context);
            for (int i = 1; i < rhs.evaluate(context); i++) {
                result = result * lhs.evaluate(context);
            }
            return result;
        }
        return 0;
    }
}
