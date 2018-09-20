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

package net.daporkchop.lib.binary;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.UUID;

/**
 * A byte buffer that is good and also nice
 *
 * @author DaPorkchop_
 */
public class PorkBuf {
    private byte[] backing;
    @Getter
    private int readPos, writePos;

    @Getter
    @Setter
    private boolean expand;

    private PorkBuf(byte[] backing) {
        this.backing = backing;
    }

    /**
     * Creates a new byte buffer with a given size
     *
     * @param size the size of the byte buffer
     * @return a new byte buffer with the given size
     */
    public static PorkBuf allocate(int size) {
        return new PorkBuf(new byte[size]);
    }

    /**
     * Creates a new byte buffer wrapping the given bytes
     *
     * @param data the bytes to wrap
     * @return a new byte buffer wrapping the given bytes
     * @throws NullPointerException if the given byte array is null
     */
    public static PorkBuf wrap(@NonNull byte[] data) {
        return new PorkBuf(data);
    }

    /**
     * Writes a byte to the buffer
     *
     * @param b the byte to write
     */
    public void putByte(byte b) {
        this.put(b);
    }

    /**
     * Writes a short to the buffer
     *
     * @param s the short to write
     */
    public void putShort(short s) {
        this.put((byte) (s >>> 8),
                (byte) (s));
    }

    /**
     * Writes an int to the buffer
     *
     * @param i the int to write
     */
    public void putInt(int i) {
        this.put((byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) (i >>> 0));
    }

    /**
     * Writes a long to the buffer
     *
     * @param l the long to write
     */
    public void putLong(long l) {
        this.put((byte) (l >>> 56),
                (byte) (l >>> 48),
                (byte) (l >>> 40),
                (byte) (l >>> 32),
                (byte) (l >>> 24),
                (byte) (l >>> 16),
                (byte) (l >>> 8),
                (byte) (l >>> 0));
    }

    /**
     * Writes a float to the buffer
     *
     * @param f the float to write
     */
    public void putFloat(float f) {
        this.putInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a double to the buffer
     *
     * @param d the double to write
     */
    public void putDouble(double d) {
        this.putLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a UTF-8 encoded string to the buffer, along with a 4-byte length prefix
     *
     * @param s the string to write
     */
    public void putUTF(@NonNull String s) {
        this.putInt(s.length());
        this.put(s.getBytes(UTF8.utf8));
    }

    /**
     * Writes a UTF-8 encoded string to the buffer, with a somewhat compacted prefix
     *
     * @param s the string to write
     */
    public void putUTFCompact(@NonNull String s) {
        int l = s.length();
        if (l == 0) {
            this.put((byte) 0);
        } else if (l < 128) {
            this.put((byte) l);
            this.put(s.getBytes(UTF8.utf8));
        } else if (l <= 65536) {
            l -= 1;
            this.put((byte) (128 | (l >> 8)), (byte) (l & 0xFF));
            this.put(s.getBytes(UTF8.utf8));
        } else {
            throw new IllegalArgumentException("String too long! " + s);
        }
    }

    /**
     * Writes a UUID to the buffer
     *
     * @param uuid a UUID
     */
    public void putUUID(@NonNull UUID uuid) {
        this.putLong(uuid.getMostSignificantBits());
        this.putLong(uuid.getLeastSignificantBits());
    }

    /**
     * Writes a plain byte array to the buffer
     *
     * @param b the bytes to write
     */
    public void putBytes(@NonNull byte[] b) {
        this.put(b);
    }

    /**
     * Read a byte from the buffer
     *
     * @return a byte
     */
    public byte getByte() {
        return this.get(1)[0];
    }

    /**
     * Read a short from the buffer
     *
     * @return a short
     */
    public short getShort() {
        byte[] b = this.get(2);
        return (short) ((((short) b[0] & 0xFF) << 8)
                | ((short) b[1] & 0xFF));
    }

    /**
     * Read an int from the buffer
     *
     * @return an int
     */
    public int getInt() {
        byte[] b = this.get(4);
        return (((int) b[0] & 0xFF) << 24)
                | (((int) b[1] & 0xFF) << 16)
                | (((int) b[2] & 0xFF) << 8)
                | ((int) b[3] & 0xFF);
    }

    /**
     * Read a long from the buffer
     *
     * @return a long
     */
    public long getLong() {
        byte[] b = this.get(8);
        return (((long) b[0] & 0xFF) << 56L)
                | (((long) b[1] & 0xFF) << 48L)
                | (((long) b[2] & 0xFF) << 40L)
                | (((long) b[3] & 0xFF) << 32L)
                | (((long) b[4] & 0xFF) << 24L)
                | (((long) b[5] & 0xFF) << 16L)
                | (((long) b[6] & 0xFF) << 8L)
                | ((long) b[7] & 0xFF);
    }

    /**
     * Read a float from the buffer
     *
     * @return a float
     */
    public float getFloat() {
        return Float.intBitsToFloat(this.getInt());
    }

    /**
     * Read a double from the buffer
     *
     * @return a double
     */
    public double getDouble() {
        return Double.longBitsToDouble(this.getLong());
    }

    /**
     * Read a UTF-8 encoded string from the buffer
     *
     * @return a string
     */
    public String getUTF() {
        int len = this.getInt();
        return new String(this.get(len), UTF8.utf8);
    }

    /**
     * Reads a UTF-8 encoded string from the buffer, with a compacted prefix
     *
     * @return a string
     */
    public String getUTFCompact() {
        int i = this.getByte();
        if ((i >> 7) == 0) {
            return new String(this.get(i), UTF8.utf8);
        } else {
            i = ((i & 127) << 8) | (int) this.getByte();
            return new String(this.get(i), UTF8.utf8);
        }
    }

    /**
     * Reads a UUID from the buffer
     *
     * @return a UUID
     */
    public UUID getUUID() {
        return new UUID(this.getLong(), this.getLong());
    }

    /**
     * Reads a plain byte array from the buffer
     *
     * @param len the number of bytes to read
     * @return a byte array
     */
    public byte[] getBytes(int len) {
        if (len < 1) throw new IllegalArgumentException();
        return this.get(len);
    }

    /**
     * Gets the plain byte array that this buffer is working on.
     * This byte array will reflect changes done to the buffer, and vice-versa (unless
     * the buffer expands, in which case a new array is created).
     *
     * @return the buffer's byte array
     */
    public byte[] toArray() {
        return this.backing;
    }

    /**
     * Gets all bytes written to the buffer.
     * The returned byte array does not reflect changes made to the buffer.
     *
     * @return all bytes written to the buffer
     */
    public byte[] getWrittenBytes() {
        byte[] bytes = new byte[this.writePos];
        System.arraycopy(this.backing, 0, bytes, 0, this.writePos);
        return bytes;
    }

    public void resetRead() {
        this.readPos = 0;
    }

    public void seekRead(int pos) {
        this.readPos = pos;
    }

    public void resetWrite() {
        this.writePos = 0;
    }

    public void seekWrite(int pos) {
        this.writePos = pos;
    }

    public void reset() {
        this.resetRead();
        this.resetWrite();
    }

    public void seek(int pos) {
        this.seekRead(pos);
        this.seekWrite(pos);
    }

    private byte[] get(int len) {
        if (this.readPos + len > this.backing.length) {
            throw new BufferUnderflowException();
        }
        byte[] b = new byte[len];
        System.arraycopy(this.backing, this.readPos, b, 0, len);
        this.readPos += len;
        return b;
    }

    private void put(byte... bytes) {
        if (this.writePos + bytes.length > this.backing.length) {
            if (this.expand) {
                int newLen = this.backing.length;
                do {
                    newLen <<= 1;
                } while (this.writePos + bytes.length > newLen);
                //expand array
                byte[] b = new byte[newLen];
                System.arraycopy(this.backing, 0, b, 0, this.writePos);
                this.backing = b;
            } else {
                throw new BufferOverflowException();
            }
        }
        System.arraycopy(bytes, 0, this.backing, this.writePos, bytes.length);
        this.writePos += bytes.length;
    }
}
