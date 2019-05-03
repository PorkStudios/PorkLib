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

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.buf.exception.PorkBufReadOutOfBoundsException;
import net.daporkchop.lib.binary.buf.exception.PorkBufWriteOutOfBoundsException;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.nio.ByteBuffer;

/**
 * An implementation of a {@link PorkBuf} that operates on a specific region of another
 * {@link PorkBuf} instance.
 *
 * @author DaPorkchop_
 */
//TODO: add implementations of all read/write methods for performance
@Getter
public class SnippetImpl extends AbstractPorkBuf {
    protected final long offset;

    protected final PorkBuf delegate;

    public SnippetImpl(long offset, long length, @NonNull PorkBuf delegate) {
        this.capacity = this.maxCapacity = length;
        this.offset = offset;
        this.delegate = delegate;
    }

    @Override
    public PorkBuf putByte(byte b) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(1);
        this.delegate.putByte(this.offset + this.writerIndex++, b);
        return this;
    }

    @Override
    public PorkBuf putByte(long index, byte b) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 1, false);
        this.delegate.putByte(this.offset + index, b);
        return this;
    }

    @Override
    public byte getByte() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(1);
        return this.delegate.getByte(this.offset + this.readerIndex++);
    }

    @Override
    public byte getByte(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 1, true);
        return this.delegate.getByte(this.offset + index);
    }

    @Override
    public long memoryAddress() {
        return this.delegate.memoryAddress() + this.offset;
    }

    @Override
    public long memorySize() {
        return this.maxCapacity;
    }

    @Override
    public Object refObj() {
        return this.delegate.refObj();
    }

    //forward snippet, copy methods etc. to parent for performance reasons
    @Override
    public DataOut outputStream(long offset, long limit) {
        this.ensureInBounds(offset, limit, false);
        return this.delegate.outputStream(this.offset + offset, this.offset + limit);
    }

    @Override
    public DataIn inputStream(long offset, long limit) {
        this.ensureInBounds(offset, limit, true);
        return this.delegate.inputStream(this.offset + offset, this.offset + limit);
    }

    @Override
    public ByteBuf netty(long offset, int len) {
        this.ensureInBounds(offset, len, true);
        return this.delegate.netty(this.offset + offset, len);
    }

    @Override
    public ByteBuffer nio(long offset, int len) {
        this.ensureInBounds(offset, len, true);
        return this.delegate.nio(this.offset + offset, len);
    }

    @Override
    public PorkBuf snippet(long offset, long len) {
        this.ensureInBounds(offset, len, true);
        return this.delegate.snippet(this.offset + offset, len);
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }
}
