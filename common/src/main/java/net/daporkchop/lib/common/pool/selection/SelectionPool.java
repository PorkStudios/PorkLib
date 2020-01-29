/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.pool.selection;

import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A method of selecting a certain value out of a larger quantity.
 * <p>
 * This isn't so much a pool as a group which provides access to some collection of values.
 *
 * @author DaPorkchop_
 */
public interface SelectionPool<V> extends Supplier<V> {
    /**
     * Gets a pool with no values.
     *
     * @param <V> the type of value
     * @return an empty pool
     */
    static <V> SelectionPool<V> empty() {
        return EmptySelectionPool.getInstance();
    }

    /**
     * Constructs a new singleton pool with the given value.
     *
     * @param value the value
     * @param <V>   the type of the value
     * @return a new singleton pool with the given value
     */
    static <V> SelectionPool<V> singleton(@NonNull V value) {
        return new SingletonSelectionPool<>(value);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given array of values.
     *
     * @see #random(Object[], Random, boolean)
     */
    static <V> SelectionPool<V> random(@NonNull V[] values) {
        return random(values, null, false);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given array of values.
     *
     * @see #random(Object[], Random, boolean)
     */
    static <V> SelectionPool<V> random(@NonNull V[] values, Random random) {
        return random(values, random, false);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given array of values.
     *
     * @param values    the array of values to choose from
     * @param random    an {@link Random} to use for choosing a value. If {@code null}, {@link java.util.concurrent.ThreadLocalRandom} will be used
     * @param skipClone whether to skip cloning the array before creating the pool
     * @param <V>       the type of value
     * @return a new pool with the given values
     */
    static <V> SelectionPool<V> random(@NonNull V[] values, Random random, boolean skipClone) {
        switch (values.length) {
            case 0:
                return empty();
            case 1:
                return singleton(values[0]);
            default:
                return new RandomSelectionPool<>(skipClone ? values : values.clone(), random);
        }
    }

    /**
     * Constructs a new pool which will randomly select a value from the given {@link Stream} of values.
     *
     * @see #random(Stream, Random)
     */
    static <V> SelectionPool<V> random(@NonNull Stream<V> values) {
        return random(values, null);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given {@link Stream} of values.
     *
     * @param values the {@link Stream} of values to choose from
     * @param random an {@link Random} to use for choosing a value. If {@code null}, {@link java.util.concurrent.ThreadLocalRandom} will be used
     * @param <V>    the type of value
     * @return a new pool with the given values
     */
    @SuppressWarnings("unchecked")
    static <V> SelectionPool<V> random(@NonNull Stream<V> values, Random random) {
        return (SelectionPool<V>) random(values.toArray(Object[]::new), random, true);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given {@link Collection} of values.
     *
     * @see #random(Collection, Random)
     */
    static <V> SelectionPool<V> random(@NonNull Collection<V> values) {
        return random(values, null);
    }

    /**
     * Constructs a new pool which will randomly select a value from the given {@link Collection} of values.
     *
     * @param values the {@link Collection} of values to choose from
     * @param random an {@link Random} to use for choosing a value. If {@code null}, {@link java.util.concurrent.ThreadLocalRandom} will be used
     * @param <V>    the type of value
     * @return a new pool with the given values
     */
    @SuppressWarnings("unchecked")
    static <V> SelectionPool<V> random(@NonNull Collection<V> values, Random random) {
        return (SelectionPool<V>) random(values.toArray(), random, true);
    }

    /**
     * Constructs a new pool which will select a value from the given array of values using a round-robin method.
     *
     * @see #roundRobin(Object[], boolean)
     */
    static <V> SelectionPool<V> roundRobin(@NonNull V[] values) {
        return roundRobin(values, false);
    }

    /**
     * Constructs a new pool which will select a value from the given array of values using a round-robin method.
     *
     * @param values    the array of values to choose from
     * @param skipClone whether to skip cloning the array before creating the pool
     * @param <V>       the type of value
     * @return a new pool with the given values
     */
    static <V> SelectionPool<V> roundRobin(@NonNull V[] values, boolean skipClone) {
        switch (values.length) {
            case 0:
                return empty();
            case 1:
                return singleton(values[0]);
            default:
                return new RoundRobinSelectionPool<>(skipClone ? values : values.clone());
        }
    }

    /**
     * Constructs a new pool which will select a value from the given {@link Stream} of values using a round-robin method.
     *
     * @see #roundRobin(Stream)
     */
    @SuppressWarnings("unchecked")
    static <V> SelectionPool<V> roundRobin(@NonNull Stream<V> values) {
        return (SelectionPool<V>) roundRobin(values.toArray(Object[]::new), true);
    }

    /**
     * Constructs a new pool which will select a value from the given {@link Collection} of values using a round-robin method.
     *
     * @see #roundRobin(Collection)
     */
    @SuppressWarnings("unchecked")
    static <V> SelectionPool<V> roundRobin(@NonNull Collection<V> values) {
        return (SelectionPool<V>) roundRobin(values.toArray(), true);
    }

    /**
     * Retrieves any value from this pool.
     * <p>
     * Exactly which value will be returned is defined by the implementation.
     *
     * @return a value from this pool
     */
    V any();

    /**
     * Retrieves a {@link List} containing all values in this pool which match the given condition.
     * <p>
     * In the event that no values match the given condition, an empty {@link List} will be returned.
     *
     * @param condition the condition to match
     * @return a {@link List} containing all values in this pool which match the given condition
     */
    List<V> matching(@NonNull Predicate<V> condition);

    /**
     * Provided only for convenience so that a {@link SelectionPool} may be used as a {@link Supplier}.
     */
    @Override
    default V get() {
        return this.any();
    }
}
