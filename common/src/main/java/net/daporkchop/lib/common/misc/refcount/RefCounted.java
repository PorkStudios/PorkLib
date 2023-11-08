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

package net.daporkchop.lib.common.misc.refcount;

import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

/**
 * A type that has a reference count which can be atomically incremented and decremented, and will be released
 * once the reference count reaches 0.
 * <p>
 * The reference count of a newly created instance is always 1.
 * <p>
 * Invoking {@link AutoCloseable#close()} on a {@link RefCounted} has the same effect as {@link #release()}.
 *
 * @author DaPorkchop_
 */
public interface RefCounted extends AutoCloseable {
    /**
     * @return the current reference count
     */
    int refCnt();

    /**
     * Retains this instance by incrementing the reference count.
     *
     * @throws AlreadyReleasedException if this instance's reference count has already reached 0
     */
    RefCounted retain() throws AlreadyReleasedException;

    /**
     * Releases this instance by decrementing the reference count.
     *
     * @return whether or not the reference count reached 0 and instance was released
     * @throws AlreadyReleasedException if this instance's reference count has already reached 0
     */
    boolean release() throws AlreadyReleasedException;

    @Override
    default void close() {
        this.release();
    }
}
