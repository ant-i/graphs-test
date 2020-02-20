package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class GraphConnections {

    @NonNull
    public static <N> GraphConnection<N> of(@NonNull N node) {
        return new SimpleGraphConnection<>(node);
    }

    private static class SimpleGraphConnection<N> implements GraphConnection<N> {

        @NonNull
        private final N node;

        @NonNull
        private final Map<N, GraphEdge<N>> adjacentNodes = new HashMap<>();

        @NonNull
        private final Map<N, GraphEdge<N>> adjacentNodesView = Collections.unmodifiableMap(adjacentNodes);

        private SimpleGraphConnection(@NonNull N node) {
            this.node = node;
        }

        @NonNull
        @Override
        public Set<N> getAdjacentNodes() {
            return adjacentNodesView.keySet();
        }

        @NonNull
        @Override
        public Optional<GraphEdge<N>> getEdgeTo(@NonNull N node) {
            return Optional.ofNullable(adjacentNodes.get(node));
        }

        @Override
        public boolean newConnection(@NonNull GraphEdge<N> edge) {
            if (edge.isOrdered()) {
                var orderedEdge = (OrderedEdge<N>) edge;
                adjacentNodes.put(orderedEdge.getTarget(), edge);
            } else {
                var u = edge.getNodeU();
                var v = edge.getNodeV();
                if (!Objects.equals(node, u)) {
                    adjacentNodes.put(u, edge);
                }

                if (!Objects.equals(node, v)) {
                    adjacentNodes.put(v, edge);
                }
            }

            return true;
        }

        @Override
        public boolean isDisjoint() {
            return adjacentNodes.isEmpty();
        }

        @NonNull
        @Override
        public String toString() {
            return "Connection(" + node + ": " + adjacentNodes.keySet() + ")";
        }
    }

}
