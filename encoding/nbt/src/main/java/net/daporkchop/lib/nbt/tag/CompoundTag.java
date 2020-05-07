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
import java.util.HashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.TagUtil.*;

/**
 * Representation of an NBT compound tag.
 *
 * @author DaPorkchop_
 */
public final class CompoundTag extends AbstractRefCounted {
    protected final Map<String, Object> map;
    protected final String name;

    public CompoundTag() {
        this.map = new HashMap<>();
        this.name = null;
    }

    /**
     * Internal constructor, don't use this.
     *
     * @see TagUtil#readCompound(DataIn)
     */
    @Deprecated
    public CompoundTag(@NonNull DataIn in, boolean root) throws IOException {
        this.map = new HashMap<>(); //TODO: pool these?

        if (root) {
            checkState(in.readUnsignedByte() == TAG_COMPOUND, "Root tag is not a compound tag!");
            this.name = in.readUTF();
        } else {
            this.name = null;
        }

        while (true) {
            int id = in.readUnsignedByte();
            if (id == TAG_END) {
                break;
            }
            String name = in.readUTF();
            checkState(this.map.putIfAbsent(name, parse(in, id)) == null, "Duplicate key: \"%s\"", name);
        }
    }

    public void write(@NonNull DataOut out) throws IOException  {
        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            int id = IDS.get(entry.getValue().getClass());
            out.writeByte(id);
            out.writeUTF(entry.getKey());
            encode(out, entry.getValue(), id);
        }
        out.writeByte(TAG_END);
    }

    /**
     * Gets this compound tag's name.
     * <p>
     * This will only be non-null if this tag was read from disk, and is the root tag.
     *
     * @return this compound tag's name
     */
    public String getName() {
        return this.name;
    }

    @Override
    protected void doRelease() {
        this.map.forEach((key, value) -> {
            if (value instanceof RefCounted) {
                ((RefCounted) value).release();
            }
        });
        this.map.clear();
    }

    @Override
    public CompoundTag retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompoundTag) {
            CompoundTag other = (CompoundTag) obj;
            return (this.name != null ? this.name.equals(other.name) : other.name == null) && this.map.equals(other.map);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            TagUtil.toString(this.name, this, builder, 0, -1);
            return builder.toString();
        }
    }

    void toString(@NonNull StringBuilder builder, int depth) {
        builder.append(this.map.size()).append(" entries\n");
        PStrings.appendMany(builder, ' ', (depth - 1) << 1);
        builder.append("{\n");
        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            TagUtil.toString(entry.getKey(), entry.getValue(), builder, depth, -1);
        }
        PStrings.appendMany(builder, ' ', (depth - 1) << 1);
        builder.append('}');
    }
}
