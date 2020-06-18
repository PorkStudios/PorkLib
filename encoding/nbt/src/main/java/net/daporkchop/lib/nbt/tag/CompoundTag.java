/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.nbt.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.collections.collectors.PCollectors;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Representation of an NBT compound tag.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class CompoundTag extends Tag<CompoundTag> implements Iterable<Map.Entry<String, Tag>> {
    protected final Map<String, Tag> map;
    @Getter
    protected final String name;

    public CompoundTag() {
        this.map = new LinkedHashMap<>();
        this.name = null;
    }

    public CompoundTag(@NonNull String name) {
        this.map = new LinkedHashMap<>();
        this.name = name;
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public CompoundTag(@NonNull DataIn in, @NonNull NBTOptions options, String selfName) throws IOException {
        this.map = new LinkedHashMap<>();
        this.name = selfName;

        while (true) {
            int id = in.readUnsignedByte();
            if (id == TAG_END) {
                break;
            }
            String name = options.internKeys() ? in.readUTF().intern() : in.readUTF();
            Tag tag = options.parser().read(in, options, id);
            if (options.allowDuplicates()) {
                this.map.put(name, tag);
            } else {
                checkState(this.map.putIfAbsent(name, tag) == null, "Duplicate tag name: \"%s\"", name);
            }
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        for (Map.Entry<String, Tag> entry : this.map.entrySet()) {
            out.writeByte(entry.getValue().id());
            out.writeUTF(entry.getKey());
            entry.getValue().write(out);
        }
        out.writeByte(TAG_END);
    }

    public int size() {
        return this.map.size();
    }

    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return this.map.entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, Tag>> action) {
        this.map.entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, Tag>> spliterator() {
        return this.map.entrySet().spliterator();
    }

    @Override
    public int id() {
        return TAG_COMPOUND;
    }

    @Override
    public String typeName() {
        return "Compound";
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompoundTag && this.map.equals(((CompoundTag) obj).map);
    }

    @Override
    public CompoundTag clone() {
        return new CompoundTag(this.map.entrySet().stream().collect(PCollectors.toLinkedHashMap(Map.Entry::getKey, e -> e.getValue().clone())), this.name);
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, PorkUtil.fallbackIfNull(name, this.name), index);
        builder.append(this.map.size()).append(" entries\n");
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("{\n");
        for (Map.Entry<String, Tag> entry : this.map.entrySet()) {
            entry.getValue().toString(builder, depth + 1, entry.getKey(), -1);
        }
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("}\n");
    }

    @Override
    public void release() throws AlreadyReleasedException {
        this.map.values().forEach(Tag::release);
        this.map.clear();
    }

    public boolean contains(@NonNull String name) {
        return this.map.containsKey(name);
    }

    public <T extends Tag<T>> void forEach(@NonNull BiConsumer<String, T> callback) {
        for (Map.Entry<String, Tag> entry : this.map.entrySet()) {
            callback.accept(entry.getKey(), uncheckedCast(entry.getValue()));
        }
    }

    //
    //
    // setters
    //
    //

    public CompoundTag putTag(@NonNull String name, @NonNull Tag<?> value) {
        this.map.put(name, value);
        return this;
    }

    public CompoundTag putBoolean(@NonNull String name, boolean value) {
        return this.putByte(name, value ? (byte) 1 : 0);
    }

    public CompoundTag putByte(@NonNull String name, byte value) {
        this.map.put(name, new ByteTag(value));
        return this;
    }

    public CompoundTag putShort(@NonNull String name, short value) {
        this.map.put(name, new ShortTag(value));
        return this;
    }

    public CompoundTag putInt(@NonNull String name, int value) {
        this.map.put(name, new IntTag(value));
        return this;
    }

    public CompoundTag putLong(@NonNull String name, long value) {
        this.map.put(name, new LongTag(value));
        return this;
    }

    public CompoundTag putFloat(@NonNull String name, float value) {
        this.map.put(name, new FloatTag(value));
        return this;
    }

    public CompoundTag putDouble(@NonNull String name, double value) {
        this.map.put(name, new DoubleTag(value));
        return this;
    }

    public CompoundTag putByteArray(@NonNull String name, @NonNull byte[] value) {
        this.map.put(name, new ByteArrayTag(value));
        return this;
    }

    public CompoundTag putString(@NonNull String name, @NonNull String value) {
        this.map.put(name, new StringTag(value));
        return this;
    }

    public CompoundTag putIntArray(@NonNull String name, @NonNull int[] value) {
        this.map.put(name, new IntArrayTag(value));
        return this;
    }

    public CompoundTag putLongArray(@NonNull String name, @NonNull long[] value) {
        this.map.put(name, new LongArrayTag(value));
        return this;
    }

    //
    //
    // getters
    //
    //

    public <T extends Tag<T>> T getTag(@NonNull String name) {
        @SuppressWarnings("unchecked")
        T tag = (T) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag;
    }

    public <T extends Tag<T>> T getTag(@NonNull String name, T fallback) {
        @SuppressWarnings("unchecked")
        T tag = (T) this.map.get(name);
        return tag != null ? tag : fallback;
    }

    public boolean getBoolean(@NonNull String name) {
        return this.getByte(name) != 0;
    }

    public boolean getBoolean(@NonNull String name, boolean fallback) {
        return this.getByte(name, fallback ? (byte) 1 : 0) != 0;
    }

    public byte getByte(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.byteValue();
    }

    public byte getByte(@NonNull String name, byte fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.byteValue() : fallback;
    }

    public short getShort(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.shortValue();
    }

    public short getShort(@NonNull String name, short fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.shortValue() : fallback;
    }

    public int getInt(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.intValue();
    }

    public int getInt(@NonNull String name, int fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.intValue() : fallback;
    }

    public long getLong(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.longValue();
    }

    public long getLong(@NonNull String name, long fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.longValue() : fallback;
    }

    public float getFloat(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.floatValue();
    }

    public float getFloat(@NonNull String name, float fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.floatValue() : fallback;
    }

    public double getDouble(@NonNull String name) {
        NumberTag tag = (NumberTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.doubleValue();
    }

    public double getDouble(@NonNull String name, double fallback) {
        NumberTag tag = (NumberTag) this.map.get(name);
        return tag != null ? tag.doubleValue() : fallback;
    }

    public byte[] getByteArray(@NonNull String name) {
        ByteArrayTag tag = (ByteArrayTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.value();
    }

    public byte[] getByteArray(@NonNull String name, byte[] fallback) {
        ByteArrayTag tag = (ByteArrayTag) this.map.get(name);
        return tag != null ? tag.value() : fallback;
    }

    public String getString(@NonNull String name) {
        StringTag tag = (StringTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.value();
    }

    public String getString(@NonNull String name, String fallback) {
        StringTag tag = (StringTag) this.map.get(name);
        return tag != null ? tag.value() : fallback;
    }

    public <T extends Tag<T>> ListTag<T> getList(@NonNull String name, @NonNull Class<T> type) {
        int component = Tag.CLASS_TO_ID.getOrDefault(type, 0);
        checkArg(component != 0, "Invalid component class: %s", component);
        @SuppressWarnings("unchecked")
        ListTag<T> tag = (ListTag<T>) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        checkArg(tag.list().isEmpty() || tag.component == component, "TAG_List(\"%s\") has invalid type id %d (expected: %d)", name, tag.component, component);
        return tag;
    }

    public <T extends Tag<T>> ListTag<T> getList(@NonNull String name, @NonNull Class<T> type, ListTag<T> fallback) {
        int component = Tag.CLASS_TO_ID.getOrDefault(type, 0);
        checkArg(component != 0, "Invalid component class: %s", component);
        @SuppressWarnings("unchecked")
        ListTag<T> tag = (ListTag<T>) this.map.get(name);
        if (tag != null) {
            checkArg(tag.list().isEmpty() || tag.component == component, "TAG_List(\"%s\") has invalid type id %d (expected: %d)", name, tag.component, component);
            return tag;
        } else {
            return fallback;
        }
    }

    public CompoundTag getCompound(@NonNull String name) {
        CompoundTag tag = (CompoundTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag;
    }

    public CompoundTag getCompound(@NonNull String name, CompoundTag fallback) {
        CompoundTag tag = (CompoundTag) this.map.get(name);
        return tag != null ? tag : fallback;
    }

    public int[] getIntArray(@NonNull String name) {
        IntArrayTag tag = (IntArrayTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.value();
    }

    public int[] getIntArray(@NonNull String name, int[] fallback) {
        IntArrayTag tag = (IntArrayTag) this.map.get(name);
        return tag != null ? tag.value() : fallback;
    }

    public long[] getLongArray(@NonNull String name) {
        LongArrayTag tag = (LongArrayTag) this.map.get(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag.value();
    }

    public long[] getLongArray(@NonNull String name, long[] fallback) {
        LongArrayTag tag = (LongArrayTag) this.map.get(name);
        return tag != null ? tag.value() : fallback;
    }

    public <T extends Tag<T>> T remove(@NonNull String name) {
        @SuppressWarnings("unchecked")
        T tag = (T) this.map.remove(name);
        checkArg(tag != null, "No tag with name \"%s\" found!", name);
        return tag;
    }
}
