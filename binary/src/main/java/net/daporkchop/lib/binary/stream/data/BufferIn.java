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

package net.daporkchop.lib.binary.stream.data;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An implementation of {@link DataIn} that can read from a {@link ByteBuffer}
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class BufferIn extends DataIn {
    @NonNull
    private final ByteBuffer buffer;

    @Override
    public int read() throws IOException {
        return this.buffer.remaining() == 0 ? -1 : this.buffer.get() & 255;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        if (off >= 0 && len >= 0 && len <= b.length - off) {
            if (len == 0) {
                return 0;
            } else {
                int var4 = Math.min(this.buffer.remaining(), len);
                if (var4 == 0) {
                    return -1;
                } else {
                    this.buffer.get(b, off, var4);
                    return var4;
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public long skip(long var1) throws IOException {
        if (var1 <= 0L) {
            return 0L;
        } else {
            int var3 = (int) var1;
            int var4 = Math.min(this.buffer.remaining(), var3);
            this.buffer.position(this.buffer.position() + var4);
            return (long) var3;
        }
    }

    @Override
    public int available() throws IOException {
        return this.buffer.remaining();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public byte readByte() throws IOException {
        return this.buffer.get();
    }

    @Override
    public short readShort() throws IOException {
        return this.buffer.getShort();
    }

    @Override
    public int readInt() throws IOException {
        return this.buffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.buffer.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.buffer.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.buffer.getDouble();
    }

    @Override
    public int readFully(@NonNull byte[] b, int off, int len) throws IOException {
        this.buffer.get(b, off, len);
        return len;
    }
}
