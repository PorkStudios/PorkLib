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

import lombok.NonNull;

import java.util.function.IntFunction;

/**
 * A simple {@link ArrayAllocator} which doesn't do any pooling at all, but simply allocates the arrays as they're requested.
 *
 * @author DaPorkchop_
 */
final class UnpooledArrayAllocator<V> extends AbstractArrayAllocator<V> {
    public UnpooledArrayAllocator(@NonNull IntFunction<V> lambda) {
        super(lambda);
    }

    public UnpooledArrayAllocator(@NonNull Class<?> componentClass) {
        super(componentClass);
    }

    @Override
    public V atLeast(int length) {
        return this.exactly(length);
    }

    @Override
    public V exactly(int length) {
        return this.createArray(length);
    }

    @Override
    public void release(@NonNull V array) {
        //no-op
    }
}
