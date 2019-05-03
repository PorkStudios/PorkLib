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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.buf.exception.PorkBufReadOutOfBoundsException;
import net.daporkchop.lib.binary.buf.exception.PorkBufWriteOutOfBoundsException;
import net.daporkchop.lib.common.util.PorkUtil;

import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NIOBufferWrapper extends AbstractPorkBuf {
    @NonNull
    protected final ByteBuffer delegate;
    protected boolean closed;

    @Override
    public long capacity() {
        return this.delegate.capacity();
    }

    @Override
    public long maxCapacity() {
        return this.delegate.capacity();
    }

    @Override
    public PorkBuf putByte(byte b) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(1);
        this.delegate.put((int) this.writerIndex++, b);
        return this;
    }

    @Override
    public PorkBuf putByte(long index, byte b) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 1, false);
        this.delegate.put((int) index, b);
        return this;
    }

    @Override
    public byte getByte() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(1);
        return this.delegate.get((int) this.readerIndex++);
    }

    @Override
    public byte getByte(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 1, true);
        return this.delegate.get((int) index);
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long memorySize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object refObj() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        PorkUtil.release(this.delegate);
        this.closed = true;
    }
}
