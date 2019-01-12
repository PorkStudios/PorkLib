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

package net.daporkchop.lib.binary.buf;

/**
 * Version 2.0 of the PorkBuf! However, unlike the old one, this one isn't crap.
 * <p>
 * Doesn't really have any advantages over NIO's {@link java.nio.ByteBuffer} or Netty's
 * {@link io.netty.buffer.ByteBuf} except for the fact that it supports 64-bit length.
 *
 * @author DaPorkchop_
 */
public interface PorkBuf {
    /**
     * Gets the current capacity of this buffer (i.e. the number of bytes it can hold without expanding)
     *
     * @return the current capacity of this buffer
     */
    long capacity();

    /**
     * Sets this buffer's capacity to the given number of bytes.
     * <p>
     * If larger than the current capacity, newly added bytes will be set to 0. If smaller than the current
     * capacity, the contents will be truncated.
     * <p>
     * Does not have to be implemented.
     *
     * @param capacity the new capacity
     * @return this buffer
     */
    default PorkBuf setCapacity(long capacity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Ensures that the buffer has at least a certain capacity, expanding if required.
     *
     * @param numBytes the minimum number of bytes required
     * @return this buffer
     */
    default PorkBuf requireBytes(long numBytes) {
        this.requireBytes(0L, numBytes);
        return this;
    }

    /**
     * Ensures that the buffer has at least a certain capacity, expanding if required.
     *
     * @param offset   the offset that bytes will be required at
     * @param numBytes the minimum number of bytes required
     * @return this buffer
     */
    default PorkBuf requireBytes(long offset, long numBytes) {
        if (this.capacity() < offset + numBytes) {
            this.setCapacity(offset + numBytes);
        }
        return this;
    }

    /**
     * Gets this buffer's maximum allowed capacity
     * @return this buffer's maximum allows capacity
     */
    default long maxCapacity()  {
        return this.capacity();
    }

    default PorkBuf setMaxCapacity(long maxCapacity)    {
        throw new UnsupportedOperationException();
    }
}
