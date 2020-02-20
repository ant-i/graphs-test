package graphs.concurrent;

import graphs.GraphEdge;
import graphs.MutableGraph;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A synchronized delegate graph implementation.
 * Delegates all graph functionality to a specified backed graph providing synchronization
 * via read-write lock.
 * <p>
 * Any reading and traversal operations are allowed in parallel iff no other thread is mutating the graph.
 * Any mutation operations are allowed only when there are no threads reading or writing.
 * <p>
 * This is a pretty straight-forward solution to thread-safety.
 *
 * @param <N> Graph vertex/node type
 */
public class ReadWriteSynchronizedGraph<N> implements MutableGraph<N> {

    @NonNull
    private final MutableGraph<N> delegate;

    @NonNull
    private final ReentrantReadWriteLock readWriteLock;

    public ReadWriteSynchronizedGraph(@NonNull MutableGraph<N> delegate, boolean fair) {
        this.delegate = delegate;
        this.readWriteLock = new ReentrantReadWriteLock(fair);
    }

    @Override
    public boolean addVertex(@NonNull N node) {
        readWriteLock.writeLock().lock();
        try {
            return delegate.addVertex(node);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean addEdge(@NonNull N nodeU, @NonNull N nodeV) {
        readWriteLock.writeLock().lock();
        try {
            return delegate.addEdge(nodeU, nodeV);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public @NonNull List<GraphEdge<N>> getPath(@NonNull N source, @NonNull N target) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getPath(source, target);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public @NonNull Set<N> getNodes() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getNodes();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public @NonNull Set<GraphEdge<N>> getEdges() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getEdges();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean isDirected() {
        return delegate.isDirected();
    }

    @NonNull
    @Override
    public String toString() {
        readWriteLock.readLock().lock();
        try {
            return delegate.toString();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

}
