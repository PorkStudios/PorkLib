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

package net.daporkchop.lib.collections.concurrent;

import lombok.NonNull;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;

/**
 * @author DaPorkchop_
 */
public interface ConcurrentPIterator<V> extends PIterator<ConcurrentPIterator.Entry<V>> {
    /**
     * A wrapper around a value in a concurrent iterator, to allow for multiple threads to actually do iteration concurrently
     *
     * @param <V> the value type
     */
    interface Entry<V> {
        /**
         * Gets this entry's value.
         *
         * @return this entry's value, or {@code null} if this entry has been removed.
         */
        V get();

        /**
         * Sets this entry's value. This will modify the value in the backing collection.
         *
         * @param value the new value to set
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        void set(@NonNull V value) throws AlreadyRemovedException;

        /**
         * Sets this entry's value. This will modify the value in the backing collection.
         *
         * @param value the new value to set
         * @return the old value
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        V replace(@NonNull V value) throws AlreadyRemovedException;

        /**
         * Removes this entry from the backing collection.
         * <p>
         * May only be invoked once per instance of {@link Entry}.
         *
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        void remove() throws AlreadyRemovedException;
    }
}
