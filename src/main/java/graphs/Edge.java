package graphs;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Abstract type of an edge of a graph; aka arc, link
 * For undirected graph an edge (u, v) is identical to the edge of (v, u).
 * For directed graph has an orientation(or an order) so that the edge (u, v) is the edge from node u, to node v.
 * In this manner we need to implement source and target methods to cope with those constraints.
 *
 * @param <N> type of a node
 */
public abstract class Edge<N> implements GraphEdge<N> {

    @NonNull
    private final N nodeU;
    @NonNull
    private final N nodeV;

    // Constructor methods

    public static <N> OrderedEdge<N> ordered(@NonNull N source, @NonNull N target) {
        return new Ordered<>(source, target);
    }

    public static <N> GraphEdge<N> unordered(@NonNull N nodeU, @NonNull N nodeV) {
        return new Unordered<>(nodeU, nodeV);
    }

    private Edge(@NonNull N nodeU, @NonNull N nodeV) {
        this.nodeU = nodeU;
        this.nodeV = nodeV;
    }

    /**
     * If graph {@link #isOrdered()} then returns {@link OrderedEdge#getSource()}
     */
    @NonNull
    @Override
    public N getNodeU() {
        return nodeU;
    }

    /**
     * If graph {@link #isOrdered()} then returns {@link OrderedEdge#getTarget()}
     */
    @NonNull
    @Override
    public N getNodeV() {
        return nodeV;
    }

    // Both implementations must implement these to comply with invariants
    public abstract int hashCode();

    public abstract boolean equals(@Nullable Object other);


    private static final class Ordered<N> extends Edge<N> implements OrderedEdge<N> {

        public Ordered(@NonNull N nodeU, @NonNull N nodeV) {
            super(nodeU, nodeV);
        }

        @NonNull
        @Override
        public N getSource() {
            return getNodeU();
        }

        @NonNull
        @Override
        public N getTarget() {
            return getNodeV();
        }

        @Override
        public boolean isOrdered() {
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSource(), getTarget());
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof OrderedEdge)) {
                return false;
            }

            var edge = (OrderedEdge<?>) other;
            return Objects.equals(getSource(), edge.getSource()) && Objects.equals(getTarget(), edge.getTarget());
        }

        @Override
        public String toString() {
            return "Edge(" + getSource() + "->" + getTarget() +")";
        }

    }

    private static final class Unordered<N> extends Edge<N> {

        public Unordered(@NonNull N nodeU, @NonNull N nodeV) {
            super(nodeU, nodeV);
        }

        @Override
        public boolean isOrdered() {
            return false;
        }

        @Override
        public int hashCode() {
            // In undirected graph edges (u, v) and (v, u) are the same and should produce the same hash-code
            return Objects.hash(getNodeU(), getNodeV()) + Objects.hash(getNodeV(), getNodeU());
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof GraphEdge)) {
                return false;
            }

            var edge = (GraphEdge<?>) other;
            if (edge.isOrdered()) {
                return false;
            }

            // edge (u, v) in undirected graph is the same as edge (v, u) so
            if (Objects.equals(getNodeU(), edge.getNodeU()) && Objects.equals(getNodeV(), edge.getNodeV())) {
                return true;
            }

            return Objects.equals(getNodeV(), edge.getNodeU()) && Objects.equals(getNodeU(), edge.getNodeV());
        }

        @Override
        public String toString() {
            return "Edge(" + getNodeU() + "--" + getNodeV() +")";
        }

    }

}
