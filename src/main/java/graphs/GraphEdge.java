package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a connection between two nodes in a graph
 *
 * @param <N>
 */
public interface GraphEdge<N> {

    @NonNull
    N getNodeU();

    @NonNull
    N getNodeV();

    boolean isOrdered();

}
