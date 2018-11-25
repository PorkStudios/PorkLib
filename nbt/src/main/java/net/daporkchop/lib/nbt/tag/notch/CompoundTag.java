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
 */

package net.daporkchop.lib.nbt.tag.notch;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
public class CompoundTag extends Tag {
    private final Map<String, Tag> contents = new HashMap<>();

    public CompoundTag(String name) {
        super(name);
    }

    @Override
    public void read(@NonNull DataIn in, @NonNull TagRegistry registry) throws IOException {
        byte id;
        while ((id = in.readByte()) != 0)   {
            String name;
            {
                byte[] b = new byte[in.readShort() & 0xFFFF];
                in.readFully(b, 0, b.length);
                name = new String(b, UTF8.utf8);
            }
            Tag tag = registry.create(id, name);
            tag.read(in, registry);
            this.contents.put(name, tag);
        }
    }

    @Override
    public void write(@NonNull DataOut out, @NonNull TagRegistry registry) throws IOException {
        for (Map.Entry<String, Tag> entry : this.contents.entrySet())   {
            byte id = registry.getId(entry.getValue().getClass());
            out.writeByte(id);
            byte[] name = entry.getKey().getBytes(UTF8.utf8);
            out.writeShort((short) name.length);
            out.write(name);
            entry.getValue().write(out, registry);
        }
        out.writeByte((byte) 0);
    }

    @Override
    public String toString() {
        return String.format("CompoundTag(\"%s\"): %d children", this.getName(), this.contents.size());
    }

    //UTILITY METHODS
    @SuppressWarnings("unchecked")
    public <T extends Tag> T get(@NonNull String name)  {
        return (T) this.contents.get(name);
    }

    public void put(@NonNull String name, @NonNull Tag tag) {
        this.contents.put(name, tag);
    }

    public boolean contains(@NonNull String name)   {
        return this.contents.containsKey(name);
    }

    public void remove(@NonNull String name)     {
        this.contents.remove(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> void forEach(@NonNull BiConsumer<String, T> consumer)    {
        ((Map<String, T>) this.contents).forEach(consumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> void forEach(@NonNull Consumer<T> consumer)    {
        ((Map<String, T>) this.contents).values().forEach(consumer);
    }

    public void clear() {
        this.contents.clear();
    }

    public void putByte(@NonNull String name, byte val)   {
        this.put(name, new ByteTag(name, val));
    }

    public void putShort(@NonNull String name, short val)   {
        this.put(name, new ShortTag(name, val));
    }

    public void putInt(@NonNull String name, int val)   {
        this.put(name, new IntTag(name, val));
    }

    public void putLong(@NonNull String name, long val)   {
        this.put(name, new LongTag(name, val));
    }

    public void putFloat(@NonNull String name, float val)   {
        this.put(name, new FloatTag(name, val));
    }

    public void putDouble(@NonNull String name, double val)   {
        this.put(name, new DoubleTag(name, val));
    }

    public void putByteArray(@NonNull String name, @NonNull byte[] val)   {
        this.put(name, new ByteArrayTag(name, val));
    }

    public void putIntArray(@NonNull String name, @NonNull int[] val)   {
        this.put(name, new IntArrayTag(name, val));
    }

    public void putLongArray(@NonNull String name, @NonNull long[] val)   {
        this.put(name, new LongArrayTag(name, val));
    }

    public void putString(@NonNull String name, @NonNull String val)   {
        this.put(name, new StringTag(name, val));
    }

    public void putCompound(@NonNull CompoundTag val)   {
        this.put(val.getName(), val);
    }

    public <T extends Tag> void putList(@NonNull String name, @NonNull List<T> list)  {
        this.put(name, new ListTag<>(name, list));
    }

    public byte getByte(@NonNull String name, byte def) {
        ByteTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public short getShort(@NonNull String name, short def) {
        ShortTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public int getInt(@NonNull String name, int def) {
        IntTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public long getLong(@NonNull String name, long def) {
        LongTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public float getFloat(@NonNull String name, float def) {
        FloatTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public double getDouble(@NonNull String name, double def) {
        DoubleTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public byte[] getByteArray(@NonNull String name, byte[] def) {
        ByteArrayTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public int[] getIntArray(@NonNull String name, int[] def) {
        IntArrayTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public long[] getLongArray(@NonNull String name, long[] def) {
        LongArrayTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public String getString(@NonNull String name, String def) {
        StringTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public CompoundTag getCompound(@NonNull String name, CompoundTag def) {
        CompoundTag tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag;
        }
    }

    public <T extends Tag> List<T> getList(@NonNull String name, List<T> def) {
        ListTag<T> tag = this.get(name);
        if (tag == null)    {
            return def;
        } else {
            return tag.getValue();
        }
    }

    public byte getByte(@NonNull String name) {
        return this.getByte(name, (byte) 0);
    }

    public short getShort(@NonNull String name) {
        return this.getShort(name, (short) 0);
    }

    public int getInt(@NonNull String name) {
        return this.getInt(name, 0);
    }

    public long getLong(@NonNull String name) {
        return this.getLong(name, 0L);
    }

    public float getFloat(@NonNull String name) {
        return this.getFloat(name, 0.0f);
    }

    public double getDouble(@NonNull String name) {
        return this.getDouble(name, 0.0d);
    }

    public byte[] getByteArray(@NonNull String name) {
        return this.getByteArray(name, null);
    }

    public int[] getIntArray(@NonNull String name) {
        return this.getIntArray(name, null);
    }

    public long[] getLongArray(@NonNull String name) {
        return this.getLongArray(name, null);
    }

    public String getString(@NonNull String name) {
        return this.getString(name, null);
    }

    public CompoundTag getCompound(@NonNull String name) {
        return this.getCompound(name, null);
    }

    public <T extends Tag> List<T> getList(@NonNull String name) {
        return this.getList(name, null);
    }
}
