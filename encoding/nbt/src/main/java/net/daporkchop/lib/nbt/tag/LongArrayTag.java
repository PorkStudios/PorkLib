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

package net.daporkchop.lib.nbt.tag;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.nbt.NBTOptions;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class LongArrayTag extends RefCountedTag<LongArrayTag> {
    protected final long[] value;
    protected final ArrayHandle<long[]> handle;
    protected final int length;

    public LongArrayTag(@NonNull long[] value)  {
        this.value = value;
        this.handle = null;
        this.length = value.length;
    }

    public LongArrayTag(@NonNull ArrayHandle<long[]> handle)  {
        this.value = handle.get();
        this.handle = handle;
        this.length = handle.length();
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public LongArrayTag(@NonNull DataIn in, @NonNull NBTOptions options) throws IOException {
        int length = this.length = in.readInt();
        if (options.longAlloc() != null)    {
            this.handle = options.exactArraySize() ? options.longAlloc().exactly(length) : options.longAlloc().atLeast(length);
            this.value = this.handle.get();
        } else {
            this.handle = null;
            this.value = new long[length];
        }
        for (int i = 0; i < length; i++) {
            this.value[i] = in.readLong();
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeInt(this.length);
        for (int i = 0, length = this.length; i < length; i++) {
            out.writeLong(this.value[i]);
        }
    }

    @Override
    public int id() {
        return TAG_ARRAY_LONG;
    }

    @Override
    public String typeName() {
        return "Long_Array";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0, length = this.length; i < length; i++)  {
            long l = this.value[i];
            hash = hash * 31 + (int) ((l >>> 32L) ^ l);
        }
        return  hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LongArrayTag)    {
            LongArrayTag other = (LongArrayTag) obj;
            if (this.length != other.length)    {
                return false;
            }
            for (int i = 0, length = this.length; i < length; i++)  {
                if (this.value[i] != other.value[i])    {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, name, index);
        builder.append('[').append(this.value.length).append(" longs]\n");
    }

    @Override
    protected void doRelease() {
        if (this.handle != null)    {
            this.handle.release();
        }
    }
}
