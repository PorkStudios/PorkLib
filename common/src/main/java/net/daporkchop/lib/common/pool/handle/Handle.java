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

package net.daporkchop.lib.common.pool.handle;

/**
 * A handle for {@link HandledPool}.
 * <p>
 * Wraps a value and returns the value to the pool when closed.
 *
 * @author DaPorkchop_
 */
public interface Handle<V> extends AutoCloseable {
    /**
     * @return the value that this handle belongs to
     */
    V value();

    /**
     * Closes this handle, returning the value to the pool.
     * <p>
     * Note that handle instances may be re-used as well, so calling this method more than once may produce unexpected results instead of throwing an
     * exception.
     * <p>
     * If this method is never called, this handle and the associated value will never be returned to the pool, and will simply be garbage-collected
     * as normal.
     */
    @Override
    void close();

    /**
     * @return the {@link HandledPool} that this handle belongs to
     */
    HandledPool<V> pool();
}
