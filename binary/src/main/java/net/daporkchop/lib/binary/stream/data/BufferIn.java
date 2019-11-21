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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of {@link DataIn} that can read from a {@link ByteBuffer}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class BufferIn extends DataIn {
    @NonNull
    private final ByteBuffer buffer;

    @Override
    public int read() throws IOException {
        return this.buffer.hasRemaining() ? this.buffer.get() & 0xFF : -1;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        if (this.buffer.hasRemaining()) {
            len = Math.min(len, this.buffer.remaining());
            this.buffer.get(b, off, len);
            return len;
        } else {
            return -1;
        }
    }

    @Override
    public byte[] readFully(@NonNull byte[] b, int off, int len) throws IOException {
        this.buffer.get(b, off, len);
        return b;
    }

    @Override
    public int available() throws IOException {
        return this.buffer.remaining();
    }

    @Override
    public long skip(long cnt) throws IOException {
        if (cnt <= 0L) {
            return 0L;
        } else {
            if (cnt > Integer.MAX_VALUE) {
                cnt = Integer.MAX_VALUE;
            }
            cnt = Math.min(this.buffer.remaining(), cnt);
            this.buffer.position(this.buffer.position() + (int) cnt);
            return cnt;
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.buffer.mark();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.buffer.reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public byte readByte() throws IOException {
        return this.buffer.get();
    }

    @Override
    public short readShort() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getShort();
    }

    @Override
    public short readShortLE() throws IOException {
        return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getInt();
    }

    @Override
    public int readIntLE() throws IOException {
        return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getLong();
    }

    @Override
    public long readLongLE() throws IOException {
        return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    @Override
    public float readFloatLE() throws IOException {
        return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.buffer.order(ByteOrder.BIG_ENDIAN).getDouble();
    }

    @Override
    public double readDoubleLE() throws IOException {
        return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    @Override
    public void close() throws IOException {
    }
}
