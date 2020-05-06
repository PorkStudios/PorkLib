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
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.misc.string.PStrings;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * Base interface for an NBT tag.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Tag {
    private static final Map<Class<?>, String> DISPLAY_NAMES = new IdentityHashMap<>();

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
        DISPLAY_NAMES.put(Byte.class, "Byte");
        DISPLAY_NAMES.put(Short.class, "Short");
        DISPLAY_NAMES.put(Integer.class, "Int");
        DISPLAY_NAMES.put(Long.class, "Long");
        DISPLAY_NAMES.put(Float.class, "Float");
        DISPLAY_NAMES.put(Double.class, "Double");
        DISPLAY_NAMES.put(String.class, "String");
        DISPLAY_NAMES.put(byte[].class, "ByteArray");
        DISPLAY_NAMES.put(int[].class, "IntArray");
        DISPLAY_NAMES.put(long[].class, "LongArray");
        DISPLAY_NAMES.put(CompoundTag.class, "Compound");
        DISPLAY_NAMES.put(ListTag.class, "List");
    }

    /**
     * Reads a single NBT value.
     * <p>
     * If the given id is a compound tag or a list, this method will recursively read the NBT object tree until the compound tag or list is finished.
     *
     * @param in the {@link DataIn} to read data from
     * @param id the tag ID of the value to read
     * @return the NBT value that was read
     */
    public static Object parse(@NonNull DataIn in, int id) throws IOException {
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
                return new ListTag<>(in, false);
            case TAG_COMPOUND:
                return new CompoundTag(in, false);
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

    public static CompoundTag readCompound(@NonNull DataIn in) throws IOException   {
        return new CompoundTag(in, true);
    }

    public static <T> ListTag<T> readList(@NonNull DataIn in) throws IOException   {
        return new ListTag<>(in, true);
    }

    static void toString(String name, @NonNull Object value, @NonNull StringBuilder builder, int depth, int index) {
        String displayName = DISPLAY_NAMES.get(value.getClass());
        checkArg(displayName != null, "Unknown value: %s", value);

        PStrings.appendMany(builder, ' ', depth << 1);
        builder.append("TAG_").append(displayName);
        if (name != null)   {
            builder.append("(\"").append(name).append("\")");
        } else if (index >= 0)  {
            builder.append('[').append(index).append(']');
        }
        builder.append(": ");

        if (value instanceof CompoundTag) {
            ((CompoundTag) value).toString(builder, depth + 1);
        } else if (value instanceof ListTag) {
            ((ListTag) value).toString(builder, depth + 1);
        } else if (value.getClass().isArray()) {
            builder.append('[').append(Array.getLength(value)).append(' ').append(value.getClass().getComponentType().getName()).append("s]");
        } else if (value instanceof String) {
            builder.append('"').append((String) value).append('"');
        } else {
            builder.append(value);
        }

        builder.append('\n');
    }
}
