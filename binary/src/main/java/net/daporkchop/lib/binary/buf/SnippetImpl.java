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
}
