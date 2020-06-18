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
import lombok.val;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class ByteTag extends NumberTag<ByteTag> {
    protected final byte value;

    public ByteTag(byte value)  {
        this.value = value;
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public ByteTag(@NonNull DataIn in) throws IOException {
        this.value = in.readByte();
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeByte(this.value);
    }

    @Override
    public int id() {
        return TAG_BYTE;
    }

    @Override
    public String typeName() {
        return "Byte";
    }

    @Override
    public int hashCode() {
        return this.value & 0xFF;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteTag && this.value == ((ByteTag) obj).value;
    }

    @Override
    public ByteTag clone() {
        return this;
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, name, index);
        builder.append(this.value).append('\n');
    }

    // NumberTag stuff

    @Override
    public byte byteValue() {
        return this.value;
    }

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }
}
