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

package net.daporkchop.lib.collections.util;

/**
 * Some shared methods across all types of collections.
 * <p>
 * Implementations of this class are expected to be thread-safe at a minimum, optionally supporting full concurrency.
 *
 * @author DaPorkchop_
 */
public interface BaseCollection {
    /**
     * Gets this collection's size (the number of values in the collection).
     *
     * @return this collection's size
     */
    long size();

    /**
     * Checks whether or not this collection is empty (i.e. contains no values).
     *
     * @return whether or not this collection is empty
     */
    default boolean isEmpty() {
        return this.size() == 0L;
    }

    /**
     * Removes all values from this collection.
     */
    void clear();

    /**
     * Checks whether or not this collection supports concurrent access. All implementations of {@link BaseCollection}
     * are required to be thread-safe, but full concurrency is optional, so this provides a way of checking whether an
     * implementation supports concurrency or not.
     *
     * @return whether or not this collection supports concurrent access
     */
    boolean isConcurrent();
}
