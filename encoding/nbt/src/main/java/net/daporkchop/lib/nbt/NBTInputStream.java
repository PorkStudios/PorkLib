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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.nbt.alloc.DefaultNBTArrayAllocator;
import net.daporkchop.lib.nbt.alloc.NBTArrayAllocator;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Allows reading NBT tags from an {@link InputStream}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class NBTInputStream extends DataIn {
    protected final DataIn in;
    protected final TagRegistry defaultRegistry;

    @Getter
    protected final NBTArrayAllocator alloc;

    public NBTInputStream(@NonNull InputStream in) throws IOException {
        this(in, TagRegistry.NOTCHIAN, DefaultNBTArrayAllocator.INSTANCE);
    }

    public NBTInputStream(@NonNull InputStream in, @NonNull TagRegistry registry) throws IOException {
        this(in, registry, DefaultNBTArrayAllocator.INSTANCE);
    }

    public NBTInputStream(@NonNull InputStream in, @NonNull NBTArrayAllocator alloc) throws IOException {
        this(in, TagRegistry.NOTCHIAN, alloc);
    }

    public NBTInputStream(@NonNull InputStream in, @NonNull TagRegistry registry, @NonNull NBTArrayAllocator alloc) throws IOException {
        this.in = DataIn.wrap(in);
        this.defaultRegistry = registry;
        this.alloc = alloc;
    }

    public CompoundTag readTag() throws IOException {
        return this.readTag(this.defaultRegistry);
    }

    public synchronized CompoundTag readTag(@NonNull TagRegistry registry) throws IOException {
        byte id = this.readByte();
        if (registry.getId(CompoundTag.class) != id) {
            throw new IllegalStateException("Invalid id for compound tag!");
        }
        byte[] b = new byte[this.readUShort()];
        this.readFully(b, 0, b.length);
        CompoundTag tag = new CompoundTag(new String(b, StandardCharsets.UTF_8));
        b = null;
        tag.read(this, registry);
        return tag;
    }

    //inpustream implementations

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }
    
    //datain implementations
    @Override
    public boolean readBoolean() throws IOException {
        return this.in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.in.readByte();
    }

    @Override
    public int readUByte() throws IOException {
        return this.in.readUByte();
    }

    @Override
    public short readShort() throws IOException {
        return this.in.readShort();
    }

    @Override
    public int readUShort() throws IOException {
        return this.in.readUShort();
    }

    @Override
    public short readShortLE() throws IOException {
        return this.in.readShortLE();
    }

    @Override
    public int readUShortLE() throws IOException {
        return this.in.readUShortLE();
    }

    @Override
    public char readChar() throws IOException {
        return this.in.readChar();
    }

    @Override
    public char readCharLE() throws IOException {
        return this.in.readCharLE();
    }

    @Override
    public int readInt() throws IOException {
        return this.in.readInt();
    }

    @Override
    public long readUInt() throws IOException {
        return this.in.readUInt();
    }

    @Override
    public int readIntLE() throws IOException {
        return this.in.readIntLE();
    }

    @Override
    public long readUIntLE() throws IOException {
        return this.in.readUIntLE();
    }

    @Override
    public long readLong() throws IOException {
        return this.in.readLong();
    }

    @Override
    public long readLongLE() throws IOException {
        return this.in.readLongLE();
    }

    @Override
    public float readFloat() throws IOException {
        return this.in.readFloat();
    }

    @Override
    public float readFloatLE() throws IOException {
        return this.in.readFloatLE();
    }

    @Override
    public double readDouble() throws IOException {
        return this.in.readDouble();
    }

    @Override
    public double readDoubleLE() throws IOException {
        return this.in.readDoubleLE();
    }

    @Override
    public String readUTF() throws IOException {
        return this.in.readUTF();
    }

    @Override
    public byte[] readByteArray() throws IOException {
        return this.in.readByteArray();
    }

    @Override
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws IOException {
        return this.in.readEnum(f);
    }

    @Override
    public int readVarInt() throws IOException {
        return this.in.readVarInt();
    }

    @Override
    public long readVarLong() throws IOException {
        return this.in.readVarLong();
    }

    @Override
    public byte[] readFully(@NonNull byte[] b) throws IOException {
        return this.in.readFully(b);
    }

    @Override
    public byte[] readFully(@NonNull byte[] b, int off, int len) throws IOException {
        return this.in.readFully(b, off, len);
    }

    @Override
    public byte[] readAllAvailableBytes() throws IOException {
        return this.in.readAllAvailableBytes();
    }

    @Override
    public InputStream unwrap() {
        return this.in.unwrap();
    }
}
