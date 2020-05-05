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

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.notch.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.notch.ByteTag;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.DoubleTag;
import net.daporkchop.lib.nbt.tag.notch.FloatTag;
import net.daporkchop.lib.nbt.tag.notch.IntArrayTag;
import net.daporkchop.lib.nbt.tag.notch.IntTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.nbt.tag.notch.LongArrayTag;
import net.daporkchop.lib.nbt.tag.notch.LongTag;
import net.daporkchop.lib.nbt.tag.notch.ShortTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;
import net.daporkchop.lib.nbt.tag.pork.BooleanTag;
import net.daporkchop.lib.nbt.tag.pork.CharArrayTag;
import net.daporkchop.lib.nbt.tag.pork.CharTag;
import net.daporkchop.lib.nbt.tag.pork.DoubleArrayTag;
import net.daporkchop.lib.nbt.tag.pork.FloatArrayTag;
import net.daporkchop.lib.nbt.tag.pork.ShortArrayTag;
import net.daporkchop.lib.primitive.map.ByteObjMap;
import net.daporkchop.lib.primitive.map.ObjByteMap;
import net.daporkchop.lib.primitive.map.hash.open.ByteObjOpenHashMap;
import net.daporkchop.lib.primitive.map.hash.open.ObjByteOpenHashMap;

import java.util.function.Function;

/**
 * Allows looking up a {@link Tag}'s id from it's class, and vice-versa
 *
 * @author DaPorkchop_
 */
public class TagRegistry {
    /**
     * The original NBT tag types, used from Minecraft beta 1.3 up to 1.11.2
     */
    public static final TagRegistry NOTCHIAN = new TagRegistry()
            .register(1, ByteTag.class, ByteTag::new)
            .register(2, ShortTag.class, ShortTag::new)
            .register(3, IntTag.class, IntTag::new)
            .register(4, LongTag.class, LongTag::new)
            .register(5, FloatTag.class, FloatTag::new)
            .register(6, DoubleTag.class, DoubleTag::new)
            .register(7, ByteArrayTag.class, ByteArrayTag::new)
            .register(8, StringTag.class, StringTag::new)
            .register(9, ListTag.class, ListTag::new)
            .register(10, CompoundTag.class, CompoundTag::new)
            .register(11, IntArrayTag.class, IntArrayTag::new)
            .register(12, LongArrayTag.class, LongArrayTag::new)
            .finish();

    /**
     * The notchian NBT tags, plus the {@code long[]} tag added in 1.12.2
     */
    public static final TagRegistry VANILLA = new TagRegistry()
            .registerAll(NOTCHIAN)
            .register(12, LongArrayTag.class, LongArrayTag::new)
            .finish();

    /**
     * DaPorkchop_'s custom tag types
     */
    public static final TagRegistry PORKIAN = new TagRegistry()
            .registerAll(NOTCHIAN)
            .register(64 | 0, ShortArrayTag.class, ShortArrayTag::new)
            .register(64 | 1, FloatArrayTag.class, FloatArrayTag::new)
            .register(64 | 2, DoubleArrayTag.class, DoubleArrayTag::new)
            .register(64 | 3, BooleanTag.class, BooleanTag::new)
            .register(64 | 4, CharTag.class, CharTag::new)
            .register(64 | 5, CharArrayTag.class, CharArrayTag::new)
            .finish();

    private final ByteObjMap<Function<String, ? extends Tag>> tagCreators = new ByteObjOpenHashMap<>();
    private final ObjByteMap<Class<? extends Tag>> tagIds = new ObjByteOpenHashMap<>();
    private volatile boolean finished = false;

    /**
     * Registers all ids from another registry
     *
     * @param otherRegistry the other registry
     * @return this registry
     */
    public synchronized TagRegistry registerAll(@NonNull TagRegistry otherRegistry) {
        if (this.finished) {
            throw new IllegalStateException("registry is finished!");
        } else {
            this.tagCreators.putAll(otherRegistry.tagCreators);
            this.tagIds.putAll(otherRegistry.tagIds);
        }
        return this;
    }

    /**
     * Register a new tag
     *
     * @param id      the tag's id
     * @param clazz   the class of the tag
     * @param creator a function to create new instances of the tag
     * @param <T>     the type of the tag
     * @return this registry
     */
    public <T extends Tag> TagRegistry register(int id, @NonNull Class<T> clazz, @NonNull Function<String, T> creator) {
        if ((id & 0xFF) != id) {
            throw new IllegalArgumentException(String.format("Id too large: %d", id));
        } else {
            return this.register((byte) id, clazz, creator);
        }
    }

    /**
     * Register a new tag
     *
     * @param id      the tag's id
     * @param clazz   the class of the tag
     * @param creator a function to create new instances of the tag
     * @param <T>     the type of the tag
     * @return this registry
     */
    public synchronized <T extends Tag> TagRegistry register(byte id, @NonNull Class<T> clazz, @NonNull Function<String, T> creator) {
        if (this.finished) {
            throw new IllegalStateException("registry is finished!");
        } else if (id == 0) {
            throw new IllegalArgumentException("Tag id 0 is reserved!");
        } else if (this.tagIds.containsKey(clazz)) {
            throw new IllegalStateException(String.format("Tag %s already registered!", clazz.getCanonicalName()));
        } else {
            this.tagCreators.put(id, creator);
            this.tagIds.put(clazz, id);
        }
        return this;
    }

    /**
     * Locks this registry to prevent any more writing
     *
     * @return this registry
     */
    public synchronized TagRegistry finish() {
        if (this.finished) {
            throw new IllegalStateException("registry is finished!");
        } else {
            this.finished = true;
        }
        return this;
    }

    /**
     * Gets the id of a given tag class
     *
     * @param clazz the class of the tag to get the id required
     * @return the id of the tag class
     */
    public byte getId(@NonNull Class<? extends Tag> clazz) {
        return this.tagIds.get(clazz);
    }

    /**
     * Create a new instance of a tag
     *
     * @param id   the tag id
     * @param name the name of the new tag
     * @param <T>  the type of tag to cast to
     * @return a newly created tag with the given name
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag> T create(byte id, String name) {
        Function<String, T> creator = (Function<String, T>) this.tagCreators.get(id);
        if (creator == null) {
            throw new IllegalStateException(String.format("No such tag: %d", id & 0xFF));
        }
        return creator.apply(name);
    }
}
