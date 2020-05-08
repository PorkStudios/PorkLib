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
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.primitive.map.ObjByteMap;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Base class for all NBT tags.
 *
 * @author DaPorkchop_
 */
public abstract class Tag<T extends Tag<T>> extends AbstractRefCounted {
    public static final Map<Class<? extends Tag>, Byte> CLASS_TO_ID;

    public static final int TAG_END = 0;
    public static final int TAG_BYTE = 1;
    public static final int TAG_SHORT = 2;
    public static final int TAG_INT = 3;
    public static final int TAG_LONG = 4;
    public static final int TAG_FLOAT = 5;
    public static final int TAG_DOUBLE = 6;
    public static final int TAG_ARRAY_BYTE = 7;
    public static final int TAG_STRING = 8;
    public static final int TAG_LIST = 9;
    public static final int TAG_COMPOUND = 10;
    public static final int TAG_ARRAY_INT = 11;
    public static final int TAG_ARRAY_LONG = 12;

    static {
        Map<Class<? extends Tag>, Byte> map = new IdentityHashMap<>();
        map.put(ByteTag.class, (byte) TAG_BYTE);
        map.put(ShortTag.class, (byte) TAG_SHORT);
        map.put(IntTag.class, (byte) TAG_INT);
        map.put(LongTag.class, (byte) TAG_LONG);
        map.put(FloatTag.class, (byte) TAG_FLOAT);
        map.put(DoubleTag.class, (byte) TAG_DOUBLE);
        //map.put(ByteArrayTag.class, (byte) TAG_ARRAY_BYTE);
        map.put(StringTag.class, (byte) TAG_STRING);
        map.put(ListTag.class, (byte) TAG_LIST);
        map.put(CompoundTag.class, (byte) TAG_COMPOUND);
        //map.put(IntArrayTag.class, (byte) TAG_ARRAY_INT);
        //map.put(LongArrayTag.class, (byte) TAG_ARRAY_LONG);

        CLASS_TO_ID = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("deprecation")
    static Tag read(DataIn in, int id) throws IOException {
        switch (id) {
            case TAG_BYTE:
                return new ByteTag(in);
            case TAG_SHORT:
                return new ShortTag(in);
            case TAG_INT:
                return new IntTag(in);
            case TAG_LONG:
                return new LongTag(in);
            case TAG_FLOAT:
                return new FloatTag(in);
            case TAG_DOUBLE:
                return new DoubleTag(in);
            case TAG_STRING:
                return new StringTag(in);
            case TAG_COMPOUND:
                return new CompoundTag(in, null);
            default:
                throw new IllegalArgumentException("Unknown tag id: " + id);
        }
    }

    public abstract void write(@NonNull DataOut out) throws IOException;

    public abstract int id();

    public abstract String typeName();

    @Override
    public T retain() throws AlreadyReleasedException {
        super.retain();
        return uncheckedCast(this);
    }

    @Override
    protected void doRelease() {
        //do nothing by default, only a few tags actually have any releasing to do
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            this.toString(builder, 0, null, -1);
            return builder.toString();
        }
    }

    protected void toString(StringBuilder builder, int depth, String name, int index) {
        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("TAG_").append(this.typeName());
        if (name != null) {
            builder.append("(\"").append(name).append("\"): ");
        } else if (index >= 0) {
            builder.append('[').append(index).append("]: ");
        } else {
            builder.append(": ");
        }
    }
}
