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

import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * A data container that knows it's own offset in memory as well as it's total length in bytes, making it suitable for
 * usage with {@link sun.misc.Unsafe} (or in this case, {@link PUnsafe})
 *
 * @author DaPorkchop_
 */
public interface Offsettable {
    /**
     * Gets this container's offset in memory
     *
     * @return this container's offset in memory
     */
    long memoryAddress();

    /**
     * Gets the length (size) of this container in bytes.
     * <p>
     * In other words, the number of bytes representing this object following {@link #memoryAddress()}
     *
     * @return the length of this container in bytes
     */
    long memorySize();

    /**
     * Gets the object to use as a pointer when doing unsafe operations on this container (such as
     * {@link PUnsafe#putInt(Object, long, int)}).
     * <p>
     * If {@code null} is returned, the offset returned from {@link #memoryAddress()} may be treated as a direct
     * memory address.
     *
     * @return the object to use, or {@code null} if the offset is an address
     * @see #isAbsolute()
     */
    Object refObj();

    /**
     * Checks if the offset value returned from {@link #memoryAddress()} is absolute.
     * <p>
     * If it is absolute, then the offset should be treated as a direct memory address (as if it were returned by
     * {@link PUnsafe#allocateMemory(long)}).
     *
     * @return whether or not the offset value returned from {@link #memoryAddress()} is absolute
     */
    default boolean isAbsolute() {
        return this.refObj() == null;
    }

    /**
     * Gets this container's offset data
     *
     * @return offset data
     */
    default OffsetData data() {
        return new OffsetData(this.memoryAddress(), this.memorySize());
    }
}
