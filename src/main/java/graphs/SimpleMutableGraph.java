package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A simple implementation of mutable graph.
 * The structure of a graph is stored within a map of nodes referencing {@link GraphConnection<N>} for each vertex.
 * <p>
 * Maintains a different sets of structures - one for each node and its' connections, other a flat struct for edges
 * and the other is unmodifiable view of edges for a safe traversal and publishing.
 * <p>
 * This graph does not support weighted/valued edges.
 *
 * <p>
 * Calculating the path between nodes is done via BFS and back-tracing.
 *
 * <p>
 * This implementation is NOT thread-safe.
 *
 * @author antonovia
 * @since 2/14/2020
 */
public class SimpleMutableGraph<N> implements MutableGraph<N> {

    private final boolean directed;

    // A view of nodes with their connection information
    @NonNull
    private final Map<N, GraphConnection<N>> nodes = new HashMap<>();
    @NonNull
    private final Set<GraphEdge<N>> edges = new HashSet<>();

    @NonNull
    private final Set<GraphEdge<N>> edgesView = Collections.unmodifiableSet(edges);


    public SimpleMutableGraph(boolean directed) {
        this.directed = directed;
    }

    @Override
    public boolean addVertex(@NonNull N node) {
        requireNonNull(node, "[node]");

        if (nodes.containsKey(node)) {
            return false;
        }

        return nodes.put(node, GraphConnections.of(node)) == null;
    }

    @Override
    public boolean addEdge(@NonNull N nodeU, @NonNull N nodeV) {
        requireNonNull(nodeU, "[nodeU]");
        requireNonNull(nodeU, "[nodeV]");

        if (addVertexIfAbsent(nodeU) || addVertexIfAbsent(nodeV)) {
            return false;
        }

        var edge = edgeFrom(nodeU, nodeV);
        if (edges.contains(edge)) {
            return false;
        }

        var edgeAdded = edges.add(edge);
        // Add connections
        if (directed) {
            var ordered = (OrderedEdge<N>) edge;
            return edgeAdded && nodes.get(ordered.getSource()).newConnection(edge);
        } else {
            return edgeAdded
                    && nodes.get(nodeU).newConnection(edge)
                    && nodes.get(nodeV).newConnection(edge);
        }
    }

    @Override
    public @NonNull List<GraphEdge<N>> getPath(@NonNull N source, @NonNull N target) {
        // Graph must contain nodes first
        if (!nodes.containsKey(source) || !nodes.containsKey(target)) {
            return Collections.emptyList();
        }

        // If path is v -> u is of distance to single edge
        var straight = dumbStraightContains(source, target);
        return straight
                .map(Collections::singletonList)
                .orElseGet(() -> findPath(source, target));

    }

    @Override
    public @NonNull Set<N> getNodes() {
        return nodes.keySet();
    }

    @Override
    public @NonNull Set<GraphEdge<N>> getEdges() {
        return edgesView;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @NonNull
    @Override
    public String toString() {
        return "Graph(" +
                (isDirected() ? "directed" : "undirected") +
                "; " + edgesView + ')';
    }

    @NonNull
    private GraphEdge<N> edgeFrom(@NonNull N nodeU, @NonNull N nodeV) {
        return isDirected() ? Edge.ordered(nodeU, nodeV) : Edge.unordered(nodeU, nodeV);
    }

    // Returns false if no error. Returns true on failure
    private boolean addVertexIfAbsent(@NonNull N node) {
        if (!nodes.containsKey(node)) {
            return !addVertex(node);
        }

        return false;
    }

    @NonNull
    private Optional<GraphEdge<N>> dumbStraightContains(@NonNull N source, @NonNull N target) {
        var edge = edgeFrom(source, target);
        return edges.contains(edge) ? Optional.of(edge) : Optional.empty();
    }

    // Classic BFS
    @NonNull
    private List<GraphEdge<N>> findPath(@NonNull N source, @NonNull N target) {
        var adjacentEdges = nodes.get(source);
        if (adjacentEdges.isDisjoint()) {
            // Node is disconnected
            return Collections.emptyList();
        }

        var visitedNodes = new LinkedHashMap<N, N>();
        var queuedNodes  = new ArrayDeque<N>();
        queuedNodes.add(source);

        // Do a breadth-first search starting at the source node.
        while (!queuedNodes.isEmpty()) {
            var currentNode = queuedNodes.remove();
            var connections = nodes.get(currentNode);

            // Finish the search if we found target vertex
            if (Objects.equals(currentNode, target)) {
                return backtrace(source, target, visitedNodes);
            }

            for (N neighbour : connections.getAdjacentNodes()) {
                if (!visitedNodes.containsKey(neighbour)) {
                    queuedNodes.add(neighbour);
                    visitedNodes.put(neighbour, currentNode);
                }
            }
        }

        return backtrace(source, target, visitedNodes);
    }


    // Back-traces connections back to source from found target
    private List<GraphEdge<N>> backtrace(@NonNull N source, @NonNull N target, @NonNull LinkedHashMap<N, N> connections) {
        if (connections.isEmpty() || !connections.containsKey(target)) {
            return Collections.emptyList();
        }

        var path = new LinkedList<GraphEdge<N>>();
        var node = target;
        while (!Objects.equals(source, connections.get(node))) {
            var previous = connections.remove(node);
            nodes.get(previous).getEdgeTo(node).ifPresent(path::addFirst);

            node = previous;
        }

        path.addFirst(nodes.get(source).getEdgeTo(node).orElseThrow());
        return path;
    }

}
