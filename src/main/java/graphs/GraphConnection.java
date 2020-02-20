package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * Graph connection stores information on node surroundings within graph
 *
 * @author antonovia
 * @since 2/17/2020
 */
public interface GraphConnection<N> {

    /**
     * @return A set of adjacent nodes.
     *         Node is adjacent iff it can be reached directly
     */
    @NonNull
    Set<N> getAdjacentNodes();

    /**
     * Returns an edge instance from current node to a given node.
     *
     * @param node a node to find edge to
     * @return empty if current node does not have connection to a given one
     */
    @NonNull
    Optional<GraphEdge<N>> getEdgeTo(@NonNull N node);

    /**
     * Creates new connection between nodes.
     *
     * @param edge an ordered or unordered edge
     * @return {@code true} if connection was successfully added
     */
    boolean newConnection(@NonNull GraphEdge<N> edge);

    boolean isDisjoint();
}
