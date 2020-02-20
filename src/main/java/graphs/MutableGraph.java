package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * An extension of {@linkplain Graph} which allows mutation to the graph.
 * Mainly to basic operations - add vertex and add edge.
 *
 * @param <N> the type parameter
 */
public interface MutableGraph<N> extends Graph<N> {

    /**
     * Adds a vertex/node to the graph. Returns {@code true} if addition was successful.
     * Different implementation may have a different strategy for handling addition of duplicates.
     *
     * @param node a node to add
     * @return {@code true} if addition was successful
     */
    boolean addVertex(@NonNull N node);

    /**
     * Adds an edge to the graph between {@code nodeU} and {@code nodeV}.
     * Different implementation may behave differently but it is implied that if either node does not exists -
     * one will be created as if called by {@linkplain #addVertex(N)}
     *
     * @param nodeU the node u
     * @param nodeV the node v
     * @return {@code true} if edge was successfully added
     */
    boolean addEdge(@NonNull N nodeU, @NonNull N nodeV);

    /**
     * Connects a node {@code nodeU} with multiple nodes {@code withNodesV}
     * and returns a boolean array that represents each successful result associated
     * with {@code withNodesV}
     * <p>
     * Calls {@link #addEdge(N, N)}
     *
     * @param nodeU      node u
     * @param withNodesV nodes v
     * @return booleans for each {@link #addEdge(N, N)} associated with nodes v
     */
    default boolean[] connect(@NonNull N nodeU, @NonNull N[] withNodesV) {
        if (null == withNodesV || withNodesV.length == 0) {
            throw new IllegalArgumentException("[withNodesV] must not be empty");
        }

        var connections = new boolean[withNodesV.length];
        for (int i = 0; i < withNodesV.length; i++) {
            connections[i] = addEdge(nodeU, withNodesV[i]);
        }

        return connections;
    }

}
