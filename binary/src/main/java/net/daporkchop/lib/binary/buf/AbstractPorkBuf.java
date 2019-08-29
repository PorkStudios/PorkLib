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

package net.daporkchop.lib.binary.buf;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractPorkBuf extends ReferenceCountedPorkBuf {
    protected long capacity;
    protected long maxCapacity;
    protected long readerIndex;
    protected long writerIndex;

    @Override
    public PorkBuf capacity(long capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least 0!");
        } else if (capacity > this.maxCapacity) {
            throw new IllegalArgumentException("Capacity must be less than maxCapacity!");
        } else if (capacity < this.capacity) {
            throw new IllegalArgumentException("Capacity must be greater than maximum capacity!");
        } else if (capacity != this.capacity) {
            this.doExpand(this.capacity, capacity);
            this.capacity = capacity;
        }
        return this;
    }

    protected abstract void doExpand(long oldCapacity, long newCapacity);

    @Override
    public PorkBuf readerIndex(long readerIndex) {
        if (readerIndex < 0)    {
            throw new IllegalArgumentException("Reader index must be at least 0!");
        } else if (readerIndex > this.capacity)  {
            throw new IllegalArgumentException("Reader index must be less than or equal to capacity!");
        } else {
            this.readerIndex = readerIndex;
        }
        return this;
    }

    @Override
    public PorkBuf writerIndex(long writerIndex) {
        if (writerIndex < 0)    {
            throw new IllegalArgumentException("Writer index must be at least 0!");
        } else if (writerIndex > this.capacity)  {
            throw new IllegalArgumentException("Writer index must be less than or equal to capacity!");
        } else {
            this.writerIndex = writerIndex;
        }
        return this;
    }

    protected void assertInCapacity(long index, long cnt) {
        if (index < 0 || cnt < 0 || index + cnt > this.capacity)    {
            throw new IllegalArgumentException(String.format(
                    "Index out of bounds (pos=%d,count=%d,capacity=%d)",
                    index,
                    cnt,
                    this.capacity
            ));
        }
    }

    protected void ensureWriteable(long cnt) {
        //TODO
    }
}
