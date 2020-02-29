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

import lombok.NonNull;
import net.daporkchop.lib.primitive.lambda.consumer.ObjIntConsumer;

import java.util.function.Consumer;

/**
 * A simple registry of numeric IDs to {@link ResourceLocation}s.
 *
 * @author DaPorkchop_
 */
public interface IDRegistry {
    /**
     * @return a new {@link IDRegistryBuilder}
     */
    static IDRegistryBuilder builder() {
        return new IDRegistryBuilder();
    }

    /**
     * @return the name of this registry. May be {@code null}
     */
    ResourceLocation name();

    /**
     * @return the number of IDs in this registry
     */
    int size();

    /**
     * Runs the given callback function for every {@link ResourceLocation} in this registry.
     *
     * @param callback the callback function
     */
    void forEach(@NonNull Consumer<ResourceLocation> callback);

    /**
     * Runs the given callback function for every {@link ResourceLocation} and ID in this registry.
     *
     * @param callback the callback function
     */
    void forEach(@NonNull ObjIntConsumer<ResourceLocation> callback);

    /**
     * Finds the {@link ResourceLocation} with the given ID in this registry.
     *
     * @param id the ID of the {@link ResourceLocation} to get
     * @return the {@link ResourceLocation} with the given ID, or {@code null} if none could be found
     */
    ResourceLocation lookup(int id);

    /**
     * Finds the ID of the given {@link ResourceLocation} in this registry.
     *
     * @param name the {@link ResourceLocation} to get the ID for
     * @return the ID of the given {@link ResourceLocation}, or {@code -1} if none could be found
     */
    int lookup(@NonNull ResourceLocation name);
}
