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
import java.io.OutputStream;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class DataOut extends OutputStream {
    @NonNull
    private final OutputStream stream;

    /**
     * Writes a boolean to the buffer
     *
     * @param b the boolean to write
     */
    public void writeBoolean(boolean b) throws IOException {
        this.write(b ? 1 : 0);
    }

    /**
     * Writes a byte to the buffer
     *
     * @param b the byte to write
     */
    public void writeByte(byte b) throws IOException {
        this.write(b & 0xFF);
    }

    /**
     * Writes a short to the buffer
     *
     * @param s the short to write
     */
    public void writeShort(short s) throws IOException {
        this.write((s >>> 8) & 0xFF);
        this.write(s & 0xFF);
    }

    /**
     * Writes an int to the buffer
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
     * Writes a long to the buffer
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
     * Writes a float to the buffer
     *
     * @param f the float to write
     */
    public void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a double to the buffer
     *
     * @param d the double to write
     */
    public void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a UTF-8 encoded string to the buffer, along with a 4-byte length prefix
     *
     * @param s the string to write
     */
    public void writeUTF(String s) throws IOException {
        if (s == null) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            byte[] b = s.getBytes(UTF8.utf8);
            this.writeInt(b.length);
            this.write(b);
        }
    }

    /**
     * Writes a plain byte array to the buffer
     *
     * @param b the bytes to write
     */
    public void writeBytesSimple(@NonNull byte[] b) throws IOException {
        this.writeInt(b.length);
        this.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len != 0) {
            this.stream.write(b, off, len);
        }
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
