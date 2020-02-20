package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;

/**
 * A generic graph interface. Provides a traversal API for a graph.
 * For a mutation actions see {@link MutableGraph}.
 * <p>
 * A graph consists of a vertices/nodes of type {@code N} and edges {@link GraphEdge}
 * <p>
 * This interface implies that graph may be both directed and undirected and implementation should abide to it
 * for consistency.
 * <p>
 * This interface does not imply a support for a weighted edges but further descendants may do so.
 *
 * @param <N> Vertex/node type
 */
public interface Graph<N> {

    /**
     * Gets a lists of edges connecting {@code source} and {@code target}.
     * If path does not exists or one of the nodes is disjoint - returns an empty list. (meaning no path exists)
     *
     * @param source the node u
     * @param target the node v
     * @return the path
     */
    @NonNull
    List<GraphEdge<N>> getPath(@NonNull N source, @NonNull N target);

    /**
     * Gets all vertices/nodes this graph has.
     *
     * @return the nodes
     */
    @NonNull
    Set<N> getNodes();

    /**
     * Gets all edges this graph has.
     *
     * @return the edges
     */
    @NonNull
    Set<GraphEdge<N>> getEdges();

    /**
     * Returns {@code true} if graph is directed.
     *
     * @return the boolean
     */
    boolean isDirected();

}
