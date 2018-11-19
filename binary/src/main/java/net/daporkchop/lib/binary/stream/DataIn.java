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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public abstract class DataIn extends InputStream {
    public static DataIn wrap(InputStream in) {
        return new StreamIn(in);
    }

    public static DataIn wrap(ByteBuffer buffer) {
        return new BufferIn(buffer);
    }

    public static DataIn wrap(@NonNull File file) throws IOException {
        return wrap(new FileInputStream(file));
    }

    /**
     * Read a boolean
     *
     * @return a boolean
     */
    public boolean readBoolean() throws IOException {
        return this.read() == 1;
    }

    /**
     * Read a byte
     *
     * @return a byte
     */
    public byte readByte() throws IOException {
        return (byte) this.read();
    }

    /**
     * Read a short
     *
     * @return a short
     */
    public short readShort() throws IOException {
        return (short) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Read an int
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
     * Read a long
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
     * Read a float
     *
     * @return a float
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Read a double
     *
     * @return a double
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Read a UTF-8 encoded string
     *
     * @return a string
     */
    public String readUTF() throws IOException {
        return this.readBoolean() ? new String(this.readBytesSimple(), UTF8.utf8) : null;
    }

    /**
     * Reads a plain byte array
     *
     * @return a byte array
     */
    public byte[] readBytesSimple() throws IOException {
        int len = this.readVarInt(true);
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte) this.read();
        }
        return b;
    }

    /**
     * Reads an enum value
     *
     * @param f   a function to calculate the enum value from the name (i.e. MyEnum::valueOf)
     * @param <E> the enum type
     * @return a value of <E>, or null if input was null
     */
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws IOException {
        if (this.readBoolean()) {
            return f.apply(this.readUTF());
        } else {
            return null;
        }
    }

    public int readVarInt() throws IOException {
        return this.readVarInt(false);
    }

    public int readVarInt(boolean optimizePositive) throws IOException {
        int v = 0;
        int i;
        int o = 0;
        while (true) {
            i = this.read();
            v |= (i & 0x7F) << o;
            o += 7;
            if ((i & 0x80) == 0) {
                break;
            }
        }
        return optimizePositive ? v : ((v >>> 1) ^ -(v & 1));
    }

    public int readFully(byte[] b, int off, int len) throws IOException {
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                b[i + off] = (byte) this.read();
            }
            return len;
        } else {
            return 0;
        }
    }

    @Override
    public abstract void close() throws IOException;

    @AllArgsConstructor
    private static class StreamIn extends DataIn {
        @NonNull
        private final InputStream in;

        @Override
        public void close() throws IOException {
            this.in.close();
        }

        @Override
        public int read() throws IOException {
            return this.in.read();
        }

        @Override
        public int available() throws IOException {
            return this.in.available();
        }
    }

    @AllArgsConstructor
    private static class BufferIn extends DataIn {
        @NonNull
        private ByteBuffer buffer;

        public int read() throws IOException {
            if (this.buffer == null) {
                throw new IOException("read on a closed InputStream");
            } else {
                return this.buffer.remaining() == 0 ? -1 : this.buffer.get() & 255;
            }
        }

        public int read(byte[] var1) throws IOException {
            if (this.buffer == null) {
                throw new IOException("read on a closed InputStream");
            } else {
                return this.read(var1, 0, var1.length);
            }
        }

        public int read(byte[] var1, int var2, int var3) throws IOException {
            if (this.buffer == null) {
                throw new IOException("read on a closed InputStream");
            } else if (var1 == null) {
                throw new NullPointerException();
            } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
                if (var3 == 0) {
                    return 0;
                } else {
                    int var4 = Math.min(this.buffer.remaining(), var3);
                    if (var4 == 0) {
                        return -1;
                    } else {
                        this.buffer.get(var1, var2, var4);
                        return var4;
                    }
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public long skip(long var1) throws IOException {
            if (this.buffer == null) {
                throw new IOException("skip on a closed InputStream");
            } else if (var1 <= 0L) {
                return 0L;
            } else {
                int var3 = (int)var1;
                int var4 = Math.min(this.buffer.remaining(), var3);
                this.buffer.position(this.buffer.position() + var4);
                return (long)var3;
            }
        }

        public int available() throws IOException {
            if (this.buffer == null) {
                throw new IOException("available on a closed InputStream");
            } else {
                return this.buffer.remaining();
            }
        }

        public void close() throws IOException {
            this.buffer = null;
        }

        public synchronized void mark(int var1) {
        }

        public synchronized void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }

        public boolean markSupported() {
            return false;
        }
    }
}
