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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.collections.iterator.PIterators;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.Iterator;

/**
 * A group of multiple {@link Registry} instances identified by their ID.
 *
 * All implementations must provide at least {@code minecraft:block} and {@code minecraft:item}.
 *
 * @author DaPorkchop_
 */
public interface Registries extends Iterable<Registry> {
    Identifier BLOCK = Identifier.fromString("minecraft:block");
    Identifier ITEM = Identifier.fromString("minecraft:item");

    /**
     * @return the number of registries in this instance
     */
    int size();

    /**
     * Checks whether or not this instance contains a {@link Registry} with the given ID.
     *
     * @param id the ID of the registry to check for
     * @return whether or not this instance contains a {@link Registry} with the given ID
     */
    boolean has(@NonNull Identifier id);

    /**
     * Gets the {@link Registry} with the given ID.
     *
     * @param id the ID of the registry to get
     * @return the {@link Registry} with the given ID
     * @throws IllegalArgumentException if no {@link Registry} with the given ID could be found
     */
    Registry get(@NonNull Identifier id);

    /**
     * @return the {@link Registry} for {@code minecraft:block}
     */
    default Registry block()    {
        return this.get(BLOCK);
    }

    /**
     * @return the {@link Registry} for {@code minecraft:item}
     */
    default Registry item() {
        return this.get(ITEM);
    }
}
