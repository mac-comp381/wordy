package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

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
    protected double doEvaluate(EvaluationContext context) {
        if (operator == Operator.ADDITION) {
            return this.lhs.doEvaluate(context) + this.rhs.doEvaluate(context);
        } else if (operator == Operator.SUBTRACTION) {
            return this.lhs.doEvaluate(context) - this.rhs.doEvaluate(context);
        } else if (operator == Operator.MULTIPLICATION) {
            return this.lhs.doEvaluate(context) * this.rhs.doEvaluate(context);
        } else if (operator == Operator.DIVISION) {
            return this.lhs.doEvaluate(context) / this.rhs.doEvaluate(context);
        } else {
            return Math.pow(this.lhs.doEvaluate(context), this.rhs.doEvaluate(context));
        }
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
            if (operator == Operator.ADDITION) {
                lhs.compile(out);
                out.print(" + ");
                rhs.compile(out);
            } else if (operator == Operator.SUBTRACTION) {
                lhs.compile(out);
                out.print(" - ");
                rhs.compile(out);
            } else if (operator == Operator.MULTIPLICATION) {
                lhs.compile(out);
                out.print(" * ");
                rhs.compile(out);
            } else {
                lhs.compile(out);
                out.print(" / ");
                rhs.compile(out);
            }
            out.print(")");
        }
    }
}
