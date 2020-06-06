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

package net.daporkchop.lib.minecraft.block;

import lombok.NonNull;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A key used to define an attribute of a block state.
 *
 * @author DaPorkchop_
 */
public interface Property<V> extends Comparable<Property<?>> {
    /**
     * @return this property's name
     */
    String name();

    /**
     * @return a stream over all valid values for this property
     */
    Stream<V> values();

    /**
     * Creates a new {@link PropertyMap} populated with the return values of the given function.
     *
     * @param mappingFunction the function to use for computing the {@link BlockState}s for each value
     * @return a new {@link PropertyMap} populated with the return values of the given function
     */
    PropertyMap<V> propertyMap(@NonNull Function<V, BlockState> mappingFunction);

    @Override
    default int compareTo(Property<?> o) {
        return this.name().compareTo(o.name());
    }

    /**
     * Extension of {@link Property} for {@code int} values.
     *
     * @author DaPorkchop_
     */
    interface Int extends Property<Integer> {
        @Override
        @Deprecated
        Stream<Integer> values();

        /**
         * @return a stream over all valid values for this property
         */
        IntStream intValues();
    }

    /**
     * Extension of {@link Property} for {@code boolean} values.
     *
     * @author DaPorkchop_
     */
    interface Boolean extends Property<java.lang.Boolean> {
    }
}
