/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.common.misc.release;

import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

/**
 * A type that contains resources that may be manually released.
 *
 * @author DaPorkchop_
 */
public interface Releasable extends AutoCloseable {
    /**
     * Releases all resources used by this instance.
     * <p>
     * After invoking this method, this instance should be treated as invalid and one should assume that
     * using any fields/methods defined by superclasses will result in undefined behavior, unless the
     * superclass implementations specifically state otherwise.
     *
     * @throws AlreadyReleasedException if the resources used by this instance have already been released
     */
    void release() throws AlreadyReleasedException;

    @Override
    default void close() {
        this.release();
    }
}
