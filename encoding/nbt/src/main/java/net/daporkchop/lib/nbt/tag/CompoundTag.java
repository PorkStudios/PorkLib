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
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.nbt.NBTOptions;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Representation of an NBT compound tag.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class CompoundTag extends Tag<CompoundTag> {
    protected final Map<String, Tag> map = new LinkedHashMap<>();
    @Getter
    protected final String name;

    public CompoundTag() {
        this.name = null;
    }

    public CompoundTag(@NonNull String name) {
        this.name = name;
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public CompoundTag(@NonNull DataIn in, @NonNull NBTOptions options, String selfName) throws IOException {
        this.name = selfName;

        while (true) {
            int id = in.readUnsignedByte();
            if (id == TAG_END) {
                break;
            }
            String name = in.readUTF();
            checkState(this.map.putIfAbsent(name, Tag.read(in, options, id)) == null, "Duplicate tag name: \"%s\"", name);
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        for (Map.Entry<String, Tag> entry : this.map.entrySet()) {
            out.writeByte(entry.getValue().id());
            out.writeUTF(entry.getKey());
            entry.getValue().write(out);
        }
        out.writeByte(TAG_END);
    }

    @Override
    public int id() {
        return TAG_COMPOUND;
    }

    @Override
    public String typeName() {
        return "Compound";
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompoundTag && this.map.equals(((CompoundTag) obj).map);
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, PorkUtil.fallbackIfNull(name, this.name), index);
        builder.append(this.map.size()).append(" entries\n");
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("{\n");
        for (Map.Entry<String, Tag> entry : this.map.entrySet()) {
            entry.getValue().toString(builder, depth + 1, entry.getKey(), -1);
        }
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("}\n");
    }

    @Override
    protected void doRelease() {
        this.map.forEach((key, value) -> value.release());
        this.map.clear();
    }
}
