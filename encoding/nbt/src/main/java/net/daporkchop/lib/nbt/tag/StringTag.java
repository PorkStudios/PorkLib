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
import net.daporkchop.lib.nbt.NBTOptions;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class StringTag extends Tag<StringTag> {
    protected final String value;

    public StringTag(@NonNull String value)  {
        this.value = value;
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public StringTag(@NonNull DataIn in, @NonNull NBTOptions options) throws IOException {
        String value = in.readUTF();
        this.value = options.internValues() ? value.intern() : value;
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override
    public int id() {
        return TAG_STRING;
    }

    @Override
    public String typeName() {
        return "String";
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringTag && this.value.equals(((StringTag) obj).value);
    }

    @Override
    public StringTag clone() {
        return this;
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, name, index);
        builder.append('"').append(this.value).append("\"\n");
    }
}
