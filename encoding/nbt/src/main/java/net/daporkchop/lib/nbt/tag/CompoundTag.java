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
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.Tag.*;

/**
 * Representation of an NBT compound tag.
 *
 * @author DaPorkchop_
 */
public final class CompoundTag extends AbstractRefCounted implements Tag<CompoundTag> {
    protected final Map<String, Object> map;

    public CompoundTag(@NonNull DataIn in) throws IOException {
        this.map = new HashMap<>(); //TODO: pool these?

        while (true) {
            int id = in.readUnsignedByte();
            if (id == TAG_END) {
                break;
            }
            String name = in.readUTF();
            checkState(this.map.putIfAbsent(name, readValue(in, id)) == null, "Duplicate key: \"%s\"", name);
        }
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
    public int id() {
        return TAG_COMPOUND;
    }
}
