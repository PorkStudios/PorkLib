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
 * @author DaPorkchop_
 */
public interface Registries extends Iterable<Registry> {
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
     * Representation of an empty {@link Registries} instance.
     *
     * @author DaPorkchop_
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Empty implements Registries {
        public static final Empty INSTANCE = new Empty();

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean has(@NonNull Identifier id) {
            return false;
        }

        @Override
        public Registry get(@NonNull Identifier id) {
            throw new IllegalArgumentException(PStrings.fastFormat("Unknown ID: %s", id));
        }

        @Override
        public Iterator<Registry> iterator() {
            return PIterators.empty();
        }
    }
}
