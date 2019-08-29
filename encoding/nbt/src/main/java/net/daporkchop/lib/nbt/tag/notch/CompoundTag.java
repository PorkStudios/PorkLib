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

package net.daporkchop.lib.nbt.tag.notch;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.OldDataIn;
import net.daporkchop.lib.binary.stream.OldDataOut;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A tag that contains any number of tags required any type, all indexed by their name
 *
 * @author DaPorkchop_
 */
@Getter
public class CompoundTag extends Tag {
    private final Map<String, Tag> contents = new HashMap<>();

    public CompoundTag() {
        this("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    @Override
    public void read(@NonNull OldDataIn in, @NonNull TagRegistry registry) throws IOException {
        byte id;
        while ((id = in.readByte()) != 0) {
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
    public void write(@NonNull OldDataOut out, @NonNull TagRegistry registry) throws IOException {
        for (Map.Entry<String, Tag> entry : this.contents.entrySet()) {
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

    /**
     * Get a tag
     *
     * @param name the name of the tag
     * @param <T>  optional parameter to automagically cast to the desired tag class
     * @return the tag with the given name, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag> T get(@NonNull String name) {
        return (T) this.contents.get(name);
    }

    /**
     * Adds a tag
     *
     * @param tag the tag to add
     */
    public void put(@NonNull Tag tag) {
        this.put(tag.getName() == null ? "" : tag.getName(), tag);
    }

    /**
     * Adds a tag
     *
     * @param name the name of the tag
     * @param tag  the tag to add
     */
    public void put(@NonNull String name, @NonNull Tag tag) {
        this.contents.put(name, tag);
    }

    /**
     * Checks if this compound tag contains a tag with the given name
     *
     * @param name the name to check for
     * @return whether or not this compound tag contains a tag with the given name
     */
    public boolean contains(@NonNull String name) {
        return this.contents.containsKey(name);
    }

    /**
     * Removes a tag
     *
     * @param name the name of the tag to remove
     */
    public void remove(@NonNull String name) {
        this.contents.remove(name);
    }

    /**
     * Iterates over all contained tags
     *
     * @param consumer the function to run on the tags
     * @param <T>      optional parameter, allows for automagical casting to the desired tag sublcass
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag> void forEach(@NonNull BiConsumer<String, T> consumer) {
        ((Map<String, T>) this.contents).forEach(consumer);
    }

    /**
     * Iterates over all contained tags
     *
     * @param consumer the function to run on the tags
     * @param <T>      optional parameter, allows for automagical casting to the desired tag sublcass
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag> void forEach(@NonNull Consumer<T> consumer) {
        ((Map<String, T>) this.contents).values().forEach(consumer);
    }

    /**
     * Removes all tags
     */
    public void clear() {
        this.contents.clear();
    }

    /**
     * Puts a byte (8-bit) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putByte(@NonNull String name, byte val) {
        this.put(name, new ByteTag(name, val));
    }

    /**
     * Puts a short (16-bit) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putShort(@NonNull String name, short val) {
        this.put(name, new ShortTag(name, val));
    }

    /**
     * Puts an int (32-bit) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putInt(@NonNull String name, int val) {
        this.put(name, new IntTag(name, val));
    }

    /**
     * Puts a long (64-bit) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putLong(@NonNull String name, long val) {
        this.put(name, new LongTag(name, val));
    }

    /**
     * Puts a float (32-bit floating-point) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putFloat(@NonNull String name, float val) {
        this.put(name, new FloatTag(name, val));
    }

    /**
     * Puts a double (64-bit floating-point) value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putDouble(@NonNull String name, double val) {
        this.put(name, new DoubleTag(name, val));
    }

    /**
     * Puts a byte[] value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putByteArray(@NonNull String name, @NonNull byte[] val) {
        this.put(name, new ByteArrayTag(name, val));
    }

    /**
     * Puts an int[] value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putIntArray(@NonNull String name, @NonNull int[] val) {
        this.put(name, new IntArrayTag(name, val));
    }

    /**
     * Puts a long[] value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putLongArray(@NonNull String name, @NonNull long[] val) {
        this.put(name, new LongArrayTag(name, val));
    }

    /**
     * Puts a {@link String} value
     *
     * @param name the name of the value
     * @param val  the value
     */
    public void putString(@NonNull String name, @NonNull String val) {
        this.put(name, new StringTag(name, val));
    }

    /**
     * Puts a {@link CompoundTag} value
     *
     * @param val the value
     */
    public void putCompound(@NonNull CompoundTag val) {
        this.put(val.getName(), val);
    }

    /**
     * Puts a {@link List} value
     *
     * @param name the name of the value
     * @param list the list
     */
    public <T extends Tag> void putList(@NonNull String name, @NonNull List<T> list) {
        this.put(name, new ListTag<>(name, list));
    }

    /**
     * Gets a byte (8-bit) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public byte getByte(@NonNull String name, byte def) {
        ByteTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a short (16-bit) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public short getShort(@NonNull String name, short def) {
        ShortTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets an int (32-bit) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public int getInt(@NonNull String name, int def) {
        IntTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a long (64-bit) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public long getLong(@NonNull String name, long def) {
        LongTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a float (32-bit floating-point) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public float getFloat(@NonNull String name, float def) {
        FloatTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a double (64-bit floating-point) value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public double getDouble(@NonNull String name, double def) {
        DoubleTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a byte[] value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public byte[] getByteArray(@NonNull String name, byte[] def) {
        ByteArrayTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets an int[] value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public int[] getIntArray(@NonNull String name, int[] def) {
        IntArrayTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a long[] value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public long[] getLongArray(@NonNull String name, long[] def) {
        LongArrayTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a {@link String} value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public String getString(@NonNull String name, String def) {
        StringTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a {@link CompoundTag} value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public CompoundTag getCompound(@NonNull String name, CompoundTag def) {
        CompoundTag tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag;
        }
    }

    /**
     * Gets a {@link List} value, or returns a default value if not present
     *
     * @param name the name of the value to get
     * @param def  the default value to return if no tag could be found with the given name
     * @return the value that was found, or def if no tag could be found with the given name
     */
    public <T extends Tag> List<T> getList(@NonNull String name, List<T> def) {
        ListTag<T> tag = this.get(name);
        if (tag == null) {
            return def;
        } else {
            return tag.getValue();
        }
    }

    /**
     * Gets a byte (8-bit) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public byte getByte(@NonNull String name) {
        return this.getByte(name, (byte) 0);
    }

    /**
     * Gets a short (16-bit) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public short getShort(@NonNull String name) {
        return this.getShort(name, (short) 0);
    }

    /**
     * Gets an int (32-bit) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public int getInt(@NonNull String name) {
        return this.getInt(name, 0);
    }

    /**
     * Gets a long (64-bit) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public long getLong(@NonNull String name) {
        return this.getLong(name, 0L);
    }

    /**
     * Gets a float (32-bit floating-point) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public float getFloat(@NonNull String name) {
        return this.getFloat(name, 0.0f);
    }

    /**
     * Gets a double (64-bit floating-point) value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public double getDouble(@NonNull String name) {
        return this.getDouble(name, 0.0d);
    }

    /**
     * Gets a byte[] value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public byte[] getByteArray(@NonNull String name) {
        return this.getByteArray(name, null);
    }

    /**
     * Gets an int[] value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public int[] getIntArray(@NonNull String name) {
        return this.getIntArray(name, null);
    }

    /**
     * Gets a long[] value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public long[] getLongArray(@NonNull String name) {
        return this.getLongArray(name, null);
    }

    /**
     * Gets a {@link String} value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public String getString(@NonNull String name) {
        return this.getString(name, null);
    }

    /**
     * Gets a {@link CompoundTag} value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public CompoundTag getCompound(@NonNull String name) {
        return this.getCompound(name, null);
    }

    /**
     * Gets a {@link List} value
     *
     * @param name the name of the value
     * @return the value with the given name
     */
    public <T extends Tag> List<T> getList(@NonNull String name) {
        return this.getList(name, null);
    }
}
