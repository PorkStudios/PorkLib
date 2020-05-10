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

package net.daporkchop.lib.minecraft.registry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.primitive.lambda.consumer.ObjIntConsumer;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder class for {@link IDRegistry}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = true)
public class IDRegistryBuilder {
    @Getter
    @Setter
    protected Identifier name = null;

    protected ArrayList<Identifier> list = new ArrayList<>();

    /**
     * Whether or not the output registry should also create an indexed map of {@link Identifier}s to IDs for fast reverse ID lookups.
     * <p>
     * This will more than double memory consumption of the created {@link IDRegistry} instance, so it is recommended to only enable this if you
     * know you'll be needing it.
     */
    @Getter
    @Setter
    protected boolean map = true;

    /**
     * Registers a {@link Identifier} with the given ID.
     *
     * @param location the {@link Identifier} to register
     * @param id       the new ID of the {@link Identifier}
     * @return this {@link IDRegistryBuilder} instance
     * @throws IllegalArgumentException if the given ID is less than 0
     * @throws IllegalArgumentException if an identical {@link Identifier} is already registered with a different ID
     */
    public synchronized IDRegistryBuilder register(@NonNull Identifier location, int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID may not be less than 0!");
        }
        {
            int hashCode = location.hashCode();
            for (int i = 0, size = this.list.size(); i < size; i++) {
                if (i == id) {
                    continue;
                }
                Identifier other = this.list.get(i);
                if (other != null && other.hashCode() == hashCode && other.equals(location)) {
                    throw new IllegalArgumentException(String.format("Resource location \"%s\" is already registered with ID %d!", location, i));
                }
            }
        }

        while (this.list.size() <= id) {
            this.list.add(null);
        }
        this.list.set(id, location);
        return this;
    }

    /**
     * Registers all {@link Identifier}s in the given {@link IDRegistry} with their existing ID.
     *
     * @param registry the registry to copy from
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder registerAll(@NonNull IDRegistry registry) {
        registry.forEach(this::register);
        return this;
    }

    /**
     * Registers all {@link Identifier}s in the given {@link IDRegistryBuilder} with their existing ID.
     *
     * @param builder the builder to copy from
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder registerAll(@NonNull IDRegistryBuilder builder) {
        builder.forEach(this::register);
        return this;
    }

    /**
     * Registers all {@link Identifier}s in the given {@link Map} with their existing ID.
     *
     * @param map the map to copy from
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder registerAll(@NonNull Map<Identifier, Integer> map) {
        map.forEach(this::register);
        return this;
    }

    /**
     * Runs the given callback function for every {@link Identifier} in this builder.
     *
     * @param callback the callback function to run
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder forEach(@NonNull Consumer<Identifier> callback) {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            Identifier location = this.list.get(i);
            if (location != null) {
                callback.accept(location);
            }
        }
        return this;
    }

    /**
     * Runs the given callback function for every {@link Identifier} and ID in this builder.
     *
     * @param callback the callback function to run
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder forEach(@NonNull ObjIntConsumer<Identifier> callback) {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            Identifier location = this.list.get(i);
            if (location != null) {
                callback.accept(location, i);
            }
        }
        return this;
    }

    /**
     * Clears this {@link IDRegistryBuilder}, removing all {@link Identifier} to ID mappings.
     * <p>
     * After calling this method, this {@link IDRegistryBuilder} instance may be re-used to make another registry.
     *
     * @return this {@link IDRegistryBuilder} instance
     */
    public synchronized IDRegistryBuilder clear() {
        this.list.clear();
        return this;
    }

    /**
     * Builds a new {@link IDRegistry} using the current contents of this builder.
     *
     * @return a new {@link IDRegistry} with this builder's contents
     */
    public synchronized IDRegistry build() {
        Identifier[] arr = this.list.toArray(new Identifier[this.list.size()]);
        return this.map ? new MappedIDRegistryImpl(arr, this.name) : new IDRegistryImpl(arr, this.name);
    }
}
