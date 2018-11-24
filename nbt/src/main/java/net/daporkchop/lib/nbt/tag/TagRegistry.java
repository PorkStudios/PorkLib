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

package net.daporkchop.lib.nbt.tag;

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;
import net.daporkchop.lib.primitive.map.ByteObjectMap;
import net.daporkchop.lib.primitive.map.ObjectByteMap;
import net.daporkchop.lib.primitive.map.hashmap.ByteObjectHashMap;
import net.daporkchop.lib.primitive.map.hashmap.ObjectByteHashMap;

import java.util.function.Function;

/**
 * Allows looking up a {@link Tag}'s id from it's class, and vice-versa
 *
 * @author DaPorkchop_
 */
public class TagRegistry {
    public static final TagRegistry NOTCHIAN = new TagRegistry()
            .register(8, StringTag.class, StringTag::new)
            .register(10, CompoundTag.class, CompoundTag::new)
            .finish();

    private final ByteObjectMap<Function<String, ? extends Tag>> tagCreators = new ByteObjectHashMap<>();
    private final ObjectByteMap<Class<? extends Tag>> tagIds = new ObjectByteHashMap<>();
    private volatile boolean finished = false;

    public <T extends Tag> TagRegistry register(int id, @NonNull Class<T> clazz, @NonNull Function<String, T> creator) {
        if ((id & 0xFF) != id) {
            throw new IllegalArgumentException(String.format("Id too large: %d", id));
        } else {
            return this.register((byte) id, clazz, creator);
        }
    }

    public synchronized <T extends Tag> TagRegistry register(byte id, @NonNull Class<T> clazz, @NonNull Function<String, T> creator) {
        if (this.finished) {
            throw new IllegalStateException("registry is finished!");
        } else if (this.tagCreators.containsKey(id)) {
            throw new IllegalStateException(String.format("Tag id %d already taken!", id & 0xFF));
        } else if (this.tagIds.containsKey(clazz)) {
            throw new IllegalStateException(String.format("Tag %s already registered!", clazz.getCanonicalName()));
        } else {
            this.tagCreators.put(id, creator);
            this.tagIds.put(clazz, id);
        }
        return this;
    }

    public synchronized TagRegistry finish() {
        if (this.finished) {
            throw new IllegalStateException("registry is finished!");
        } else {
            this.finished = true;
        }
        return this;
    }

    public byte getId(@NonNull Class<? extends Tag> clazz)  {
        return this.tagIds.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> T create(byte id, @NonNull String name)  {
        Function<String, T> creator = (Function<String, T>) this.tagCreators.get(id);
        return creator.apply(name);
    }
}
