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

package net.daporkchop.lib.binary.util.unsafe.block;

import lombok.Getter;
import lombok.ToString;
import net.daporkchop.lib.common.util.PUnsafe;

import static net.daporkchop.lib.common.util.PUnsafe.*;

/**
 * @author DaPorkchop_
 */
@ToString
public class DirectMemoryBlock implements MemoryBlock {
    protected final long address;
    protected final long size;
    @Getter
    protected volatile boolean freed = false;

    public DirectMemoryBlock(long size) {
        if (size <= 0L) {
            throw new IllegalArgumentException("Size must be at least 1!");
        }

        this.size = size;
        this.address = allocateMemory(size);
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public synchronized void free() {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else {
            this.freed = true;
            freeMemory(this.address);
        }
    }

    @Override
    public long memoryOffset() {
        return this.address;
    }

    @Override
    public Object refObj() {
        return null;
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        this.tryFree();
    }

    @Override
    public byte getByte(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getByte(this.address + index);
        }
    }

    @Override
    public short getShort(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 1L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getShort(this.address + index);
        }
    }

    @Override
    public int getInt(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 3L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getInt(this.address + index);
        }
    }

    @Override
    public long getLong(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 7L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getLong(this.address + index);
        }
    }

    @Override
    public float getFloat(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 3L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getFloat(this.address + index);
        }
    }

    @Override
    public double getDouble(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 7L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getDouble(this.address + index);
        }
    }

    @Override
    public char getChar(long index) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 1L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getChar(this.address + index);
        }
    }

    @Override
    public void setByte(long index, byte val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putByte(this.address + index, val);
        }
    }

    @Override
    public void setShort(long index, short val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 1L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putShort(this.address + index, val);
        }
    }

    @Override
    public void setInt(long index, int val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 3L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putInt(this.address + index, val);
        }
    }

    @Override
    public void setLong(long index, long val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 7L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putLong(this.address + index, val);
        }
    }

    @Override
    public void setFloat(long index, float val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 3L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putFloat(this.address + index, val);
        }
    }

    @Override
    public void setDouble(long index, double val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 7L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putDouble(this.address + index, val);
        }
    }

    @Override
    public void setChar(long index, char val) {
        if (this.freed) {
            throw new IllegalStateException("Already freed!");
        } else if (index < 0L || index + 1L >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            putChar(this.address + index, val);
        }
    }
}
