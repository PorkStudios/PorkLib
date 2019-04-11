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

package net.daporkchop.lib.unsafe.block.offset;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * The same as {@link Offsettable}, except for external objects (i.e. objects that can't simply have new interfaces added
 * on to them, such as arrays)
 *
 * @author DaPorkchop_
 */
public interface Offsetter<T> {
    /**
     * Generates a simple offsetter using lambdas
     *
     * @param offset a function that will calculate the offset of a value
     * @param length a function that will calculate the length of a value
     * @param <T>    the type to create an offsetter for
     * @return the newly created offsettter
     */
    static <T> Offsetter<T> of(@NonNull ToLongFunction<T> offset, @NonNull ToLongFunction<T> length) {
        return of(offset, length, null);
    }

    /**
     * Generates a simple offsetter using lambdas
     *
     * @param offset a function that will calculate the offset of a value
     * @param length a function that will calculate the length of a value
     * @param ref    a function that will get the object to use as a pointer for unsafe memory operations (see {@link #refObj(Object)}
     * @param <T>    the type to create an offsetter for
     * @return the newly created offsettter
     */
    static <T> Offsetter<T> of(@NonNull ToLongFunction<T> offset, @NonNull ToLongFunction<T> length, Function<T, Object> ref) {
        return new Offsetter<T>() {
            @Override
            public long memoryOffset(@NonNull T val) {
                return offset.applyAsLong(val);
            }

            @Override
            public long memoryLength(@NonNull T val) {
                return length.applyAsLong(val);
            }

            @Override
            public Object refObj(@NonNull T val) {
                return ref == null ? null : ref.apply(val);
            }

            @Override
            public boolean isAbsolute() {
                return ref == null;
            }
        };
    }

    /**
     * Gets a container's offset in memory
     *
     * @param val the container
     * @return a container's offset in memory
     */
    long memoryOffset(@NonNull T val);

    /**
     * Gets the length (size) of a container in bytes.
     * <p>
     * In other words, the number of bytes representing this object following {@link #memoryOffset(Object)}
     *
     * @param val the container
     * @return the length of a container in bytes
     */
    long memoryLength(@NonNull T val);

    /**
     * Gets the object to use as a pointer when doing unsafe operations on the container (such as
     * {@link PUnsafe#putInt(Object, long, int)}).
     * <p>
     * If {@code null} is returned, the offset returned from {@link #memoryOffset(Object)} may be treated as a direct
     * memory address.
     *
     * @param val the container
     * @return the object to use, or {@code null} if the offset is an address
     * @see #isAbsolute()
     */
    Object refObj(@NonNull T val);

    /**
     * Checks if the offset value returned by this offsetter is absolute.
     * <p>
     * If it is absolute, then the offset should be treated as a direct memory address (as if it were returned by
     * {@link PUnsafe#allocateMemory(long)}).
     *
     * @return whether or not the offset value returned by this offsetter is absolute
     */
    boolean isAbsolute();

    /**
     * Gets a container's offset data
     *
     * @param val the container
     * @return offset data
     */
    default OffsetData data(@NonNull T val) {
        return new OffsetData(this.memoryOffset(val), this.memoryLength(val));
    }
}
