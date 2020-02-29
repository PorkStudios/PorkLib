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

package net.daporkchop.lib.common.function.io;

import net.daporkchop.lib.common.function.plain.TriFunction;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Function} that can throw an {@link IOException}
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface IOTriFunction<T, U, V, R> extends TriFunction<T, U, V, R> {
    @Override
    default R apply(T t, U u, V v) {
        try {
            return this.applyThrowing(t, u, v);
        } catch (IOException e) {
            PUnsafe.throwException(e);
            throw new RuntimeException(e);
        }
    }

    R applyThrowing(T t, U u, V v) throws IOException;
}
