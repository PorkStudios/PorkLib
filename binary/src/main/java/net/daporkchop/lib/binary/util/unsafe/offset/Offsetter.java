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

package net.daporkchop.lib.binary.util.unsafe.offset;

import lombok.NonNull;

/**
 * The same as {@link Offsettable}, except for external objects (i.e. objects that can't simply have new interfaces added
 * on to them, such as arrays)
 *
 * @author DaPorkchop_
 */
public interface Offsetter<T> {
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
}
