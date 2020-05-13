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
import java.util.ArrayList;
import java.util.List;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class ListTag<T extends Tag<T>> extends Tag<ListTag<T>> {
    @Getter
    protected final List<T> list;
    @Getter
    protected final String name;
    protected final int component;

    public ListTag(@NonNull Class<T> type) {
        this.list = new ArrayList<>();
        this.name = null;
        checkArg((this.component = Tag.CLASS_TO_ID.getOrDefault(type, 0)) != 0, "Invalid component class: %s", type);
    }

    public ListTag(@NonNull String name, @NonNull Class<T> type) {
        this.list = new ArrayList<>();
        this.name = name;
        checkArg((this.component = Tag.CLASS_TO_ID.getOrDefault(type, 0)) != 0, "Invalid component class: %s", type);
    }

    /**
     * @deprecated Internal API, do not touch!
     */
    @Deprecated
    public ListTag(@NonNull DataIn in, @NonNull NBTOptions options, String selfName) throws IOException {
        this.name = selfName;

        this.component = in.readUnsignedByte();
        int size = in.readInt();
        this.list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.list.add(uncheckedCast(options.parser().read(in, options, this.component)));
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeByte(this.component);
        out.writeInt(this.list.size());
        for (int i = 0, size = this.list.size(); i < size; i++) {
            this.list.get(i).write(out);
        }
    }

    @Override
    public int id() {
        return TAG_LIST;
    }

    @Override
    public String typeName() {
        return "List";
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ListTag && this.component == ((ListTag) obj).component && this.list.equals(((ListTag) obj).list);
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, PorkUtil.fallbackIfNull(name, this.name), index);
        builder.append(this.list.size()).append(" entries\n");
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("[\n");
        for (int i = 0, size = this.list.size(); i < size; i++) {
            this.list.get(i).toString(builder, depth + 1, null, i);
        }
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("]\n");
    }
}
