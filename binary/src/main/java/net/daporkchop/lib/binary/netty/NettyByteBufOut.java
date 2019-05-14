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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * An implementation of {@link DataOut} that can write to a {@link ByteBuf}
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class NettyByteBufOut extends DataOut {
    @NonNull
    protected final ByteBuf buf;

    static {
        NettyUtil.ensureNettyPresent();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void write(int b) throws IOException {
        this.buf.writeByte(b);
    }

    @Override
    public DataOut writeBoolean(boolean b) throws IOException {
        this.buf.writeBoolean(b);
        return this;
    }

    @Override
    public DataOut writeByte(byte b) throws IOException {
        this.buf.writeByte(b & 0xFF);
        return this;
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        this.buf.writeShort(s & 0xFFFF);
        return this;
    }

    @Override
    public DataOut writeMedium(int m) throws IOException {
        this.buf.writeMedium(m & 0xFFFFFF);
        return this;
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        this.buf.writeInt(i);
        return this;
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        this.buf.writeLong(l);
        return this;
    }

    @Override
    public DataOut writeFloat(float f) throws IOException {
        this.buf.writeFloat(f);
        return this;
    }

    @Override
    public DataOut writeDouble(double d) throws IOException {
        this.buf.writeDouble(d);
        return this;
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        this.buf.writeBytes(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        this.buf.writeBytes(b, off, len);
    }
}
