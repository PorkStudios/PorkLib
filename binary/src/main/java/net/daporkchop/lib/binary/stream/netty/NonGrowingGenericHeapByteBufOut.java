/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.binary.stream.netty;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Variant of {@link GenericHeapByteBufOut} which doesn't allow the destination buffer to be grown.
 *
 * @author DaPorkchop_
 */
public class NonGrowingGenericHeapByteBufOut extends GenericHeapByteBufOut {
    public NonGrowingGenericHeapByteBufOut(@NonNull ByteBuf delegate, boolean autoRelease) {
        super(delegate, autoRelease);
    }

    protected void checkWritable(int count) throws NoMoreSpaceException {
        if (!this.delegate.isWritable(count)) {
            throw new NoMoreSpaceException("buffer isn't allowed to grow!", new IndexOutOfBoundsException(String.format(
                    "writerIndex(%d) + minWritableBytes(%d) exceeds capacity(%d): %s",
                    this.delegate.writerIndex(), count, this.delegate.capacity(), this.delegate)));
        }
    }

    @Override
    protected void write0(int b) throws NoMoreSpaceException, IOException {
        this.checkWritable(1);
        super.write0(b);
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, @Positive int length) throws NoMoreSpaceException, IOException {
        this.checkWritable(length);
        super.write0(src, start, length);
    }

    @Override
    protected void write0(long addr, @Positive long length) throws NoMoreSpaceException, IOException {
        this.checkWritable(toInt(length, "length"));
        super.write0(addr, length);
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.writeText(text, 0, text.length(), charset); //delegate to non-accelerated version
    }

    @Override
    public void writeByte(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Byte.BYTES);
        super.writeByte(b);
    }

    @Override
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Short.BYTES);
        super.writeShort(v);
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Short.BYTES);
        super.writeShortLE(v);
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Character.BYTES);
        super.writeChar(v);
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Character.BYTES);
        super.writeCharLE(v);
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Integer.BYTES);
        super.writeInt(v);
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Integer.BYTES);
        super.writeIntLE(v);
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Long.BYTES);
        super.writeLong(v);
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //this.ensureOpen(); is called by super
        this.checkWritable(Long.BYTES);
        super.writeLongLE(v);
    }
}
