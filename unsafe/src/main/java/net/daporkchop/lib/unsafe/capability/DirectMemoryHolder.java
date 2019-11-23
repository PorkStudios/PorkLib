/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.unsafe.capability;

import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An object that holds a reference to a direct memory block, as allocated by {@link sun.misc.Unsafe#allocateMemory(long)}.
 *
 * @author DaPorkchop_
 */
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
            if (!this.cleaner.tryClean())   {
                throw new AlreadyReleasedException();
            }
        }
    }
}
