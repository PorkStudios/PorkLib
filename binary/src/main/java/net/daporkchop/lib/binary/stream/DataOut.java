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
import net.daporkchop.lib.binary.stream.data.BufferOut;
import net.daporkchop.lib.binary.stream.data.NonClosingStreamOut;
import net.daporkchop.lib.binary.stream.data.StreamOut;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
public abstract class DataOut extends OutputStream {
    public static DataOut wrap(OutputStream out) {
        return out instanceof DataOut ? (DataOut) out : new StreamOut(out);
    }

    public static DataOut wrapNonClosing(OutputStream out) {
        return out instanceof NonClosingStreamOut ? (NonClosingStreamOut) out : new NonClosingStreamOut(out);
    }

    public static DataOut wrap(ByteBuffer buffer) {
        return new BufferOut(buffer);
    }

    public static DataOut wrap(@NonNull File file) throws IOException {
        return wrapBuffered(file);
    }

    public static DataOut wrapBuffered(@NonNull File file) throws IOException {
        return wrap(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public static DataOut wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrap(new BufferedOutputStream(new FileOutputStream(file), bufferSize));
    }

    public static DataOut wrapNonBuffered(@NonNull File file) throws IOException {
        return wrap(new FileOutputStream(file));
    }

    /**
     * Writes a boolean
     *
     * @param b the boolean to write
     */
    public void writeBoolean(boolean b) throws IOException {
        this.write(b ? 1 : 0);
    }

    /**
     * Writes a byte (8-bit) value
     *
     * @param b the byte to write
     */
    public void writeByte(byte b) throws IOException {
        this.write(b & 0xFF);
    }

    /**
     * Writes a short (16-bit) value
     *
     * @param s the short to write
     */
    public void writeShort(short s) throws IOException {
        this.write((s >>> 8) & 0xFF);
        this.write(s & 0xFF);
    }

    /**
     * Writes an medium (24-bit) value
     *
     * @param m the medium to write
     */
    public void writeMedium(int m) throws IOException {
        this.write((m >>> 16) & 0xFF);
        this.write((m >>> 8) & 0xFF);
        this.write(m & 0xFF);
    }

    /**
     * Writes an int (32-bit) value
     *
     * @param i the int to write
     */
    public void writeInt(int i) throws IOException {
        this.write((i >>> 24) & 0xFF);
        this.write((i >>> 16) & 0xFF);
        this.write((i >>> 8) & 0xFF);
        this.write(i & 0xFF);
    }

    /**
     * Writes a long (64-bit) value
     *
     * @param l the long to write
     */
    public void writeLong(long l) throws IOException {
        this.write((int) (l >>> 56) & 0xFF);
        this.write((int) (l >>> 48) & 0xFF);
        this.write((int) (l >>> 40) & 0xFF);
        this.write((int) (l >>> 32) & 0xFF);
        this.write((int) (l >>> 24) & 0xFF);
        this.write((int) (l >>> 16) & 0xFF);
        this.write((int) (l >>> 8) & 0xFF);
        this.write((int) l & 0xFF);
    }

    /**
     * Writes a float (32-bit floating point) value
     *
     * @param f the float to write
     */
    public void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a double (64-bit floating point) value
     *
     * @param d the double to write
     */
    public void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a UTF-8 encoded string, including a null header and the length in bytes encoded as a varInt
     *
     * @param s the string to write
     */
    public void writeUTF(String s) throws IOException {
        if (s == null) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            this.writeBytesSimple(s.getBytes(UTF8.utf8));
        }
    }

    /**
     * Writes a plain byte array with a length prefix encoded as a varInt
     *
     * @param b the bytes to write
     */
    public void writeBytesSimple(@NonNull byte[] b) throws IOException {
        this.writeVarInt(b.length, true);
        this.write(b);
    }

    /**
     * Writes an enum value
     *
     * @param e   the value to write
     * @param <E> the type of the enum
     */
    public <E extends Enum<E>> void writeEnum(E e) throws IOException {
        if (e == null) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            this.writeUTF(e.name());
        }
    }

    public void writeVarInt(int i) throws IOException {
        this.writeVarInt(i, false);
    }

    public void writeVarInt(int i, boolean optimizePositive) throws IOException {
        if (!optimizePositive) {
            i = (i << 1) ^ (i >> 31);
        }
        if (i == 0) {
            this.write(0);
            return;
        }
        int next = 0;
        while (i != 0) {
            next = i & 0x7F;
            i >>>= 7;
            this.write(next | (i == 0 ? 0 : 0x80));
        }
    }

    public void writeVarLong(long l) throws IOException {
        this.writeVarLong(l, false);
    }

    public void writeVarLong(long l, boolean optimizePositive) throws IOException {
        if (!optimizePositive) {
            l = (l << 1L) ^ (l >> 63L);
        }
        if (l == 0L) {
            this.write(0);
            return;
        }
        long next = 0L;
        while (l != 0) {
            next = l & 0x7FL;
            l >>>= 7L;
            this.write((int) (next | (l == 0L ? 0L : 0x80L)));
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            this.write(b[i + off] & 0xFF);
        }
    }

    @Override
    public abstract void close() throws IOException;

}
