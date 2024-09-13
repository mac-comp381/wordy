package wordy.ast;

import java.util.Map;
import java.util.Objects;
import java.io.PrintWriter;

import wordy.interpreter.EvaluationContext;

import static wordy.ast.Utils.orderedMap;

/**
 * Two expressions joined by an operator (e.g. “x plus y”) in a Wordy abstract syntax tree.
 */
public class BinaryExpressionNode extends ExpressionNode {
    public enum Operator {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        EXPONENTIATION
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
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
    public double doEvaluate(EvaluationContext context) {
        double left = lhs.doEvaluate(context);
        double right = rhs.doEvaluate(context);
        if (operator == Operator.ADDITION) {
            return left + right;
        } else if (operator == Operator.MULTIPLICATION) {
            return left * right;
        } else if (operator == Operator.SUBTRACTION) {
            return left - right;
        } else if (operator == Operator.DIVISION) {
            return left / right;
        } else if (operator == Operator.EXPONENTIATION) {
            return Math.pow(left, right);
        }
        throw new Error("Operator not valid");
    }

    @Override
    public void compile(PrintWriter out) {
        if (operator == Operator.EXPONENTIATION) {
            out.print("Math.pow(");
            lhs.compile(out);
            out.print(',');
            rhs.compile(out);
            out.print(')');
        } else {
            out.print('(');
            lhs.compile(out);
            if (operator == Operator.ADDITION)
                out.print('+');
            if (operator == Operator.MULTIPLICATION)
                out.print('*');
            if (operator == Operator.SUBTRACTION)
                out.print('-');
            if (operator == Operator.DIVISION)
                out.print('/');
            rhs.compile(out);
            out.print(')');
        }
    }
}
