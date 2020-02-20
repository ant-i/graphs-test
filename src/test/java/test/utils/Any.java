package test.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Simple class for testing purposes
 *
 * @param <A>
 */
public class Any<A> {

    @NonNull
    private final List<A> delegate;

    private Any(@NonNull List<A> delegate) {
        this.delegate = delegate;
    }

    @SafeVarargs
    public static <A> Any<A> of(A... as) {
        return new Any<>(Arrays.asList(as));
    }

    public boolean anyTrue(@NonNull Predicate<A> predicate) {
        return delegate.stream().anyMatch(predicate);
    }
}
