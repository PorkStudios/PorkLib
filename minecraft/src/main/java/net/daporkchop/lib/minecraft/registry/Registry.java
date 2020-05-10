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
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * A Minecraft registry, which serves as a mapping between numeric IDs and {@link Identifier}s.
 *
 * @author DaPorkchop_
 */
public interface Registry extends Iterable<Identifier> {
    /**
     * @return this registry's id
     */
    Identifier id();

    /**
     * @return the number of mappings in this {@link Registry}
     */
    int size();

    /**
     * Checks whether or not this {@link Registry} contains the given {@link Identifier}.
     *
     * @param identifier the {@link Identifier} to check for
     * @return whether or not this {@link Registry} contains the given {@link Identifier}
     */
    boolean contains(@NonNull Identifier identifier);

    /**
     * Gets the numeric ID mapped to the given {@link Identifier}.
     *
     * @param identifier the {@link Identifier} to get the numeric ID for
     * @return the numeric ID mapped to the given {@link Identifier}
     * @throws IllegalArgumentException if the given {@link Identifier} is not registered
     */
    int get(@NonNull Identifier identifier);

    /**
     * Gets the numeric ID mapped to the given {@link Identifier}.
     *
     * @param identifier the {@link Identifier} to get the numeric ID for
     * @param fallback   the {@code int} to return if the given {@link Identifier} is not registered
     * @return the numeric ID mapped to the given {@link Identifier}
     */
    int get(@NonNull Identifier identifier, int fallback);

    /**
     * Checks whether or not this {@link Registry} contains the given numeric ID.
     *
     * @param id the numeric ID to check for
     * @return whether or not this {@link Registry} contains the given numeric ID
     */
    boolean contains(int id);

    /**
     * Gets the {@link Identifier} mapped to the given numeric ID.
     *
     * @param id the numeric ID to get the {@link Identifier} for
     * @return the {@link Identifier} mapped to the given numeric ID
     * @throws IllegalArgumentException if the given numeric ID is not registered
     */
    Identifier get(int id);

    /**
     * Gets the {@link Identifier} mapped to the given numeric ID.
     *
     * @param id       the numeric ID to get the {@link Identifier} for
     * @param fallback the {@link Identifier} to return if the given numeric ID is not registered
     * @return the {@link Identifier} mapped to the given numeric ID
     */
    Identifier get(int id, Identifier fallback);

    @Override
    Iterator<Identifier> iterator();

    @Override
    void forEach(@NonNull Consumer<? super Identifier> action);

    void forEach(@NonNull ObjIntConsumer<Identifier> action);
}
