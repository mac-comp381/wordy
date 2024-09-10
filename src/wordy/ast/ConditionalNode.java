package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import wordy.interpreter.EvaluationContext;

import static wordy.ast.Utils.orderedMap;

/**
 * A conditional (“If … then”) in a Wordy abstract syntax tree.
 * 
 * Wordy only supports direct comparisons between two numeric expressions, e.g.
 * "If x is less than y then….” Wordy does not support boolean operators, or arbitrary boolean
 * expressions. The general structure of a Wordy conditional is:
 * 
 *     If <lhs> <operator> <rhs> then <ifTrue> else <ifFalse>
 */
public class ConditionalNode extends StatementNode {
    public enum Operator {
        EQUALS, LESS_THAN, GREATER_THAN
    }

    private final Operator operator;
    private final ExpressionNode lhs, rhs;
    private final StatementNode ifTrue, ifFalse;

    public ConditionalNode(Operator operator, ExpressionNode lhs, ExpressionNode rhs, StatementNode ifTrue, StatementNode ifFalse) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return orderedMap(
            "lhs", lhs,
            "rhs", rhs,
            "ifTrue", ifTrue,
            "ifFalse", ifFalse);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ConditionalNode that = (ConditionalNode) o;
        return this.operator == that.operator
            && this.lhs.equals(that.lhs)
            && this.rhs.equals(that.rhs)
            && this.ifTrue.equals(that.ifTrue)
            && this.ifFalse.equals(that.ifFalse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, lhs, rhs, ifTrue, ifFalse);
    }

    @Override
    public String toString() {
        return "ConditionalNode{"
            + "operator=" + operator
            + ", lhs=" + lhs
            + ", rhs=" + rhs
            + ", trueBlock=" + ifTrue
            + ", falseBlock=" + ifFalse
            + '}';
    }

    @Override
    protected String describeAttributes() {
        return "(operator=" + operator + ')';
    }

    @Override
    public void doRun(EvaluationContext context) {
        double left = this.lhs.doEvaluate(context);
        double right = this.rhs.doEvaluate(context);
        if (operator == Operator.EQUALS) {
            if (left == right) ifTrue.doRun(context);
            else ifFalse.doRun(context);
        }
        else if (operator == Operator.LESS_THAN) {
            if (left < right) ifTrue.doRun(context);
            else ifFalse.doRun(context);
        }
        else if (operator == Operator.GREATER_THAN) {
            if (left > right) ifTrue.doRun(context);
            else ifFalse.doRun(context);
        }
        else {
            throw new Error("Operator not supported");
        }
    }

    @Override
    public void compile(PrintWriter out) {
        out.print("if");
        out.print('(');
        lhs.compile(out);
        if (operator ==  Operator.EQUALS) out.print("=");
        if (operator ==  Operator.LESS_THAN) out.print("<");
        if (operator ==  Operator.GREATER_THAN) out.print(">");
        rhs.compile(out);
        out.print(')');
        ifTrue.compile(out);
        out.print("else ");
        ifFalse.compile(out);
    }
}
