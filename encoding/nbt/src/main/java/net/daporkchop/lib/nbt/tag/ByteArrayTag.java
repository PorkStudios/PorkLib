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
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.nbt.NBTOptions;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class ByteArrayTag extends Tag<ByteArrayTag> {
    protected final byte[] value;
    protected final ArrayHandle<byte[]> handle;
    protected final int length;

    public ByteArrayTag(@NonNull byte[] value)  {
        this.value = value;
        this.handle = null;
        this.length = value.length;
    }

    public ByteArrayTag(@NonNull ArrayHandle<byte[]> handle)  {
        this.value = handle.get();
        this.handle = handle;
        this.length = handle.length();
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public ByteArrayTag(@NonNull DataIn in, @NonNull NBTOptions options) throws IOException {
        int length = this.length = in.readInt();
        if (options.byteAlloc() != null)    {
            this.handle = options.exactArraySize() ? options.byteAlloc().exactly(length) : options.byteAlloc().atLeast(length);
            this.value = this.handle.get();
        } else {
            this.handle = null;
            this.value = new byte[length];
        }
        in.readFully(this.value, 0, length);
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeInt(this.length);
        out.write(this.value, 0, this.length);
    }

    @Override
    public int id() {
        return TAG_ARRAY_BYTE;
    }

    @Override
    public String typeName() {
        return "Byte_Array";
    }

    @Override
    public void release() {
        if (this.handle != null)    {
            this.handle.release();
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0, length = this.length; i < length; i++)  {
            hash = hash * 31 + (this.value[i] & 0xFF);
        }
        return  hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ByteArrayTag)    {
            ByteArrayTag other = (ByteArrayTag) obj;
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
    public ByteArrayTag clone() {
        return new ByteArrayTag(this.value.clone());
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, name, index);
        builder.append('[').append(this.value.length).append(" bytes]\n");
    }
}
