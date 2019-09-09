package wordy.ast;

public final class LoopExitNode extends StatementNode {
    public LoopExitNode() {
    }

    @Override
    public boolean equals(Object o) {
        return this == o
            || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "LoopExitNode";
    }
}
