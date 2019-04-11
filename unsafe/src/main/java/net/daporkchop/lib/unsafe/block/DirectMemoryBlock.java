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

package net.daporkchop.lib.unsafe.block;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
@ToString
public class DirectMemoryBlock implements MemoryBlock {
    protected final long address;
    protected final long size;
    
    protected final PCleaner cleaner;

    public DirectMemoryBlock(long size) {
        if (size <= 0L) {
            throw new IllegalArgumentException("Size must be at least 1!");
        }

        this.size = size;
        this.address = PUnsafe.allocateMemory(size);
        this.cleaner = PCleaner.cleaner(this, this.address);
    }

    @Override
    public long memoryAddress() {
        return this.address;
    }

    @Override
    public long memorySize() {
        return this.size;
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
    public void release() throws AlreadyReleasedException {
        synchronized (this.cleaner) {
            if (this.cleaner.isCleaned())   {
                throw new AlreadyReleasedException();
            }
            this.cleaner.clean();
        }
    }

    @Override
    public byte getByte(long index) {
        if (index < 0L || index > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getByte(this.address + index);
        }
    }

    @Override
    public short getShort(long index) {
        if (index < 0L || index + 1L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getShort(this.address + index);
        }
    }

    @Override
    public int getInt(long index) {
        if (index < 0L || index + 3L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getInt(this.address + index);
        }
    }

    @Override
    public long getLong(long index) {
        if (index < 0L || index + 7L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getLong(this.address + index);
        }
    }

    @Override
    public float getFloat(long index) {
        if (index < 0L || index + 3L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getFloat(this.address + index);
        }
    }

    @Override
    public double getDouble(long index) {
        if (index < 0L || index + 7L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getDouble(this.address + index);
        }
    }

    @Override
    public char getChar(long index) {
        if (index < 0L || index + 1L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            return PUnsafe.getChar(this.address + index);
        }
    }

    @Override
    public void getBytes(long index, @NonNull byte[] arr, int off, int len) {
        if (index < 0L || index + len > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + off, len);
        }
    }

    @Override
    public void getShorts(long index, @NonNull short[] arr, int off, int len) {
        if (index < 0L || index + (len << 1L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_SHORT_BASE_OFFSET + (off << 1L), len << 1L);
        }
    }

    @Override
    public void getInts(long index, @NonNull int[] arr, int off, int len) {
        if (index < 0L || index + (len << 2L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_INT_BASE_OFFSET + (off << 2L), len << 2L);
        }
    }

    @Override
    public void getLongs(long index, @NonNull long[] arr, int off, int len) {
        if (index < 0L || index + (len << 3L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_LONG_BASE_OFFSET + (off << 3L), len << 3L);
        }
    }

    @Override
    public void getFloats(long index, @NonNull float[] arr, int off, int len) {
        if (index < 0L || index + (len << 2L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_FLOAT_BASE_OFFSET + (off << 2L), len << 2L);
        }
    }

    @Override
    public void getDoubles(long index, @NonNull double[] arr, int off, int len) {
        if (index < 0L || index + (len << 3L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_DOUBLE_BASE_OFFSET + (off << 3L), len << 3L);
        }
    }

    @Override
    public void getChars(long index, @NonNull char[] arr, int off, int len) {
        if (index < 0L || index + (len << 1L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(null, this.address, arr, PUnsafe.ARRAY_CHAR_BASE_OFFSET + (off << 1L), len << 1L);
        }
    }

    @Override
    public void setByte(long index, byte val) {
        if (index < 0L || index > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putByte(this.address + index, val);
        }
    }

    @Override
    public void setShort(long index, short val) {
        if (index < 0L || index + 1L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putShort(this.address + index, val);
        }
    }

    @Override
    public void setInt(long index, int val) {
        if (index < 0L || index + 3L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putInt(this.address + index, val);
        }
    }

    @Override
    public void setLong(long index, long val) {
        if (index < 0L || index + 7L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putLong(this.address + index, val);
        }
    }

    @Override
    public void setFloat(long index, float val) {
        if (index < 0L || index + 3L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putFloat(this.address + index, val);
        }
    }

    @Override
    public void setDouble(long index, double val) {
        if (index < 0L || index + 7L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putDouble(this.address + index, val);
        }
    }

    @Override
    public void setChar(long index, char val) {
        if (index < 0L || index + 1L > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else {
            PUnsafe.putChar(this.address + index, val);
        }
    }

    @Override
    public void setBytes(long index, @NonNull byte[] arr, int off, int len) {
        if (index < 0L || index + len > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + off, null, this.address, len);
        }
    }

    @Override
    public void setShorts(long index, @NonNull short[] arr, int off, int len) {
        if (index < 0L || index + (len << 1L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_SHORT_BASE_OFFSET + (off << 1L), null, this.address, len << 1L);
        }
    }

    @Override
    public void setInts(long index, @NonNull int[] arr, int off, int len) {
        if (index < 0L || index + (len << 2L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_INT_BASE_OFFSET + (off << 2L), null, this.address, len << 2L);
        }
    }

    @Override
    public void setLongs(long index, @NonNull long[] arr, int off, int len) {
        if (index < 0L || index + (len << 3L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_LONG_BASE_OFFSET + (off << 3L), null, this.address, len << 3L);
        }
    }

    @Override
    public void setFloats(long index, @NonNull float[] arr, int off, int len) {
        if (index < 0L || index + (len << 2L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_FLOAT_BASE_OFFSET + (off << 2L), null, this.address, len << 2L);
        }
    }

    @Override
    public void setDoubles(long index, @NonNull double[] arr, int off, int len) {
        if (index < 0L || index + (len << 3L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_DOUBLE_BASE_OFFSET + (off << 3L), null, this.address, len << 3L);
        }
    }

    @Override
    public void setChars(long index, @NonNull char[] arr, int off, int len) {
        if (index < 0L || index + (len << 1L) > this.size) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal index: %d (must be in range 0-%d)", index, this.size));
        } else if (off < 0 || off + len > arr.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Illegal offset/length: off=%d,length=%d for array length %d", off, len, arr.length));
        } else {
            PUnsafe.copyMemory(arr, PUnsafe.ARRAY_CHAR_BASE_OFFSET + (off << 1L), null, this.address, len << 1L);
        }
    }
}
