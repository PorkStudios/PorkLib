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

package net.daporkchop.lib.common.pool.selection;

import lombok.NonNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * A method of selecting a certain value out of a larger quantity.
 *
 * @author DaPorkchop_
 */
public interface SelectionPool<V> {
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
     * Retrieves any value from this pool which matches the given condition.
     * <p>
     * Unless explicitly stated in the implementation, this is expected to behave identically to {@link #any()}, with the only difference being that a value
     * must match the given condition to be eligible to be returned.
     * <p>
     * In the event that no values match the given condition, {@code null} will be returned.
     *
     * @param condition the condition to match
     * @return a value from this pool which matches the given condition
     */
    V anyMatching(@NonNull Predicate<V> condition);
}
