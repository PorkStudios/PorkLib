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

package net.daporkchop.lib.graphics.bitmap.impl;

import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.capability.AccessibleDirectMemoryHolder;
import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base implementation of {@link net.daporkchop.lib.graphics.bitmap.Bitmap} backed by direct memory.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirectBitmap extends AbstractBitmap implements AccessibleDirectMemoryHolder {
    protected final long ptr;

    protected final PCleaner cleaner;

    public AbstractDirectBitmap(int width, int height) {
        super(width, height);

        this.cleaner = PCleaner.cleaner(this, this.ptr = this.memorySize());
    }

    @Override
    public final long memoryAddress() throws AlreadyReleasedException {
        if (this.cleaner.isCleaned())   {
            throw new AlreadyReleasedException();
        } else {
            return this.ptr;
        }
    }

    @Override
    public final long memorySize() {
        return (long) this.width * (long) this.height;
    }

    @Override
    public final void release() throws AlreadyReleasedException {
        //TODO
    }
}
