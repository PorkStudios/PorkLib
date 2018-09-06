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

package net.daporkchop.lib.nbt.tag.impl.notch;

import net.daporkchop.lib.nbt.TagType;
import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.impl.pork.DoubleArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.FloatArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.LongArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.ShortArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.StringArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.object.ObjectTag;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class CompoundTag extends Tag<Map<String, Tag>> implements Cloneable {
    public CompoundTag() {
        this("");
    }

    public CompoundTag(String name) {
        super(name, new Hashtable<>());
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        for (Tag tag : getValue().values()) {
            Tag.writeNamedTag(tag, dos);
        }
        dos.writeByte(0); //TAG_END
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        getValue().clear();
        Tag tag;
        while ((tag = Tag.readNamedTag(dis)).getType() != TagType.TAG_END) {
            getValue().put(tag.getName(), tag);
        }
    }

    public boolean hasTags() {
        return getValue().isEmpty();
    }

    public Tag getTag(String name) {
        return getValue().get(name);
    }

    public boolean containsTag(String name) {
        return getValue().containsKey(name);
    }

    public CompoundTag removeTag(String name) {
        getValue().remove(name);
        return this;
    }

    //BEGIN NOTCHIAN TAGS
    public CompoundTag putTag(String name, Tag tag) {
        getValue().put(name, tag.setName(name));
        return this;
    }

    public CompoundTag putByte(String name, byte value) {
        return putTag(name, new ByteTag(name, value));
    }

    public byte getByte(String name) {
        if (!containsTag(name)) return (byte) 0;
        return ((ByteTag) getTag(name)).getValue();
    }

    public CompoundTag putShort(String name, short value) {
        return putTag(name, new ShortTag(name, value));
    }

    public short getShort(String name) {
        if (!containsTag(name)) return 0;
        return ((ShortTag) getTag(name)).getValue();
    }

    public CompoundTag putInt(String name, int value) {
        return putTag(name, new IntTag(name, value));
    }

    public int getInt(String name) {
        if (!containsTag(name)) return 0;
        return ((IntTag) getTag(name)).getValue();
    }

    public CompoundTag putLong(String name, long value) {
        return putTag(name, new LongTag(name, value));
    }

    public long getLong(String name) {
        if (!containsTag(name)) return 0L;
        return ((LongTag) getTag(name)).getValue();
    }

    public CompoundTag putFloat(String name, float value) {
        return putTag(name, new FloatTag(name, value));
    }

    public float getFloat(String name) {
        if (!containsTag(name)) return 0F;
        return ((FloatTag) getTag(name)).getValue();
    }

    public CompoundTag putDouble(String name, double value) {
        return putTag(name, new DoubleTag(name, value));
    }

    public double getDouble(String name) {
        if (!containsTag(name)) return 0D;
        return ((DoubleTag) getTag(name)).getValue();
    }

    public CompoundTag putString(String name, String value) {
        return putTag(name, new StringTag(name, value));
    }

    public String getString(String name) {
        if (!containsTag(name)) return "";
        return ((StringTag) getTag(name)).getValue();
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        return putTag(name, new ByteArrayTag(name, value));
    }

    public byte[] getByteArray(String name) {
        if (!containsTag(name)) return new byte[0];
        return ((ByteArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putIntArray(String name, int[] value) {
        return putTag(name, new IntArrayTag(name, value));
    }

    public int[] getIntArray(String name) {
        if (!containsTag(name)) return new int[0];
        return ((IntArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putList(ListTag<? extends Tag> list) {
        return putTag(list.getName(), list);
    }

    @SuppressWarnings("unchecked")
    public ListTag<? extends Tag> getList(String name) {
        if (!containsTag(name)) return new ListTag<>(name);
        return (ListTag<? extends Tag>) getTag(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getTypedList(String name) {
        if (containsTag(name)) {
            return (ListTag<T>) getTag(name);
        }
        return new ListTag<>(name);
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        return putTag(name, value);
    }

    public CompoundTag getCompound(String name) {
        if (!containsTag(name)) return new CompoundTag(name);
        return (CompoundTag) getTag(name);
    }

    public CompoundTag putBoolean(String string, boolean val) {
        return putByte(string, val ? (byte) 1 : (byte) 0);
    }

    public boolean getBoolean(String name) {
        return getByte(name) != 0;
    }
    //END NOTCHIAN TAGS

    //BEGIN PORKAIN TAGS
    public CompoundTag putShortArray(String name, short[] arr) {
        return putTag(name, new ShortArrayTag(name, arr));
    }

    public short[] getShortArray(String name) {
        if (!containsTag(name)) return new short[0];
        return ((ShortArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putLongArray(String name, long[] arr) {
        return putTag(name, new LongArrayTag(name, arr));
    }

    public long[] getLongArray(String name) {
        if (!containsTag(name)) return new long[0];
        return ((LongArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putFloatArray(String name, float[] arr) {
        return putTag(name, new FloatArrayTag(name, arr));
    }

    public float[] getFloatArray(String name) {
        if (!containsTag(name)) return new float[0];
        return ((FloatArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putDoubleArray(String name, double[] arr) {
        return putTag(name, new DoubleArrayTag(name, arr));
    }

    public double[] getDoubleArray(String name) {
        if (!containsTag(name)) return new double[0];
        return ((DoubleArrayTag) getTag(name)).getValue();
    }

    public CompoundTag putStringArray(String name, String[] arr) {
        return putTag(name, new StringArrayTag(name, arr));
    }

    public String[] getStringArray(String name) {
        if (!containsTag(name)) return new String[0];
        return ((StringArrayTag) getTag(name)).getValue();
    }

    public <T> CompoundTag putObject(String name, T obj) {
        return putTag(name, new ObjectTag<>(name, obj));
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String name) {
        if (!containsTag(name)) return null;
        return ((ObjectTag<T>) getTag(name)).getValue();
    }
    //END PORKIAN TAGS

    @Override
    public TagType getType() {
        return TagType.TAG_COMPOUND;
    }

    public String toString() {
        return "CompoundTag " + this.getName() + " (" + getValue().size() + " entries)";
    }

    @Override
    public CompoundTag copy() {
        CompoundTag nbt = new CompoundTag();
        getValue().forEach((key, value) -> nbt.putTag(key, value.copy()));
        return nbt;
    }
}
