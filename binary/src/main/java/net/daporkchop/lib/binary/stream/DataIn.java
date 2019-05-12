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

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.data.BufferIn;
import net.daporkchop.lib.binary.stream.data.NonClosingStreamIn;
import net.daporkchop.lib.binary.stream.data.StreamIn;
import net.daporkchop.lib.binary.util.exception.EndOfStreamException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * Provides simple methods for reading data from a binary form
 *
 * @author DaPorkchop_
 * @see DataOut
 */
public abstract class DataIn extends InputStream {
    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link DataIn}
     */
    public static DataIn wrap(@NonNull InputStream in) {
        return in instanceof DataIn ? (DataIn) in : new StreamIn(in);
    }

    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}.
     * <p>
     * Calling {@link #close()} on the returned {@link DataIn} will not cause the wrapped stream to be closed.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link NonClosingStreamIn}
     */
    public static DataIn wrapNonClosing(@NonNull InputStream in) {
        return in instanceof NonClosingStreamIn ? (NonClosingStreamIn) in : new NonClosingStreamIn(in);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into a {@link DataIn}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as a {@link DataIn}
     */
    public static DataIn wrap(@NonNull ByteBuffer buffer) {
        return new BufferIn(buffer);
    }

    /**
     * @see #wrapBuffered(File)
     */
    public static DataIn wrap(@NonNull File file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the default buffer size of {@link BufferedInputStream#DEFAULT_BUFFER_SIZE}.
     *
     * @param file the file to read from
     * @return a buffered {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapBuffered(@NonNull File file) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the given buffer size.
     *
     * @param file       the file to read from
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file), bufferSize));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * {@link DataIn} instances returned from this method will NOT be buffered.
     *
     * @param file the file to read from
     * @return a direct {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapNonBuffered(@NonNull File file) throws IOException {
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
     * Read a byte (8-bit) value
     *
     * @return a byte
     */
    public byte readByte() throws IOException {
        return (byte) this.read();
    }

    /**
     * Read a short (16-bit) value
     *
     * @return a short
     */
    public short readShort() throws IOException {
        return (short) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Reads a medium (24-bit) value
     *
     * @return a medium
     */
    public int readMedium() throws IOException {
        return ((this.read() & 0xFF) << 16)
                | ((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF);
    }

    /**
     * Read an int (32-bit) value
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
     * Read a long (64-bit) value
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
     * Read a float (32-bit floating point) value
     *
     * @return a float
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Read a double (64-bit floating point) value
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
     * Reads a plain byte array with a length prefix encoded as a varInt
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

    /**
     * Reads a variable-length int, with optimization for negative numbers
     *
     * @return the number that was read
     * @throws EndOfStreamException if end of stream is reached before the number could be completely read
     * @see #readVarInt(boolean)
     */
    public int readVarInt() throws IOException {
        return this.readVarInt(false);
    }

    /**
     * Reads a variable-length int.
     * <p>
     * This reads the number in a form that is as compact as possible, using only the minimum required number of bytes to store
     * the value. However, very large numbers can take up to 5 bytes due to the headers required.
     *
     * @param optimizePositive if {@code true}, small negative numbers will be optimized at the expense of positives being one
     *                         bit longer. if {@code false}, no optimization will be applied, meaning small positives will use
     *                         a low number of bytes and all negatives will be 5 bytes long.
     * @return the number that was read
     * @throws EndOfStreamException if end of stream is reached before the number could be completely read
     */
    public int readVarInt(boolean optimizePositive) throws IOException {
        int v = 0;
        int i;
        int o = 0;
        do {
            i = this.read();
            if (i == -1) {
                throw new EndOfStreamException();
            } else {
                v |= (i & 0x7F) << o;
                o += 7;
            }
        } while ((i & 0x80) != 0 && o < 32);
        return optimizePositive ? v : ((v >>> 1) ^ -(v & 1));
    }

    /**
     * Reads a variable-length long, with optimization for negative numbers
     *
     * @return the number that was read
     * @throws EndOfStreamException if end of stream is reached before the number could be completely read
     * @see #readVarLong(boolean)
     */
    public long readVarLong() throws IOException {
        return this.readVarLong(false);
    }

    /**
     * Reads a variable-length long.
     * <p>
     * This reads the number in a form that is as compact as possible, using only the minimum required number of bytes to store
     * the value. However, very large numbers can take up to 9 bytes due to the headers required.
     *
     * @param optimizePositive if {@code true}, small negative numbers will be optimized at the expense of positives being one
     *                         bit longer. if {@code false}, no optimization will be applied, meaning small positives will use
     *                         a low number of bytes and all negatives will be 9 bytes long.
     * @return the number that was read
     * @throws EndOfStreamException if end of stream is reached before the number could be completely read
     */
    public long readVarLong(boolean optimizePositive) throws IOException {
        long v = 0L;
        int i;
        int o = 0;
        do {
            i = this.read();
            if (i == -1) {
                throw new EndOfStreamException();
            } else {
                v |= (i & 0x7FL) << o;
                o += 7;
            }
        } while ((i & 0x80) != 0 && o < 64);
        return optimizePositive ? v : ((v >>> 1L) ^ -(v & 1L));
    }

    /**
     * Attempts to fill a byte array with data.
     * <p>
     * Functionally equivalent to:
     * {@code return readFully(b, 0, b.length);}
     *
     * @param b the byte array to read into
     * @return the number of bytes read
     * @throws EndOfStreamException if end of stream is reached before the required number required bytes are read
     */
    public int readFully(@NonNull byte[] b) throws IOException {
        return b.length == 0 ? 0 : this.readFully(b, 0, b.length);
    }

    /**
     * Attempts to fill a given region of a byte array with data
     *
     * @param b   the byte array to read into
     * @param off the offset in the array to write data to
     * @param len the number of bytes to read
     * @return the number of bytes read
     * @throws EndOfStreamException if end of stream is reached before the required number required bytes are read
     */
    public int readFully(@NonNull byte[] b, int off, int len) throws IOException {
        if (len >= 0 && StreamUtil.read(this, b, off, len) != len) {
            throw new EndOfStreamException();
        }
        return len;
    }

    @Override
    public abstract void close() throws IOException;
}
