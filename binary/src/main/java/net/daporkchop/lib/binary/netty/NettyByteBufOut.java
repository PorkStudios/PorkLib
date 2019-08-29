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

package net.daporkchop.lib.binary.netty;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.OldDataOut;

import java.io.IOException;

/**
 * An implementation of {@link OldDataOut} that can write to a {@link ByteBuf}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class NettyByteBufOut extends OldDataOut {
    static {
        NettyUtil.ensureNettyPresent();
    }

    @NonNull
    protected ByteBuf buf;

    @Override
    public void write(int b) throws IOException {
        this.ensureOpen();
        this.buf.writeByte(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        this.ensureOpen();
        this.buf.writeBytes(b, off, len);
    }

    @Override
    public OldDataOut writeBoolean(boolean b) throws IOException {
        this.ensureOpen();
        this.buf.writeBoolean(b);
        return this;
    }

    @Override
    public OldDataOut writeByte(byte b) throws IOException {
        this.ensureOpen();
        this.buf.writeByte(b & 0xFF);
        return this;
    }

    @Override
    public OldDataOut writeShort(short s) throws IOException {
        this.ensureOpen();
        this.buf.writeShort(s & 0xFFFF);
        return this;
    }

    @Override
    public OldDataOut writeMedium(int m) throws IOException {
        this.ensureOpen();
        this.buf.writeMedium(m & 0xFFFFFF);
        return this;
    }

    @Override
    public OldDataOut writeUMedium(int m) throws IOException {
        return this.writeMedium(m & 0xFFFFFF);
    }

    @Override
    public OldDataOut writeInt(int i) throws IOException {
        this.ensureOpen();
        this.buf.writeInt(i);
        return this;
    }

    @Override
    public OldDataOut writeLong(long l) throws IOException {
        this.ensureOpen();
        this.buf.writeLong(l);
        return this;
    }

    @Override
    public OldDataOut writeFloat(float f) throws IOException {
        this.ensureOpen();
        this.buf.writeFloat(f);
        return this;
    }

    @Override
    public OldDataOut writeDouble(double d) throws IOException {
        this.ensureOpen();
        this.buf.writeDouble(d);
        return this;
    }

    @Override
    public final void close() throws IOException {
        try {
            this.ensureOpen();
            if (this.handleClose(this.buf)) {
                this.buf.release();
            }
        } finally {
            this.buf = null;
        }
    }

    /**
     * Called when this stream is closed.
     *
     * @param buf the buffer that this stream was writing to
     * @return whether or not the buffer should be released
     * @throws IOException if an IO exception occurs you dummy
     */
    protected abstract boolean handleClose(@NonNull ByteBuf buf) throws IOException;

    protected final void ensureOpen() {
        if (this.buf == null) {
            throw new IllegalStateException("Already closed!");
        }
    }

    /**
     * A basic implementation of {@link NettyByteBufOut} that simply does nothing when closed.
     */
    static class Default extends NettyByteBufOut {
        public Default(ByteBuf buf) {
            super(buf);
        }

        @Override
        protected boolean handleClose(@NonNull ByteBuf buf) throws IOException {
            return false;
        }
    }
}
