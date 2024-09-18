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
        // I'm not very familiar with enums. There might be a better way to do this?
        if (operator.toString().equals("ADDITION")) {
            return lhs.doEvaluate(context) + rhs.doEvaluate(context);  
        } else if (operator.toString().equals("SUBTRACTION")) {
            return lhs.doEvaluate(context) - rhs.doEvaluate(context);  
        }  else if (operator.toString().equals("MULTIPLICATION")) {
            return lhs.doEvaluate(context) * rhs.doEvaluate(context);  
        }  else if (operator.toString().equals("DIVISION")) {
            return lhs.doEvaluate(context) / rhs.doEvaluate(context);  
        }  else {
            return Math.pow(lhs.doEvaluate(context), rhs.doEvaluate(context));
        } 
    }

    @Override
    public void compile(PrintWriter out) {
        if (operator.toString().equals("ADDITION")) {
            out.print("(");
            lhs.compile(out);
            out.print("+");
            rhs.compile(out); 
            out.print(")");
        } else if (operator.toString().equals("SUBTRACTION")) {
            out.print("(");
            lhs.compile(out);
            out.print("-");
            rhs.compile(out); 
            out.print(")");
        }  else if (operator.toString().equals("MULTIPLICATION")) {
            out.print("(");
            lhs.compile(out);
            out.print("*");
            rhs.compile(out); 
            out.print(")"); 
        }  else if (operator.toString().equals("DIVISION")) {
            out.print("(");
            lhs.compile(out);
            out.print("/");
            rhs.compile(out); 
            out.print(")");
        }  else {
            out.print("Math.pow(");
            lhs.compile(out);
            out.print(",");
            rhs.compile(out); 
            out.print(")");
        } 
    }
}
