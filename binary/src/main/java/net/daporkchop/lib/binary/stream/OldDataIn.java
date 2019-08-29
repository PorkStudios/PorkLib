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
import net.daporkchop.lib.binary.stream.data.BufferIn;
import net.daporkchop.lib.binary.stream.data.StreamIn;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Provides simple methods for reading data from a binary form
 *
 * @author DaPorkchop_
 * @see OldDataOut
 */
public abstract class OldDataIn extends InputStream {
    /**
     * Wraps an {@link InputStream} to make it into a {@link OldDataIn}
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link OldDataIn}
     */
    public static OldDataIn wrap(@NonNull InputStream in) {
        return in instanceof OldDataIn ? (OldDataIn) in : new StreamIn(in, true);
    }

    /**
     * Wraps an {@link InputStream} to make it into a {@link OldDataIn}.
     * <p>
     * Calling {@link #close()} on the returned {@link OldDataIn} will not cause the wrapped stream to be closed.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link StreamIn}
     */
    public static OldDataIn wrapNonClosing(@NonNull InputStream in) {
        return in instanceof StreamIn ? ((StreamIn) in).close(false) : new StreamIn(in, false);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into a {@link OldDataIn}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as a {@link OldDataIn}
     */
    public static OldDataIn wrap(@NonNull ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return new StreamIn(new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining()), false);
        } else {
            return new BufferIn(buffer);
        }
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into an {@link InputStream}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as an {@link InputStream}
     */
    public static InputStream wrapAsStream(@NonNull ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            return new BufferIn(buffer);
        }
    }

    /**
     * @see #wrapBuffered(File)
     */
    public static OldDataIn wrap(@NonNull File file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link OldDataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the default buffer size of {@link BufferedInputStream#DEFAULT_BUFFER_SIZE}.
     *
     * @param file the file to read from
     * @return a buffered {@link OldDataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static OldDataIn wrapBuffered(@NonNull File file) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * Gets a {@link OldDataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the given buffer size.
     *
     * @param file       the file to read from
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link OldDataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static OldDataIn wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file), bufferSize));
    }

    /**
     * Gets a {@link OldDataIn} for reading from a {@link File}.
     * <p>
     * {@link OldDataIn} instances returned from this method will NOT be buffered.
     *
     * @param file the file to read from
     * @return a direct {@link OldDataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static OldDataIn wrapNonBuffered(@NonNull File file) throws IOException {
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
     * Read a byte (8-bit) value
     *
     * @return a byte
     */
    public int readUByte() throws IOException {
        return this.read() & 0xFF;
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
     * Read a short (16-bit) value
     *
     * @return a short
     */
    public int readUShort() throws IOException {
        return (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Read a char (16-bit) value
     *
     * @return a char
     */
    public char readChar() throws IOException {
        return (char) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Reads a medium (24-bit) value
     *
     * @return a medium
     */
    public int readMedium() throws IOException {
        int value = this.readUMedium();
        if ((value & 0x800000) != 0) {
            value |= 0xFF000000;
        }
        return value;
    }

    /**
     * Reads a medium (24-bit) value
     *
     * @return a medium
     */
    public int readUMedium() throws IOException {
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
     * Read an int (32-bit) value
     *
     * @return an int
     */
    public long readUInt() throws IOException {
        return (((long) this.read() & 0xFFL) << 24L)
                | (((long) this.read() & 0xFFL) << 16L)
                | (((long) this.read() & 0xFFL) << 8L)
                | ((long) this.read() & 0xFFL);
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
        return new String(this.readByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a plain byte array with a length prefix encoded as a varInt
     *
     * @return a byte array
     */
    public byte[] readByteArray() throws IOException {
        byte[] b = new byte[this.readVarInt()];
        this.readFully(b);
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
     * Reads a Mojang-style VarInt.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the read value
     */
    public int readVarInt() throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = this.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Reads a Mojang-style VarLong.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the read value
     */
    public long readVarLong() throws IOException {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = this.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Attempts to fill a byte array with data.
     * <p>
     * Functionally equivalent to:
     * {@code return readFully(b, 0, b.length);}
     *
     * @param b the byte array to read into
     * @return the {@code byte[]} that the data was read into
     * @throws IOException if end of stream is reached before the required number required bytes are read
     */
    public byte[] readFully(@NonNull byte[] b) throws IOException {
        return this.readFully(b, 0, b.length);
    }

    /**
     * Attempts to fill a given region of a byte array with data
     *
     * @param b   the byte array to read into
     * @param off the offset in the array to write data to
     * @param len the number of bytes to read
     * @return the {@code byte[]} that the data was read into
     * @throws IOException if end of stream is reached before the required number required bytes are read
     */
    public byte[] readFully(@NonNull byte[] b, int off, int len) throws IOException {
        int i = 0;
        while (len >= 0 && (i = this.read(b, off + i, len)) != -1) {
            len -= i;
        }
        if (i == -1) {
            throw new IOException("Reached end of stream!");
        }
        return b;
    }

    /**
     * Reads all available bytes from this stream, as returned by {@link #available()}.
     *
     * @return all available bytes from this stream
     */
    public byte[] readAllAvailableBytes() throws IOException {
        byte[] b = new byte[this.available()];
        this.readFully(b);
        return b;
    }

    @Override
    public abstract void close() throws IOException;
}
