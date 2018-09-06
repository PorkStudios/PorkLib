/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.binary.stream;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class DataIn extends InputStream {
    @NonNull
    private final InputStream stream;

    /**
     * Read a boolean from the buffer
     *
     * @return a boolean
     */
    public boolean readBoolean() throws IOException {
        return this.read() == 1;
    }

    /**
     * Read a byte from the buffer
     *
     * @return a byte
     */
    public byte readByte() throws IOException {
        return (byte) this.read();
    }

    /**
     * Read a short from the buffer
     *
     * @return a short
     */
    public short readShort() throws IOException {
        return (short) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Read an int from the buffer
     *
     * @return an int
     */
    public int readInt() throws IOException {
        return ((this.read() & 0xFF) << 24)
                | ((this.read() & 0xFF) << 16)
                | ((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF);
    }

    /**
     * Read a long from the buffer
     *
     * @return a long
     */
    public long readLong() throws IOException {
        return (((long) this.read() & 0xFF) << 56L)
                | (((long) this.read() & 0xFF) << 48L)
                | (((long) this.read() & 0xFF) << 40L)
                | (((long) this.read() & 0xFF) << 32L)
                | (((long) this.read() & 0xFF) << 24L)
                | (((long) this.read() & 0xFF) << 16L)
                | (((long) this.read() & 0xFF) << 8L)
                | ((long) this.read() & 0xFF);
    }

    /**
     * Read a float from the buffer
     *
     * @return a float
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Read a double from the buffer
     *
     * @return a double
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Read a UTF-8 encoded string from the buffer
     *
     * @return a string
     */
    public String readUTF() throws IOException {
        if (this.readBoolean()) {
            int len = this.readInt();
            byte[] b = new byte[len];
            StreamUtil.read(this.stream, b, 0, b.length);
            return new String(b, UTF8.utf8);
        } else {
            return null;
        }
    }

    /**
     * Reads a UUID from the buffer
     *
     * @return a UUID
     */
    public UUID readUUID() throws IOException {
        return new UUID(this.readLong(), this.readLong());
    }

    /**
     * Reads a plain byte array from the buffer
     *
     * @return a byte array
     */
    public byte[] readBytesSimple() throws IOException {
        int len = this.readInt();
        byte[] b = new byte[len];
        StreamUtil.read(this.stream, b, 0, b.length);
        return b;
    }

    @Override
    public int read() throws IOException {
        return this.stream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len != 0) {
            return this.stream.read(b, off, len);
        } else {
            return 0;
        }
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    @Override
    public int available() throws IOException {
        return this.stream.available();
    }
}
