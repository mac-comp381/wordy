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
    protected double doEvaluate(EvaluationContext context) {
        double lNum = lhs.doEvaluate(context);
        double rNum = rhs.doEvaluate(context);
        if(operator.equals(Operator.ADDITION))
            return lNum + rNum;
        if(operator.equals(Operator.DIVISION))
            return lNum / rNum;
        if(operator.equals(Operator.EXPONENTIATION))
            return Math.pow(lNum, rNum);
        if(operator.equals(Operator.MULTIPLICATION))
            return lNum * rNum;
        if(operator.equals(Operator.SUBTRACTION))
            return lNum - rNum;
        // https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html
        // https://stackoverflow.com/questions/4872978/how-do-i-pass-a-class-as-a-parameter-in-java
            //Specifically citing the answer from Olanrewaju O. Joseph
        throw new EnumConstantNotPresentException(Operator.class, operator.toString());
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
