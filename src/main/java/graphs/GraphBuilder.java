package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

public class GraphBuilder {

    private final boolean directed;

    public GraphBuilder(boolean directed) {
        this.directed = directed;
    }

    public static GraphBuilder undirected() {
        return new GraphBuilder(false);
    }

    public static GraphBuilder directed() {
        return new GraphBuilder(true);
    }

    @NonNull
    public <N> Graph<N> build() {
        return new SimpleMutableGraph<>(directed);
    }

}
