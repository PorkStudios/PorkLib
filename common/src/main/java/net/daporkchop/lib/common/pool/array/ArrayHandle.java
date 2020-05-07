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

package net.daporkchop.lib.common.pool.array;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Similar to a {@link Handle}, but intended for use in such a way as to allow returning a slice of an array rather than the whole thing.
 * <p>
 * Note that attempting to access the array outside of the given range is completely unsafe and almost guaranteed to break things in unpredictable
 * ways. Don't do that. Ever.
 *
 * @author DaPorkchop_
 */
public interface ArrayHandle<V> extends RefCounted {
    /**
     * @return the array owned by this handle
     */
    V get();

    /**
     * @return the first index in the array
     */
    int offset();

    /**
     * @return the number of elements in the array which may be safely accessed, starting at {@link #offset()}
     */
    int length();

    @Override
    ArrayHandle<V> retain() throws AlreadyReleasedException;
}
