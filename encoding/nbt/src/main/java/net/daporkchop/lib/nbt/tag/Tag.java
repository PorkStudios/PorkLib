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
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

/**
 * Base interface for an NBT tag.
 *
 * @author DaPorkchop_
 */
public interface Tag<T extends Tag<T>> {
    int TAG_END = 0;
    int TAG_BYTE = 1;
    int TAG_SHORT = 2;
    int TAG_INT = 3;
    int TAG_LONG = 4;
    int TAG_FLOAT = 5;
    int TAG_DOUBLE = 6;
    int TAG_ARRAY_BYTE = 7;
    int TAG_STRING = 8;
    int TAG_LIST = 9;
    int TAG_COMPOUND = 10;
    int TAG_ARRAY_INT = 11;
    int TAG_ARRAY_LONG = 12;

    static Object readValue(@NonNull DataIn in, int id) throws IOException {
        switch (id) {
            case TAG_BYTE:
                return in.readByte();
            case TAG_SHORT:
                return in.readShort();
            case TAG_INT:
                return in.readInt();
            case TAG_LONG:
                return in.readLong();
            case TAG_FLOAT:
                return in.readFloat();
            case TAG_DOUBLE:
                return in.readDouble();
            case TAG_ARRAY_BYTE:
                return in.fill(new byte[in.readInt()]);
            case TAG_STRING:
                return in.readUTF();
            case TAG_LIST:
                return new ListTag<>(in);
            case TAG_COMPOUND:
                return new CompoundTag(in);
            case TAG_ARRAY_INT: {
                int length = in.readInt();
                int[] arr = new int[length];
                for (int i = 0; i < length; i++) {
                    arr[i] = in.readInt();
                }
                return arr;
            }
            case TAG_ARRAY_LONG: {
                int length = in.readInt();
                long[] arr = new long[length];
                for (int i = 0; i < length; i++) {
                    arr[i] = in.readLong();
                }
                return arr;
            }
            default:
                throw new IllegalArgumentException("Invalid tag ID: " + id);
        }
    }

    /**
     * @return this tag's numeric ID
     */
    int id();
}
