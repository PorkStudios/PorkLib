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

package net.daporkchop.lib.collections.list;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.list.immutable.ImmutableListOne;
import net.daporkchop.lib.collections.list.immutable.ImmutableListTwo;

import java.util.List;

/**
 * Helper methods for {@link List}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PLists {
    /**
     * Gets an immutable {@link List} containing the given value.
     *
     * @param v0  the value
     * @param <T> the value type
     * @return an immutable {@link List} containing the given value
     */
    public static <T> List<T> immutable(T v0) {
        return ImmutableListOne.of(v0);
    }

    /**
     * Gets an immutable {@link List} containing the two given values.
     *
     * @param v0  the value at index 0
     * @param v1  the value at index 1
     * @param <T> the value type
     * @return an immutable {@link List} containing the two given values
     */
    public static <T> List<T> immutable(T v0, T v1) {
        return ImmutableListTwo.of(v0, v1);
    }
}
