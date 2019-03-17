/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.collections.stream;

import lombok.NonNull;
import net.daporkchop.lib.collections.PCollection;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.util.BaseCollection;
import net.daporkchop.lib.collections.util.exception.CannotMakeStreamConcurrentException;
import net.daporkchop.lib.collections.util.exception.CannotMakeStreamOrderedException;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.function.io.IOPredicate;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A simplification of {@link java.util.stream.Stream}.
 *
 * @param <V> the type to be used as a value
 * @author DaPorkchop_
 */
//TODO: more methods here, and add a few primitive stream types!
public interface PStream<V> extends BaseCollection {
    /**
     * Returns (an estimation of) the size required this stream.
     * <p>
     * If the size of this stream is unknown or for other reasons cannot be calculated, this method will return -1.
     *
     * @return the number of elements in this stream
     */
    @Override
    long size();

    /**
     * Checks whether or not this stream is ordered (i.e. all methods maintain the order of values while using them). Even
     * if only one method does not preserve element order, this method should return {@code false}.
     *
     * @return whether or not this stream is ordered
     */
    boolean isOrdered();

    /**
     * Returns a stream over the same data that supports ordered operations.
     * <p>
     * If this stream is already ordered, it will return itself.
     * <p>
     * If for whatever reason this stream is impossible to make into an ordered stream, it will return itself.
     *
     * @return a stream over the same data that supports concurrent operations
     */
    PStream<V> ordered();

    /**
     * Returns a stream over the same data that supports ordered operations.
     * <p>
     * If this stream is already ordered, it will return itself.
     * <p>
     * If for whatever reason this stream is impossible to make into an ordered stream, an exception will be thrown.
     *
     * @return a stream over the same data that supports concurrent operations
     * @throws CannotMakeStreamOrderedException if the stream cannot be ordered
     */
    default PStream<V> forceOrdered() throws CannotMakeStreamOrderedException {
        if (this.isOrdered()) {
            return this;
        }
        PStream<V> ordered = this.ordered();
        if (ordered.isOrdered()) {
            return ordered;
        } else {
            throw new CannotMakeStreamOrderedException("unknown reason");
        }
    }

    /**
     * Returns a stream over the same data that supports concurrent operations.
     * <p>
     * If this stream is already concurrent, it will return itself.
     * <p>
     * If for whatever reason this stream is impossible to make into a concurrent stream, it will return itself.
     *
     * @return a stream over the same data that supports concurrent operations
     */
    PStream<V> concurrent();

    /**
     * Returns a stream over the same data that supports concurrent operations.
     * <p>
     * If this stream is already concurrent, it will return itself.
     * <p>
     * If for whatever reason this stream is impossible to make into a concurrent stream, an exception will be thrown.
     *
     * @return a stream over the same data that supports concurrent operations
     * @throws CannotMakeStreamConcurrentException if the stream cannot be concurrent
     */
    default PStream<V> forceConcurrent() throws CannotMakeStreamConcurrentException {
        if (this.isConcurrent()) {
            return this;
        }
        PStream<V> concurrent = this.concurrent();
        if (concurrent.isConcurrent()) {
            return concurrent;
        } else {
            throw new CannotMakeStreamConcurrentException("unknown reason");
        }
    }

    /**
     * Iterates over all values in the stream, passing them as parameters to the given function.
     *
     * @param consumer the function to run
     */
    void forEach(@NonNull Consumer<V> consumer);

    /**
     * Iterates over all values in the stream, passing them as parameters to the given function.
     *
     * @param consumer the function to run
     * @return this stream
     */
    default PStream<V> forEachAndContinue(@NonNull Consumer<V> consumer) {
        this.forEach(consumer);
        return this;
    }

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOConsumer}
     *
     * @see #forEach(Consumer)
     */
    default void forEachIO(@NonNull IOConsumer<V> consumer) {
        this.forEach(consumer);
    }

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOConsumer}
     *
     * @see #forEachAndContinue(Consumer)
     */
    default PStream<V> forEachIOAndContinue(@NonNull IOConsumer<V> consumer) {
        this.forEach(consumer);
        return this;
    }

    /**
     * Maps every value in the stream to another value using a function, and returns a new stream consisting of the mapped
     * values.
     *
     * @param mappingFunction the function to be used for converting values to the new type
     * @param <T>             the new value type
     * @return a stream consisting of the mapped values. If this stream is concurrent, this will also return a concurrent stream.
     */
    <T> PStream<T> map(@NonNull Function<V, T> mappingFunction);

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOFunction}
     *
     * @see #map(Function)
     */
    default <T> PStream<T> mapIO(@NonNull IOFunction<V, T> mappingFunction) {
        return this.map(mappingFunction);
    }

    /**
     * Filters this stream down to only contain values that fit a certain condition.
     *
     * @param condition the condition that must be met for a value to remain in the stream
     * @return a stream consisting of every value that met the condition. If this stream is concurrent, this will also return a concurrent stream.
     */
    PStream<V> filter(@NonNull Predicate<V> condition);

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOFunction}
     *
     * @see #filter(Predicate)
     */
    default PStream<V> filterIO(@NonNull IOPredicate<V> condition) {
        return this.filter(condition);
    }

    /**
     * Filters this stream down to only contain values that fit a certain condition. Values will be mapped according to
     * the given mapping function before being fed to the condition.
     *
     * @param mappingFunction a function that will be used to map values to another type before checking them against the
     *                        condition
     * @param condition       the condition that must be met for every value in the stream
     * @param <T>             the intermediary type that will be passed to the condition
     * @return a stream consisting of every value that met the condition. If this stream is concurrent, this will also return a concurrent stream.
     */
    default <T> PStream<V> filter(@NonNull Function<V, T> mappingFunction, @NonNull Predicate<T> condition) {
        return this.filter(v -> condition.test(mappingFunction.apply(v)));
    }

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOFunction}
     * and {@link IOPredicate}
     *
     * @see #filter(Function, Predicate)
     */
    default <T> PStream<V> filterIO(@NonNull IOFunction<V, T> mappingFunction, @NonNull IOPredicate<T> condition) {
        return this.filterIO(mappingFunction, condition);
    }

    /**
     * Removes all duplicate elements from this stream.
     *
     * @param comparator a function that will be used to check for equality between objects
     * @return a stream consisting of every value in this stream, with duplicates removed. If this stream is concurrent, this will also return a concurrent stream.
     */
    PStream<V> distinct(@NonNull BiPredicate<V, V> comparator);

    /**
     * Removes all duplicate elements from this stream. Checks for equal objects via {@link Object#equals(Object)}.
     *
     * @return a stream consisting of every value in this stream, with duplicates removed. If this stream is concurrent, this will also return a concurrent stream.
     */
    default PStream<V> distinct() {
        return this.distinct(Object::equals);
    }

    /**
     * Removes all duplicate elements from this stream. Checks for equal objects via their identity, not by value.
     *
     * @return a stream consisting of every value in this stream, with duplicates removed. If this stream is concurrent, this will also return a concurrent stream.
     */
    default PStream<V> distinctIdentity() {
        return this.distinct((o1, o2) -> o1 == o2);
    }

    /**
     * Collects all the values in this stream into a single collection.
     *
     * @param collectionCreator a function that will provide an instance of a {@link PCollection} which will have the
     *                          contents of this stream added to it and then be returned
     * @param <T>               the type of collection to be returned
     * @return the collection
     */
    default <T extends PCollection<? super V>> T collect(@NonNull Supplier<T> collectionCreator) {
        return this.collect(collectionCreator.get());
    }

    /**
     * Collects all the values in this stream into a single collection.
     *
     * @param collection the collection that will have the contents of this stream added to it
     * @param <T>        the type of collection to be returned
     * @return the collection
     */
    default <T extends PCollection<? super V>> T collect(@NonNull T collection) {
        collection.clear();
        this.forEach(collection::add);
        return collection;
    }

    /**
     * Converts this stream into a {@link PMap}.
     *
     * @param keyExtractor   a function that will convert values from this stream into the key type to be used in the map
     * @param valueExtractor a function that will convert values from this stream into the value type to be used in the map
     * @param mapCreator     a function that will provide an instance of a {@link PMap} which will have the key => value pairs
     *                       generated by the two other functions added to it and then be returned
     * @param <Key>          the key type to be used in the map
     * @param <Value>        the value type to be used in the map
     * @param <T>            the type of map to be returned
     * @return the map
     */
    <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator);

    /**
     * Converts this stream into an array.
     *
     * @param arrayCreator a function that will create an array of this stream's value type with a given size
     * @return the contents of this stream as an array
     */
    V[] toArray(@NonNull IntFunction<V[]> arrayCreator);

    @Override
    default void clear() {
        throw new UnsupportedOperationException("clear");
    }
}
