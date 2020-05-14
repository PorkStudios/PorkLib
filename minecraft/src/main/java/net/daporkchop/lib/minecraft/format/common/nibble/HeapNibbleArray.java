/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.format.common.nibble;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.common.pool.handle.Handle;

import java.util.Arrays;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of a {@link NibbleArray} backed by a heap {@code byte[]}.
 *
 * @author DaPorkchop_
 */
public abstract class HeapNibbleArray implements NibbleArray {
    protected final byte[] arr;
    protected final int offset;

    protected final Handle<byte[]> handle;
    protected final ByteBuf buf;

    public HeapNibbleArray() {
        this(new byte[2048], 0);
    }

    public HeapNibbleArray(@NonNull byte[] arr, int offset) {
        checkRangeLen(arr.length, offset, MAX_INDEX >> 1);

        this.arr = arr;
        this.offset = notNegative(offset, "offset");
        this.handle = null;
        this.buf = null;
    }

    public HeapNibbleArray(@NonNull Handle<byte[]> handle) {
        checkRange(handle instanceof ArrayHandle ? ((ArrayHandle) handle).length() : handle.get().length, 0, MAX_INDEX >> 1);

        this.arr = handle.retain().get();
        this.offset = 0;
        this.handle = handle;
        this.buf = null;
    }

    public HeapNibbleArray(@NonNull ByteBuf buf) {
        checkArg(buf.hasArray(), "buffer doesn't have an array!");
        checkRangeLen(buf.capacity(), buf.readerIndex(), MAX_INDEX >> 1);

        this.arr = buf.retain().array();
        this.offset = buf.arrayOffset() + buf.readerIndex();
        this.handle = null;
        this.buf = buf;
    }

    @Override
    public int get(int offset) {
        checkIndex(offset >= 0 && offset < MAX_INDEX);
        return NibbleArray.extractNibble(offset, this.arr[this.offset + (offset >> 1)]);
    }

    @Override
    public void set(int offset, int value) {
        checkIndex(offset >= 0 && offset < MAX_INDEX);
        checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
        this.arr[this.offset + (offset >> 1)] = (byte) NibbleArray.insertNibble(offset, this.arr[this.offset + (offset >> 1)], value);
    }

    @Override
    public abstract NibbleArray clone();

    @Override
    public void close() {
        if (this.handle != null) {
            this.handle.release();
        }
        if (this.buf != null) {
            this.buf.release();
        }
    }

    public static final class YZX extends HeapNibbleArray {
        public YZX() {
            super();
        }

        public YZX(@NonNull byte[] arr, int offset) {
            super(arr, offset);
        }

        public YZX(@NonNull Handle<byte[]> handle) {
            super(handle);
        }

        public YZX(@NonNull ByteBuf buf) {
            super(buf);
        }

        @Override
        public int get(int x, int y, int z) {
            NibbleArray.checkCoords(x, y, z);
            int offset = (y << 8) | (z << 4) | x;
            return NibbleArray.extractNibble(offset, this.arr[this.offset + (offset >> 1)]);
        }

        @Override
        public void set(int x, int y, int z, int value) {
            NibbleArray.checkCoords(x, y, z);
            checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
            int offset = (y << 8) | (z << 4) | x;
            this.arr[this.offset + (offset >> 1)] = (byte) NibbleArray.insertNibble(offset, this.arr[this.offset + (offset >> 1)], value);
        }

        @Override
        public NibbleArray clone() {
            return new YZX(Arrays.copyOfRange(this.arr, this.offset, MAX_INDEX >> 1), 0);
        }
    }

    public static final class XZY extends HeapNibbleArray {
        public XZY() {
            super();
        }

        public XZY(@NonNull byte[] arr, int offset) {
            super(arr, offset);
        }

        public XZY(@NonNull Handle<byte[]> handle) {
            super(handle);
        }

        public XZY(@NonNull ByteBuf buf) {
            super(buf);
        }

        @Override
        public int get(int x, int y, int z) {
            NibbleArray.checkCoords(x, y, z);
            int offset = (x << 8) | (z << 4) | y;
            return NibbleArray.extractNibble(offset, this.arr[this.offset + (offset >> 1)]);
        }

        @Override
        public void set(int x, int y, int z, int value) {
            NibbleArray.checkCoords(x, y, z);
            checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
            int offset = (x << 8) | (z << 4) | y;
            this.arr[this.offset + (offset >> 1)] = (byte) NibbleArray.insertNibble(offset, this.arr[this.offset + (offset >> 1)], value);
        }

        @Override
        public NibbleArray clone() {
            return new XZY(Arrays.copyOfRange(this.arr, this.offset, MAX_INDEX >> 1), 0);
        }
    }
}
