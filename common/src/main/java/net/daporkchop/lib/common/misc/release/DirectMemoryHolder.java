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

import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

/**
 * An object that holds a reference to a direct memory block, as allocated by {@link sun.misc.Unsafe#allocateMemory(long)}.
 *
 * @author DaPorkchop_
 */
@Deprecated
public interface DirectMemoryHolder extends Releasable {
    /**
     * Releases the memory block referenced by this instance. After invoking this, assume that
     * the behavior of all other methods in the class is undefined unless specifically stated otherwise.
     *
     * @throws AlreadyReleasedException if the memory was already released
     */
    @Override
    void release() throws AlreadyReleasedException;

    /**
     * An abstract implementation of {@link DirectMemoryHolder} which handles the basic behaviors
     * of cleaners, etc. automagically.
     * <p>
     * The memory block may not be resized (i.e. with {@link PUnsafe#reallocateMemory(long, long)}).
     */
    abstract class AbstractConstantSize implements DirectMemoryHolder {
        protected final long pos;
        protected final long size;
        protected final PCleaner cleaner;

        public AbstractConstantSize(long size) {
            this.pos = PUnsafe.allocateMemory(this.size = size);
            this.cleaner = PCleaner.cleaner(this, this.pos);
        }

        @Override
        public void release() throws AlreadyReleasedException {
            if (!this.cleaner.clean())   {
                throw new AlreadyReleasedException();
            }
        }
    }
}
