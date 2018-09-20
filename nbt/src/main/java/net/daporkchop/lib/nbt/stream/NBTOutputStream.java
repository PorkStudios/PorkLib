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

package net.daporkchop.lib.nbt.stream;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTOutputStream implements DataOutput, AutoCloseable {
    private final DataOutputStream stream;
    private final ByteOrder endianness;
    private final boolean network;

    public NBTOutputStream(OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness, boolean network) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    public boolean isNetwork() {
        return this.network;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.stream.write(bytes);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.stream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.stream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.stream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v);
        }
        this.stream.writeInt(v);

    }

    @Override
    public void writeLong(long v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Long.reverseBytes(v);
        }
        this.stream.writeLong(v);

    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.stream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeShort(bytes.length);

        this.stream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
