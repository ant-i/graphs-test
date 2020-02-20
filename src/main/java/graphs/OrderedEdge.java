package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Ordered edge - is an edge of a directed graph.
 * This edge has a source node and a target node.
 *
 * @param <N>
 */
public interface OrderedEdge<N> extends GraphEdge<N> {

    @NonNull
    N getSource();

    @NonNull
    N getTarget();

}
