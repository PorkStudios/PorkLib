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
import lombok.NonNull;
import net.daporkchop.lib.binary.Endianess;
import net.daporkchop.lib.binary.buffer.PorkBuf;
import net.daporkchop.lib.unsafe.PUnsafe;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

/**
 * Base implementation of a {@link net.daporkchop.lib.binary.buffer.PorkBuf} backed by a single {@code byte[]}.
 *
 * @author DaPorkchop_
 */
public abstract class BaseHeapPorkBuf extends AbstractPorkBuf {
    protected byte[] arr;

    public BaseHeapPorkBuf(@NonNull byte[] arr, int capacity, int maxCapacity) {
        this.arr = arr;
        this.capacity(capacity);
        this.maxCapacity(maxCapacity);
    }

    @Override
    protected void expand0(long from, long capacity) {
        if (capacity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Heap array too big: " + capacity);
        } else {
            byte[] newArray = new byte[(int) capacity];
            System.arraycopy(this.arr, 0, newArray, 0, this.arr.length);
            this.arr = newArray;
        }
    }

    @Override
    protected void setByte0(long index, byte val) {
        PUnsafe.putByte(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, val);
    }

    @Override
    protected void setShort0(long index, short val) {
        PUnsafe.putShort(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.BIG.isNative() ? val : Short.reverseBytes(val));
    }

    @Override
    protected void setShortLE0(long index, short val) {
        PUnsafe.putShort(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.LITTLE.isNative() ? val : Short.reverseBytes(val));
    }

    @Override
    protected void setChar0(long index, char val) {
        PUnsafe.putChar(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.BIG.isNative() ? val : Character.reverseBytes(val));
    }

    @Override
    protected void setCharLE0(long index, char val) {
        PUnsafe.putChar(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.LITTLE.isNative() ? val : Character.reverseBytes(val));
    }

    @Override
    protected void setInt0(long index, int val) {
        PUnsafe.putInt(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.BIG.isNative() ? val : Integer.reverseBytes(val));
    }

    @Override
    protected void setIntLE0(long index, int val) {
        PUnsafe.putInt(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.LITTLE.isNative() ? val : Integer.reverseBytes(val));
    }

    @Override
    protected void setLong0(long index, long val) {
        PUnsafe.putLong(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.BIG.isNative() ? val : Long.reverseBytes(val));
    }

    @Override
    protected void setLongLE0(long index, long val) {
        PUnsafe.putLong(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Endianess.LITTLE.isNative() ? val : Long.reverseBytes(val));
    }

    @Override
    protected void setFloat0(long index, float val) {
        if (Endianess.BIG.isNative())   {
            PUnsafe.putFloat(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, val);
        } else {
            PUnsafe.putInt(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Integer.reverseBytes(Float.floatToRawIntBits(val)));
        }
    }

    @Override
    protected void setFloatLE0(long index, float val) {
        if (Endianess.LITTLE.isNative())   {
            PUnsafe.putFloat(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, val);
        } else {
            PUnsafe.putInt(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Integer.reverseBytes(Float.floatToRawIntBits(val)));
        }
    }

    @Override
    protected void setDouble0(long index, double val) {
        if (Endianess.BIG.isNative())   {
            PUnsafe.putDouble(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, val);
        } else {
            PUnsafe.putLong(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Long.reverseBytes(Double.doubleToRawLongBits(val)));
        }
    }

    @Override
    protected void setDoubleLE0(long index, double val) {
        if (Endianess.LITTLE.isNative())   {
            PUnsafe.putDouble(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, val);
        } else {
            PUnsafe.putLong(this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, Long.reverseBytes(Double.doubleToRawLongBits(val)));
        }
    }

    @Override
    protected void setBytes0(long index, byte[] arr, int start, int length) {
        PUnsafe.copyMemory(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + start, this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, length);
    }

    @Override
    protected void setBytes0(long index, PorkBuf buf, long start, long length) {
        //TODO
    }

    @Override
    protected void setBytes0(long index, ByteBuf buf, int start, int length) {
        buf.getBytes(start, this.arr, (int) index, length);
    }

    @Override
    protected void setBytes0(long index, ByteBuffer buf, int start, int length) {
        if (buf.hasArray()) {
            System.arraycopy(buf.array(), buf.arrayOffset() + start, this.arr, (int) index, length);
        } else {
            //TODO: make this nicer
            PUnsafe.copyMemory(null, ((DirectBuffer) buf).address(), this.arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + index, length);
        }
    }
}
