package webfx.tool.buildtool.util.streamable;

import webfx.tool.buildtool.util.spliterable.ThrowableSpliterable;
import webfx.tool.buildtool.util.streamable.impl.StreamableImpl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Bruno Salmon
 */
public interface Streamable<T> extends Iterable<T> {

    Streamable<T> filter(Predicate<? super T> predicate);

    <R> Streamable<R> map(Function<? super T, ? extends R> mapper);

    <R> Streamable<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper);

    Streamable<T> distinct();

    Streamable<T> cache();

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    static <T> Streamable<T> fromIterable(Iterable<T> iterable) {
        return new StreamableImpl<>(iterable);
    }

    static <T> Streamable<T> fromSpliterable(ThrowableSpliterable<T> throwableSpliterable) {
        return fromIterable(throwableSpliterable.toSpliterable());
    }
}