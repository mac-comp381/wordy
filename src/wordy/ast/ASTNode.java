package wordy.ast;

import org.parboiled.common.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ASTNode {
    public abstract Map<String, ASTNode> getChildren();

    public void compile(PrintWriter out) {
        throw new UnsupportedOperationException("Compilation not implemented yet for " + getClass().getSimpleName());
    }

    public Set<VariableNode> findAllVariables() {
        Set<VariableNode> results = new HashSet<>();
        forEachVariable(results::add);
        return results;
    }

    private void forEachVariable(Consumer<VariableNode> consumer) {
        if(this instanceof VariableNode)
            consumer.accept((VariableNode) this);
        for(var child: getChildren().values()) {
            child.forEachVariable(consumer);
        }
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public String dump() {
        return dump(new StringBuffer(), "", 0).toString();
    }

    private StringBuffer dump(StringBuffer out, String label, int indent) {
        out.append(" ".repeat(indent));
        if(!StringUtils.isEmpty(label)) {
            out.append(label);
            out.append(": ");
        }
        out.append(getClass().getSimpleName().replaceFirst("Node$", ""));
        out.append(describeAttributes());
        out.append('\n');
        for(var entry : getChildren().entrySet()) {
            entry.getValue().dump(out, entry.getKey(), indent + 2);
        }
        return out;
    }

    protected String describeAttributes() {
        return "";
    }
}
