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
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of a {@link NibbleArray} backed by direct memory.
 *
 * @author DaPorkchop_
 */
public abstract class DirectNibbleArray extends AbstractRefCounted implements NibbleArray {
    protected final long addr;

    protected final ByteBuf buf;

    public DirectNibbleArray() {
        this(Unpooled.directBuffer(MAX_INDEX >> 1, MAX_INDEX >> 1));
        PUnsafe.setMemory(this.addr, MAX_INDEX >> 1, (byte) 0);
    }

    public DirectNibbleArray(@NonNull ByteBuf buf) {
        checkArg(buf.hasMemoryAddress(), "buffer doesn't have an address!");
        checkRangeLen(buf.capacity(), buf.readerIndex(), MAX_INDEX >> 1);

        this.addr = buf.retain().memoryAddress() + buf.readerIndex();
        this.buf = buf;
    }

    @Override
    public int get(int offset) {
        checkIndex(offset >= 0 && offset < MAX_INDEX);
        return NibbleArray.extractNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)));
    }

    @Override
    public void set(int offset, int value) {
        checkIndex(offset >= 0 && offset < MAX_INDEX);
        checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
        PUnsafe.putByte(this.addr + (offset >> 1), (byte) NibbleArray.insertNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)), value));
    }

    @Override
    public abstract NibbleArray clone();

    @Override
    public NibbleArray retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.buf.release();
    }

    /**
     * Direct memory-based {@link NibbleArray} implementation using the YZX coordinate order.
     *
     * @author DaPorkchop_
     */
    public static final class YZX extends DirectNibbleArray {
        public YZX() {
            super();
        }

        public YZX(@NonNull ByteBuf buf) {
            super(buf);
        }

        @Override
        public int get(int x, int y, int z) {
            NibbleArray.checkCoords(x, y, z);
            int offset = (y << 8) | (z << 4) | x;
            return NibbleArray.extractNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)));
        }

        @Override
        public void set(int x, int y, int z, int value) {
            NibbleArray.checkCoords(x, y, z);
            checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
            int offset = (y << 8) | (z << 4) | x;
            PUnsafe.putByte(this.addr + (offset >> 1), (byte) NibbleArray.insertNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)), value));
        }

        @Override
        public NibbleArray clone() {
            ByteBuf buf = Unpooled.directBuffer(MAX_INDEX >> 1, MAX_INDEX >> 1);
            buf.writeBytes(Unpooled.wrappedBuffer(this.addr, MAX_INDEX >> 1, false));
            return new YZX(buf);
        }
    }

    /**
     * Direct memory-based {@link NibbleArray} implementation using the XZY coordinate order.
     *
     * @author DaPorkchop_
     */
    public static final class XZY extends DirectNibbleArray {
        public XZY() {
            super();
        }

        public XZY(@NonNull ByteBuf buf) {
            super(buf);
        }

        @Override
        public int get(int x, int y, int z) {
            NibbleArray.checkCoords(x, y, z);
            int offset = (x << 8) | (z << 4) | y;
            return NibbleArray.extractNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)));
        }

        @Override
        public void set(int x, int y, int z, int value) {
            NibbleArray.checkCoords(x, y, z);
            checkArg(value >= 0 && value < 16, "nibble value must be in range 0-15");
            int offset = (x << 8) | (z << 4) | y;
            PUnsafe.putByte(this.addr + (offset >> 1), (byte) NibbleArray.insertNibble(offset, PUnsafe.getByte(this.addr + (offset >> 1)), value));
        }

        @Override
        public NibbleArray clone() {
            ByteBuf buf = Unpooled.directBuffer(MAX_INDEX >> 1, MAX_INDEX >> 1);
            buf.writeBytes(Unpooled.wrappedBuffer(this.addr, MAX_INDEX >> 1, false));
            return new XZY(buf);
        }
    }
}
