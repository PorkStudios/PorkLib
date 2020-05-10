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
 * A simple registry of numeric IDs to {@link Identifier}s.
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
    Identifier name();

    /**
     * @return the number of IDs in this registry
     */
    int size();

    /**
     * Runs the given callback function for every {@link Identifier} in this registry.
     *
     * @param callback the callback function
     */
    void forEach(@NonNull Consumer<Identifier> callback);

    /**
     * Runs the given callback function for every {@link Identifier} and ID in this registry.
     *
     * @param callback the callback function
     */
    void forEach(@NonNull ObjIntConsumer<Identifier> callback);

    /**
     * Finds the {@link Identifier} with the given ID in this registry.
     *
     * @param id the ID of the {@link Identifier} to get
     * @return the {@link Identifier} with the given ID, or {@code null} if none could be found
     */
    Identifier lookup(int id);

    /**
     * Finds the ID of the given {@link Identifier} in this registry.
     *
     * @param name the {@link Identifier} to get the ID for
     * @return the ID of the given {@link Identifier}, or {@code -1} if none could be found
     */
    int lookup(@NonNull Identifier name);
}
