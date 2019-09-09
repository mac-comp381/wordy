package wordy.ast;

import org.parboiled.common.StringUtils;

import java.util.Map;

public abstract class ASTNode {
    public abstract Map<String, ASTNode> getChildren();

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
