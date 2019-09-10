package wordy.ast;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlockNode extends StatementNode {
    public static final BlockNode EMPTY = new BlockNode();

    public final List<StatementNode> statements;

    public BlockNode(List<StatementNode> statements) {
        this.statements = statements;
    }

    public BlockNode(StatementNode... statements) {
        this.statements = Arrays.asList(statements);
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        Map<String, ASTNode> result = new LinkedHashMap<>();
        var iter = statements.iterator();
        for(int index = 0; iter.hasNext(); index++) {
            result.put(String.valueOf(index), iter.next());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        BlockNode blockNode = (BlockNode) o;
        return statements.equals(blockNode.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

    @Override
    public String toString() {
        return "BlockNode{statements=" + statements + '}';
    }
}
