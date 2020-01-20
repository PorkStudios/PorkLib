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

package net.daporkchop.lib.binary.buffer.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.buffer.PorkBuf;

/**
 * A base implementation of {@link PorkBuf} that implements the basic behaviors of reader and writer indices.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractPorkBuf implements PorkBuf {
    protected static void validateIndices(long readerIndex, long writerIndex, long capacity) throws IndexOutOfBoundsException {
        if (readerIndex < 0L || writerIndex < readerIndex || capacity < writerIndex) {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d, capacity: %d", readerIndex, writerIndex, capacity));
        }
    }

    private long readerIndex;
    private long writerIndex;
    private long capacity;
    private long maxCapacity;

    @Override
    public PorkBuf readerIndex(long readerIndex) throws IndexOutOfBoundsException {
        validateIndices(readerIndex, this.writerIndex, this.capacity);
        this.readerIndex = readerIndex;
        return this;
    }

    @Override
    public PorkBuf writerIndex(long writerIndex) throws IndexOutOfBoundsException {
        validateIndices(this.readerIndex, writerIndex, this.capacity);
        this.writerIndex = writerIndex;
        return this;
    }

    @Override
    public PorkBuf ensureWritable(long count) throws IndexOutOfBoundsException {
        if (count < 0L) {
            throw new IllegalArgumentException(String.valueOf(count));
        }
        return this.ensureCapacity(this.writerIndex + count);
    }

    @Override
    public PorkBuf ensureCapacity(long count) throws IndexOutOfBoundsException {
        if (count < 0L) {
            throw new IllegalArgumentException(String.valueOf(count));
        }
        if (count > this.capacity)  {
            if (count > this.maxCapacity)   {
                throw new IndexOutOfBoundsException(String.format("count: %d, maxCapacity: %d", count, this.maxCapacity));
            }

            long targetCapacity = this.capacity;
            do {
                targetCapacity = Math.min(this.maxCapacity, targetCapacity << 1L);
            } while (targetCapacity < count);

            //TODO: this could cause threading issues...
            this.doExpand(this.capacity, targetCapacity);
            this.capacity = targetCapacity;
        }
        return this;
    }

    @Override
    public long readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    @Override
    public long writableBytes() {
        return this.capacity - this.writerIndex;
    }

    /**
     * Expands this {@link PorkBuf}'s capacity.
     *
     * @param from     the current capacity
     * @param capacity the capacity to expand to
     */
    protected abstract void doExpand(long from, long capacity);
}
