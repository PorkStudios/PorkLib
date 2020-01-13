/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.nbt.streaming.encode;

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.Tag;

/**
 * @author DaPorkchop_
 */
public final class StreamingListTagEncoder extends StreamingNBTEncoder {
    protected final int lengthIndex;
    protected final int type;
    protected int count = 0;

    protected StreamingListTagEncoder(@NonNull StreamingNBTEncoder parent, @NonNull Class<? extends Tag> tagClass) {
        super(parent);

        byte id = this.registry.getId(tagClass);
        if (id == Byte.MIN_VALUE) {
            throw new IllegalArgumentException("Illegal tag class: " + tagClass.getCanonicalName());
        }
        this.lengthIndex = this.out.writeByte(this.type = id).writerIndex();
        this.out.writeInt(-1);
    }

    @Override
    public void close() {
        if (this.count == 0) {
            this.out.setByte(this.lengthIndex - 1, 0);
        }
        this.out.setInt(this.lengthIndex, this.count);
    }

    public void appendBoolean(boolean value) {
        this.appendByte(value ? (byte) 1 : 0);
    }

    public void appendByte(byte value) {
        if (this.type != this.byteTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeByte(value);
    }

    public void appendShort(short value) {
        if (this.type != this.shortTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeShort(value);
    }

    public void appendInt(int value) {
        if (this.type != this.intTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeInt(value);
    }

    public void appendLong(long value) {
        if (this.type != this.longTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeLong(value);
    }

    public void appendFloat(float value) {
        if (this.type != this.floatTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeFloat(value);
    }

    public void appendDouble(double value) {
        if (this.type != this.doubleTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeDouble(value);
    }

    public void appendByteArray(@NonNull byte[] value) {
        if (this.type != this.byteArrayTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeInt(value.length).writeBytes(value);
    }

    public void appendString(@NonNull CharSequence value) {
        if (this.type != this.stringTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.appendText(value);
    }

    public StreamingCompoundTagEncoder pushCompound() {
        if (this.type != this.compoundTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        return new StreamingCompoundTagEncoder(this);
    }

    public StreamingListTagEncoder pushList(@NonNull Class<? extends Tag> tagClass) {
        if (this.type != this.listTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        return new StreamingListTagEncoder(this, tagClass);
    }

    public void appendIntArray(@NonNull int[] value) {
        if (this.type != this.intArrayTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeInt(value.length);
        for (int i : value) {
            this.out.writeInt(i);
        }
    }

    public void appendLongArray(@NonNull long[] value) {
        if (this.type != this.longArrayTagId) {
            throw new IllegalStateException();
        }
        this.count++;
        this.out.writeInt(value.length);
        for (long l : value) {
            this.out.writeLong(l);
        }
    }

    /**
     * Increments the internal tag counter.
     * <p>
     * Should not be used unless you know exactly what you're doing!
     */
    public void _internal_incrementCounter() {
        this.count++;
    }
}
