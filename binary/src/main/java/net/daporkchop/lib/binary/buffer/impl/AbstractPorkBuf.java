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

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.buffer.PorkBuf;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.Endianess;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.nio.ByteBuffer;

/**
 * A base implementation of {@link PorkBuf} that implements the basic behaviors of reader and writer indices.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractPorkBuf extends PorkBuf {
    protected static void validateIndices(long readerIndex, long writerIndex, long capacity) throws IndexOutOfBoundsException {
        if (readerIndex < 0L || writerIndex < readerIndex || capacity < writerIndex) {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d, capacity: %d", readerIndex, writerIndex, capacity));
        }
    }

    protected static void validateBounds(long index, long length, long capacity) throws IndexOutOfBoundsException {
        if (index < 0L || length < 0L || capacity < index + length) {
            throw new IndexOutOfBoundsException(String.format("index: %d, length: %d, capacity: %d", index, length, capacity));
        }
    }

    private long readerIndex;
    private long writerIndex;

    @Setter(AccessLevel.PROTECTED)
    private long capacity;
    @Setter(AccessLevel.PROTECTED) //TODO: use actual setter implementations here
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
        } else if (count > this.capacity) {
            if (count > this.maxCapacity) {
                throw new IndexOutOfBoundsException(String.format("count: %d, maxCapacity: %d", count, this.maxCapacity));
            }

            //TODO: smarter
            synchronized (this) {
                long targetCapacity = this.capacity;
                do {
                    targetCapacity = Math.min(this.maxCapacity, targetCapacity << 1L);
                } while (targetCapacity < count);

                this.expand0(this.capacity, targetCapacity);
                this.capacity = targetCapacity;
            }
        }
        return this;
    }

    @Override
    public PorkBuf skip(long count) throws IndexOutOfBoundsException {
        if (count < 0L) {
            throw new IllegalArgumentException(String.valueOf(count));
        }
        long readerIndex = this.readerIndex;
        validateIndices(readerIndex + count, this.writerIndex, this.capacity);
        this.readerIndex = readerIndex + count;
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
    protected abstract void expand0(long from, long capacity);

    //
    //
    // Indexed write methods
    //
    //


    @Override
    public PorkBuf setBoolean(long index, boolean val) {
        validateBounds(index, 1L, this.capacity);
        this.setBoolean0(index, val);
        return this;
    }

    protected abstract void setBoolean0(long index, boolean val);

    @Override
    public PorkBuf setByte(long index, byte val) {
        validateBounds(index, 1L, this.capacity);
        this.setByte0(index, val);
        return this;
    }

    @Override
    public PorkBuf setByte(long index, int val) {
        validateBounds(index, 1L, this.capacity);
        this.setByte0(index, (byte) val);
        return this;
    }

    protected abstract void setByte0(long index, byte val);

    @Override
    public PorkBuf setShort(long index, short val) {
        return this.setShortNE(index, Endianess.BIG_ENDIAN_NATIVE ? val : Short.reverseBytes(val));
    }

    @Override
    public PorkBuf setShortLE(long index, short val) {
        //TODO: something something unaligned access won't work, need to fix
        return this.setShortNE(index, Endianess.LITTLE_ENDIAN_NATIVE ? val : Short.reverseBytes(val));
    }

    @Override
    public PorkBuf setShortNE(long index, short val) {
        validateBounds(index, 2L, this.capacity);
        this.setShort0(index, val);
        return this;
    }

    protected abstract void setShort0(long index, short val);

    @Override
    public PorkBuf setChar(long index, char val) {
        return null;
    }

    @Override
    public PorkBuf setCharLE(long index, char val) {
        return null;
    }

    @Override
    public PorkBuf setCharNE(long index, char val) {
        return null;
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public long memoryAddress() {
        return 0;
    }

    @Override
    public int refCnt() {
        return 0;
    }

    @Override
    public PorkBuf retain() throws AlreadyReleasedException {
        return null;
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        return false;
    }

    @Override
    public PorkBuf setInt(long index, int val) {
        return null;
    }

    @Override
    public PorkBuf setIntLE(long index, int val) {
        return null;
    }

    @Override
    public PorkBuf setIntNE(long index, int val) {
        return null;
    }

    @Override
    public PorkBuf setLong(long index, long val) {
        return null;
    }

    @Override
    public PorkBuf setLongLE(long index, long val) {
        return null;
    }

    @Override
    public PorkBuf setLongNE(long index, long val) {
        return null;
    }

    @Override
    public PorkBuf setFloat(long index, float val) {
        return null;
    }

    @Override
    public PorkBuf setFloatLE(long index, float val) {
        return null;
    }

    @Override
    public PorkBuf setFloatNE(long index, float val) {
        return null;
    }

    @Override
    public PorkBuf setDouble(long index, double val) {
        return null;
    }

    @Override
    public PorkBuf setDoubleLE(long index, double val) {
        return null;
    }

    @Override
    public PorkBuf setDoubleNE(long index, double val) {
        return null;
    }

    @Override
    public PorkBuf setBytes(long index, @NonNull byte[] arr, int start, int length) throws IndexOutOfBoundsException {
        PorkUtil.assertInRangeLen(arr.length, start, length);
        validateBounds(index, length, this.capacity);
        this.setBytes0(index, arr, start, length);
        return this;
    }

    protected abstract void setBytes0(long index, byte[] arr, int start, int length);

    @Override
    public PorkBuf setBytes(long index, @NonNull PorkBuf buf, long length) throws IndexOutOfBoundsException {
        long readerIndex = buf.readerIndex();
        validateBounds(readerIndex, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        buf.skip(length);
        this.setBytes0(index, buf, readerIndex, length);
        buf.readerIndex(readerIndex + length);
        return this;
    }

    @Override
    public PorkBuf setBytes(long index, @NonNull PorkBuf buf, long start, long length) throws IndexOutOfBoundsException {
        validateBounds(start, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        this.setBytes0(index, buf, start, length);
        return this;
    }

    protected abstract void setBytes0(long index, PorkBuf buf, long start, long length);

    @Override
    public PorkBuf setBytes(long index, @NonNull ByteBuf buf, int length) throws IndexOutOfBoundsException {
        int readerIndex = buf.readerIndex();
        validateBounds(readerIndex, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        buf.skipBytes(length);
        this.setBytes0(index, buf, readerIndex, length);
        return this;
    }

    @Override
    public PorkBuf setBytes(long index, @NonNull ByteBuf buf, int start, int length) throws IndexOutOfBoundsException {
        validateBounds(start, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        this.setBytes0(index, buf, start, length);
        return this;
    }

    protected abstract void setBytes0(long index, ByteBuf buf, int start, int length);

    @Override
    public PorkBuf setBytes(long index, @NonNull ByteBuffer buf, int length) throws IndexOutOfBoundsException {
        int position = buf.position();
        validateBounds(position, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        buf.position(position + length);
        this.setBytes0(index, buf, position, length);
        return this;
    }

    @Override
    public PorkBuf setBytes(long index, @NonNull ByteBuffer buf, int start, int length) throws IndexOutOfBoundsException {
        validateBounds(start, length, buf.capacity());
        validateBounds(index, length, this.capacity);
        this.setBytes0(index, buf, start, length);
        return this;
    }

    protected abstract void setBytes0(long index, ByteBuffer buf, int start, int length);
}
