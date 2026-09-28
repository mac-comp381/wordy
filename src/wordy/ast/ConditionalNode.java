package wordy.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

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

        private static final Map<Operator, BiPredicate<Double, Double>> OPERATOR_FUNCTIONS = Map.of(
        Operator.EQUALS, (a, b) -> Double.compare(a, b) == 0,
        Operator.LESS_THAN, (a, b) -> a < b,
        Operator.GREATER_THAN, (a, b) -> a > b
    );

    private String getOperatorSymbol() {
        switch (operator) {
            case EQUALS:
                return "==";
            case LESS_THAN:
                return "<";
            case GREATER_THAN:
                return ">";
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
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
    protected void doRun(EvaluationContext context) {
        // Evaluate left-hand side and right-hand side expressions
        double leftValue = lhs.evaluate(context);
        double rightValue = rhs.evaluate(context);

        // Use the operator to check the condition using the BiPredicate
        boolean conditionIsTrue = OPERATOR_FUNCTIONS.get(operator).test(leftValue, rightValue);

        // Run the appropriate branch based on the condition
        if (conditionIsTrue) {
            ifTrue.run(context);
        } else {
            ifFalse.run(context);
        }
    }

    // This is the only method that I lost my mind on
    // Thank you very much for the hints :)
    
    @Override
    public void compile(PrintWriter out) {
        // Compile the condition
        out.print("if(");
        lhs.compile(out);
        out.print(" " + getOperatorSymbol() + " ");
        rhs.compile(out);
        out.print(") ");
    
        // Handle the ifTrue part
        if (ifTrue instanceof BlockNode) {
            ifTrue.compile(out); // BlockNode handles its own braces
        } else {
            ifTrue.compile(out);
        }
    
        // Check if the else part exists and is not explicitly meant to be empty
        if (ifFalse != null && ifFalse != BlockNode.EMPTY) {
            out.print(" else ");
            if (ifFalse instanceof BlockNode) {
                ifFalse.compile(out); // BlockNode handles its own braces
            } else {
                // It's a single statement or non-block structure
                ifFalse.compile(out);
            }
        } else if (ifFalse == BlockNode.EMPTY) {
            // Explicitly empty else block good GOSH this was hard
            out.print(" else {}");
        }
    }
    
}
