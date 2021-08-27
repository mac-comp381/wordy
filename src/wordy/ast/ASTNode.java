package wordy.ast;

import java.io.PrintWriter;
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
        return dump(new StringBuffer(), null, "", true).toString();
    }

    private StringBuffer dump(StringBuffer out, String label, String indent, boolean lastChild) {
        out.append(indent);

        if(label != null) {  // null label means root node
            // Draw tree lines
            out.append(lastChild ? "└─" : "├─");
            indent += lastChild ? "  " : "│ ";

            out.append(label);
            out.append(": ");

            // Indent children past label
            indent += " ".repeat(label.length() + 2);
        }

        out.append(getClass().getSimpleName());
        out.append(' ');
        out.append(describeAttributes());
        out.append('\n');

        for(var childIter = getChildren().entrySet().iterator(); childIter.hasNext(); ) {
            var entry = childIter.next();
            entry.getValue().dump(out, entry.getKey(), indent, !childIter.hasNext());
        }
        return out;
    }

    protected String describeAttributes() {
        return "";
    }
}
