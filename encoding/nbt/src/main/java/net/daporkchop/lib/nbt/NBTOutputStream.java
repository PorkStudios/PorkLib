/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.nbt;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author DaPorkchop_
 */
public final class NBTOutputStream extends DataOut {
    private final DataOut out;
    private final TagRegistry defaultRegistry;

    public NBTOutputStream(@NonNull OutputStream out) throws IOException {
        this(out, TagRegistry.NOTCHIAN);
    }

    public NBTOutputStream(@NonNull OutputStream out, @NonNull TagRegistry registry) throws IOException {
        this.out = DataOut.wrap(out);
        this.defaultRegistry = registry;
    }

    public void writeTag(@NonNull CompoundTag tag) throws IOException {
        this.writeTag(tag, this.defaultRegistry);
    }

    public void writeTag(@NonNull CompoundTag tag, @NonNull TagRegistry registry) throws IOException {
        this.writeByte(registry.getId(CompoundTag.class));
        byte[] b = tag.getName().getBytes(StandardCharsets.UTF_8);
        this.writeShort((short) b.length);
        this.write(b);
        tag.write(this, registry);
    }

    //inputstream implementations
    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    //dataout implementations
    @Override
    public DataOut writeBoolean(boolean b) throws IOException {
        return this.out.writeBoolean(b);
    }

    @Override
    public DataOut writeByte(byte b) throws IOException {
        return this.out.writeByte(b);
    }

    @Override
    public DataOut writeUByte(int b) throws IOException {
        return this.out.writeUByte(b);
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        return this.out.writeShort(s);
    }

    @Override
    public DataOut writeUShort(int s) throws IOException {
        return this.out.writeUShort(s);
    }

    @Override
    public DataOut writeShortLE(short s) throws IOException {
        return this.out.writeShortLE(s);
    }

    @Override
    public DataOut writeUShortLE(int s) throws IOException {
        return this.out.writeUShortLE(s);
    }

    @Override
    public DataOut writeChar(char c) throws IOException {
        return this.out.writeChar(c);
    }

    @Override
    public DataOut writeCharLE(char c) throws IOException {
        return this.out.writeCharLE(c);
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        return this.out.writeInt(i);
    }

    @Override
    public DataOut writeUInt(long i) throws IOException {
        return this.out.writeUInt(i);
    }

    @Override
    public DataOut writeIntLE(int i) throws IOException {
        return this.out.writeIntLE(i);
    }

    @Override
    public DataOut writeUIntLE(long i) throws IOException {
        return this.out.writeUIntLE(i);
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        return this.out.writeLong(l);
    }

    @Override
    public DataOut writeLongLE(long l) throws IOException {
        return this.out.writeLongLE(l);
    }

    @Override
    public DataOut writeFloat(float f) throws IOException {
        return this.out.writeFloat(f);
    }

    @Override
    public DataOut writeFloatLE(float f) throws IOException {
        return this.out.writeFloatLE(f);
    }

    @Override
    public DataOut writeDouble(double d) throws IOException {
        return this.out.writeDouble(d);
    }

    @Override
    public DataOut writeDoubleLE(double d) throws IOException {
        return this.out.writeDoubleLE(d);
    }

    @Override
    public DataOut writeUTF(@NonNull String s) throws IOException {
        return this.out.writeUTF(s);
    }

    @Override
    public DataOut writeByteArray(@NonNull byte[] b) throws IOException {
        return this.out.writeByteArray(b);
    }

    @Override
    public <E extends Enum<E>> DataOut writeEnum(@NonNull E e) throws IOException {
        return this.out.writeEnum(e);
    }

    @Override
    public DataOut writeVarInt(int value) throws IOException {
        return this.out.writeVarInt(value);
    }

    @Override
    public DataOut writeVarLong(long value) throws IOException {
        return this.out.writeVarLong(value);
    }

    @Override
    public DataOut writeBytes(@NonNull byte[] b) throws IOException {
        return this.out.writeBytes(b);
    }

    @Override
    public DataOut writeBytes(@NonNull byte[] b, int off, int len) throws IOException {
        return this.out.writeBytes(b, off, len);
    }

    @Override
    public OutputStream unwrap() {
        return this.out.unwrap();
    }
}
