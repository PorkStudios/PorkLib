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

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.TagUtil.*;

/**
 * Representation of an NBT list tag.
 *
 * @author DaPorkchop_
 */
public final class ListTag<T> extends AbstractRefCounted {
    protected final List<Object> list;
    protected final String name;

    public ListTag(String name) {
        this.list = new ArrayList<>();
        this.name = name;
    }

    /**
     * Internal constructor, don't use this.
     *
     * @see TagUtil#readList(DataIn) (DataIn)
     */
    @Deprecated
    public ListTag(@NonNull DataIn in, boolean root) throws IOException {
        if (root) {
            checkState(in.readUnsignedByte() == TAG_LIST, "Root tag is not a list tag!");
            this.name = in.readUTF();
        } else {
            this.name = null;
        }

        int id = in.readUnsignedByte();
        int length = max(in.readInt(), 0);

        this.list = new ArrayList<>(length); //TODO: pool these?
        for (int i = 0; i < length; i++) {
            this.list.add(parse(in, id));
        }
    }

    public void write(@NonNull DataOut out) throws IOException  {
        if (this.list.isEmpty())    {
            out.writeByte(TAG_END);
            out.writeInt(0);
            return;
        }

        int id = IDS.get(this.list.get(0).getClass());
        out.writeByte(id);
        out.writeInt(this.list.size());
        for (Object value : this.list) {
            encode(out, value, id);
        }
    }

    /**
     * Gets this list tag's name.
     * <p>
     * This will only be non-null if this tag was read from disk, and is the root tag.
     *
     * @return this list tag's name
     */
    public String getName() {
        return this.name;
    }

    @Override
    protected void doRelease() {
        this.list.forEach(value -> {
            if (value instanceof RefCounted) {
                ((RefCounted) value).release();
            }
        });
        this.list.clear();
    }

    @Override
    public ListTag<T> retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ListTag) {
            ListTag other = (ListTag) obj;
            return (this.name != null ? this.name.equals(other.name) : other.name == null) && this.list.equals(other.list);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            TagUtil.toString(null, this, builder, 0, -1);
            return builder.toString();
        }
    }

    void toString(@NonNull StringBuilder builder, int depth) {
        builder.append(this.list.size()).append(" entries\n");
        PStrings.appendMany(builder, ' ', (depth - 1) << 1);
        builder.append("[\n");
        for (int i = 0, size = this.list.size(); i < size; i++) {
            TagUtil.toString(null, this.list.get(i), builder, depth, i);
        }
        PStrings.appendMany(builder, ' ', (depth - 1) << 1);
        builder.append(']');
    }
}
