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

package net.daporkchop.lib.nbt.stream.encode;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.Tag;

import java.io.IOException;
import java.nio.ByteBuffer;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.Tag.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class NBTEncoder implements AutoCloseable {
    public static NBTEncoder beginCompound(@NonNull DataOut out, @NonNull String name) throws IOException {
        out.writeByte(TAG_COMPOUND);
        out.writeUTF(name);
        return new NBTEncoder(out, EncodeContext.create(null, TAG_COMPOUND, 0, 0));
    }

    public static NBTEncoder beginList(@NonNull DataOut out, @NonNull String name, @NonNull Class<? extends Tag> type, int length) throws IOException {
        int component = Tag.CLASS_TO_ID.getOrDefault(type, 0);
        notNegative(length, "length");
        checkArg(component != 0, "Invalid component class: %s", type);
        out.writeByte(TAG_LIST);
        out.writeUTF(name);
        out.writeByte(component);
        out.writeInt(length);
        return new NBTEncoder(out, EncodeContext.create(null, TAG_LIST, length, component));
    }

    @Getter
    protected final DataOut out;

    protected EncodeContext context;

    private NBTEncoder(@NonNull DataOut out, @NonNull EncodeContext context) {
        this.out = out;
        this.context = context;
    }

    //
    //
    // put named tag
    //
    //

    /**
     * Writes a named TAG_Byte.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putByte(@NonNull String name, int value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_BYTE);
        this.out.writeUTF(name);
        this.out.writeByte(value);
        return this;
    }

    /**
     * Writes a named TAG_Short.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putShort(@NonNull String name, int value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_SHORT);
        this.out.writeUTF(name);
        this.out.writeShort(value);
        return this;
    }

    /**
     * Writes a named TAG_Int.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putInt(@NonNull String name, int value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_INT);
        this.out.writeUTF(name);
        this.out.writeInt(value);
        return this;
    }

    /**
     * Writes a named TAG_Long.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putLong(@NonNull String name, long value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_LONG);
        this.out.writeUTF(name);
        this.out.writeLong(value);
        return this;
    }

    /**
     * Writes a named TAG_Float.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putFloat(@NonNull String name, float value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_FLOAT);
        this.out.writeUTF(name);
        this.out.writeFloat(value);
        return this;
    }

    /**
     * Writes a named TAG_Double.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putDouble(@NonNull String name, double value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_DOUBLE);
        this.out.writeUTF(name);
        this.out.writeDouble(value);
        return this;
    }

    /**
     * Writes a named TAG_Byte_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putByteArray(@NonNull String name, @NonNull byte[] value) throws IOException {
        return this.putByteArray(name, value, 0, value.length);
    }

    /**
     * Writes a named TAG_Byte_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     * @param off   the index of the first byte in the array to write
     * @param len   the number of bytes to write
     */
    public NBTEncoder putByteArray(@NonNull String name, @NonNull byte[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(len);
        this.out.write(value, off, len);
        return this;
    }

    /**
     * Writes a named TAG_Byte_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putByteArray(@NonNull String name, @NonNull ByteBuffer value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(value.remaining());
        this.out.write(value);
        return this;
    }

    /**
     * Writes a named TAG_Byte_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putByteArray(@NonNull String name, @NonNull ByteBuf value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(value.readableBytes());
        this.out.write(value);
        return this;
    }

    /**
     * Writes a named TAG_String.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putString(@NonNull String name, @NonNull String value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_STRING);
        this.out.writeUTF(name);
        this.out.writeUTF(value);
        return this;
    }

    /**
     * Writes a named TAG_Int_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putIntArray(@NonNull String name, @NonNull int[] value) throws IOException {
        return this.putIntArray(name, value, 0, value.length);
    }

    /**
     * Writes a named TAG_Int_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     * @param off   the index of the first int in the array to write
     * @param len   the number of ints to write
     */
    public NBTEncoder putIntArray(@NonNull String name, @NonNull int[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(len);
        for (int i = 0; i < len; i++) {
            this.out.writeInt(value[off + i]);
        }
        return this;
    }

    /**
     * Writes a named TAG_Long_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putLongArray(@NonNull String name, @NonNull long[] value) throws IOException {
        return this.putLongArray(name, value, 0, value.length);
    }

    /**
     * Writes a named TAG_Long_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     * @param off   the index of the first long in the array to write
     * @param len   the number of longs to write
     */
    public NBTEncoder putLongArray(@NonNull String name, @NonNull long[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(len);
        for (int i = 0; i < len; i++) {
            this.out.writeLong(value[off + i]);
        }
        return this;
    }

    /**
     * Writes a named {@link Tag}.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name  the tag's name
     * @param value the tag's value
     */
    public NBTEncoder putTag(@NonNull String name, @NonNull Tag value) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(value.id());
        this.out.writeUTF(name);
        value.write(this.out);
        return this;
    }

    //
    //
    // start named tag
    //
    //

    /**
     * Begins to write a named TAG_Byte.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startByte(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_BYTE);
    }

    /**
     * Begins to write a named TAG_Short.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startShort(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_SHORT);
    }

    /**
     * Begins to write a named TAG_Int.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startInt(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_INT);
    }

    /**
     * Begins to write a named TAG_Long.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startLong(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_LONG);
    }

    /**
     * Begins to write a named TAG_Float.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startFloat(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_FLOAT);
    }

    /**
     * Begins to write a named TAG_Double.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startDouble(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_DOUBLE);
    }

    /**
     * Begins to write a named TAG_Byte_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name   the tag's name
     * @param length the number of bytes that will be in the array
     */
    public NBTEncoder startByteArray(@NonNull String name, int length) throws IOException {
        notNegative(length, "length");
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_BYTE);
        this.out.writeUTF(name);
        this.out.writeInt(length);
        this.context = EncodeContext.create(this.context, TAG_ARRAY_BYTE, length, TAG_BYTE);
        return this;
    }

    /**
     * Begins to write a named TAG_String.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startString(@NonNull String name) throws IOException {
        return this.doStartNamedData(name, TAG_STRING);
    }

    private NBTEncoder doStartNamedData(@NonNull String name, int id) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(id);
        this.out.writeUTF(name);
        this.context = EncodeContext.create(this.context, id, 1, id);
        return this;
    }

    /**
     * Begins to write a named TAG_List with TAG_Byte values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListByte(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_BYTE);
    }

    /**
     * Begins to write a named TAG_List with TAG_Short values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListShort(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_SHORT);
    }

    /**
     * Begins to write a named TAG_List with TAG_Int values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListInt(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_INT);
    }

    /**
     * Begins to write a named TAG_List with TAG_Long values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListLong(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_LONG);
    }

    /**
     * Begins to write a named TAG_List with TAG_Float values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListFloat(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_FLOAT);
    }

    /**
     * Begins to write a named TAG_List with TAG_Double values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListDouble(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_DOUBLE);
    }

    /**
     * Begins to write a named TAG_List with TAG_Byte_Array values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListByteArray(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_ARRAY_BYTE);
    }

    /**
     * Begins to write a named TAG_List with TAG_String values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListString(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_STRING);
    }

    /**
     * Begins to write a named TAG_List with TAG_List values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListList(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_LIST);
    }

    /**
     * Begins to write a named TAG_List with TAG_Compound values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListCompound(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_COMPOUND);
    }

    /**
     * Begins to write a named TAG_List with TAG_Int_Array values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListIntArray(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_ARRAY_INT);
    }

    /**
     * Begins to write a named TAG_List with TAG_Long_Array values.
     *
     * @param name   the tag's name
     * @param length the number of values that will be in the list
     */
    public NBTEncoder startListLongArray(@NonNull String name, int length) throws IOException {
        return this.doStartNamedList(name, length, TAG_ARRAY_LONG);
    }

    private NBTEncoder doStartNamedList(@NonNull String name, int length, int component) throws IOException {
        notNegative(length, "length");
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_LIST);
        this.out.writeUTF(name);
        this.out.writeByte(component);
        this.out.writeInt(length);
        this.context = EncodeContext.create(this.context, TAG_LIST, length, component);
        return this;
    }

    /**
     * Begins to write a named TAG_Compound.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name the tag's name
     */
    public NBTEncoder startCompound(@NonNull String name) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_COMPOUND);
        this.out.writeUTF(name);
        this.context = EncodeContext.create(this.context, TAG_COMPOUND, 0, 0);
        return this;
    }

    /**
     * Begins to write a named TAG_Int_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name   the tag's name
     * @param length the number of ints that will be in the array
     */
    public NBTEncoder startIntArray(@NonNull String name, int length) throws IOException {
        notNegative(length, "length");
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_INT);
        this.out.writeUTF(name);
        this.out.writeInt(length);
        this.context = EncodeContext.create(this.context, TAG_ARRAY_INT, length, TAG_INT);
        return this;
    }

    /**
     * Begins to write a named TAG_Long_Array.
     * <p>
     * The current tag must be a TAG_Compound.
     *
     * @param name   the tag's name
     * @param length the number of longs that will be in the array
     */
    public NBTEncoder startLongArray(@NonNull String name, int length) throws IOException {
        notNegative(length, "length");
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_ARRAY_LONG);
        this.out.writeUTF(name);
        this.out.writeInt(length);
        this.context = EncodeContext.create(this.context, TAG_ARRAY_LONG, length, TAG_LONG);
        return this;
    }

    //
    //
    // put raw tag value(s)
    //
    //

    /**
     * Writes a TAG_Byte value.
     * <p>
     * The current tag must be expecting a TAG_Byte value (i.e. the value of a TAG_Byte or the next entry in a TAG_List of TAG_Byte).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendByte(int value) throws IOException {
        checkState(this.context.component == TAG_BYTE, "Current tag cannot accept a byte value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeByte(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a TAG_Short value.
     * <p>
     * The current tag must be expecting a TAG_Short value (i.e. the value of a TAG_Short or the next entry in a TAG_List of TAG_Short).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendShort(int value) throws IOException {
        checkState(this.context.component == TAG_SHORT, "Current tag cannot accept a short value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeShort(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a TAG_Int value.
     * <p>
     * The current tag must be expecting a TAG_Int value (i.e. the value of a TAG_Int or the next entry in a TAG_List of TAG_Int).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendInt(int value) throws IOException {
        checkState(this.context.component == TAG_INT, "Current tag cannot accept a int value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeInt(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a TAG_Long value.
     * <p>
     * The current tag must be expecting a TAG_Long value (i.e. the value of a TAG_Long or the next entry in a TAG_List of TAG_Long).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendLong(long value) throws IOException {
        checkState(this.context.component == TAG_LONG, "Current tag cannot accept a long value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeLong(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a TAG_Float value.
     * <p>
     * The current tag must be expecting a TAG_Float value (i.e. the value of a TAG_Float or the next entry in a TAG_List of TAG_Float).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendFloat(float value) throws IOException {
        checkState(this.context.component == TAG_FLOAT, "Current tag cannot accept a float value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeFloat(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a TAG_Double value.
     * <p>
     * The current tag must be expecting a TAG_Double value (i.e. the value of a TAG_Double or the next entry in a TAG_List of TAG_Double).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendDouble(double value) throws IOException {
        checkState(this.context.component == TAG_DOUBLE, "Current tag cannot accept a double value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeDouble(value);
        this.context.length--;
        return this;
    }

    /**
     * Writes a sequence of byte values.
     * <p>
     * The current tag must be expecting the given number of TAG_Byte values (i.e. the value of a single TAG_Byte, the next entries in a TAG_List
     * of TAG_Byte, or some/all of the value of a TAG_Byte_Array).
     *
     * @param value the byte values
     */
    public NBTEncoder appendBytes(@NonNull byte[] value) throws IOException {
        return this.appendBytes(value, 0, value.length);
    }

    /**
     * Writes a sequence of byte values.
     * <p>
     * The current tag must be expecting the given number of TAG_Byte values (i.e. the value of a single TAG_Byte, the next entries in a TAG_List
     * of TAG_Byte, or some/all of the value of a TAG_Byte_Array).
     *
     * @param value the byte values
     * @param off   the index of the first byte in the array to write
     * @param len   the number of bytes to write
     */
    public NBTEncoder appendBytes(@NonNull byte[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.component == TAG_BYTE, "Current tag cannot accept a byte value!");
        checkState(this.context.length >= len, "Current tag cannot accept %d more values! (remaining: %d)", len, this.context.length);
        this.out.write(value, off, len);
        this.context.length -= len;
        return this;
    }

    /**
     * Writes a sequence of byte values.
     * <p>
     * The current tag must be expecting the given number of TAG_Byte values (i.e. the value of a single TAG_Byte, the next entries in a TAG_List
     * of TAG_Byte, or some/all of the value of a TAG_Byte_Array).
     *
     * @param value the byte values
     */
    public NBTEncoder appendBytes(@NonNull ByteBuffer value) throws IOException {
        checkState(this.context.component == TAG_BYTE, "Current tag cannot accept a byte value!");
        checkState(this.context.length >= value.remaining(), "Current tag cannot accept %d more values! (remaining: %d)", value.remaining(), this.context.length);
        this.context.length -= this.out.write(value);
        return this;
    }

    /**
     * Writes a sequence of byte values.
     * <p>
     * The current tag must be expecting the given number of TAG_Byte values (i.e. the value of a single TAG_Byte, the next entries in a TAG_List
     * of TAG_Byte, or some/all of the value of a TAG_Byte_Array).
     *
     * @param value the byte values
     */
    public NBTEncoder appendBytes(@NonNull ByteBuf value) throws IOException {
        checkState(this.context.component == TAG_BYTE, "Current tag cannot accept a byte value!");
        checkState(this.context.length >= value.readableBytes(), "Current tag cannot accept %d more values! (remaining: %d)", value.readableBytes(), this.context.length);
        this.context.length -= this.out.write(value);
        return this;
    }

    /**
     * Writes a TAG_String value.
     * <p>
     * The current tag must be expecting a TAG_String value (i.e. the value of a TAG_String or the next entry in a TAG_List of TAG_Strings).
     *
     * @param value the tag's value
     */
    public NBTEncoder appendString(@NonNull String value) throws IOException {
        checkState(this.context.component == TAG_STRING, "Current tag cannot accept a string value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeUTF(value);
        this.context.length--;
        return this;
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Byte values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListByte(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_BYTE);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Short values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListShort(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_SHORT);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Int values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListInt(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_INT);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Long values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListLong(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_LONG);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Float values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListFloat(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_FLOAT);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Double values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListDouble(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_DOUBLE);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Byte_Array values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListByteArray(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_ARRAY_BYTE);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_String values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListString(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_STRING);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_List values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListList(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_LIST);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Compound values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListCompound(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_COMPOUND);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Int_Array values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListIntArray(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_ARRAY_INT);
    }

    /**
     * Begins to write an unnamed TAG_List with TAG_Long_Array values.
     *
     * @param length the number of values that will be in the list
     */
    public NBTEncoder appendListLongArray(int length) throws IOException {
        return this.doStartUnnamedList(length, TAG_ARRAY_LONG);
    }

    private NBTEncoder doStartUnnamedList(int length, int component) throws IOException {
        notNegative(length, "length");
        checkState(this.context.component == TAG_LIST, "Current tag cannot accept a list value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeByte(component);
        this.out.writeInt(length);
        this.context.length--;
        this.context = EncodeContext.create(this.context, TAG_LIST, length, component);
        return this;
    }

    /**
     * Begins to write an unnamed TAG_Compound value.
     * <p>
     * The current tag must be expecting a TAG_Compound value (i.e. the next entry in a TAG_List of TAG_Compounds).
     */
    public NBTEncoder appendCompound() throws IOException {
        checkState(this.context.component == TAG_COMPOUND, "Current tag cannot accept a compound tag!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.context.length--;
        this.context = EncodeContext.create(this.context, TAG_COMPOUND, 0, 0);
        return this;
    }

    /**
     * Writes a sequence of int values.
     * <p>
     * The current tag must be expecting the given number of TAG_Int values (i.e. the value of a single TAG_Int, the next entries in a TAG_List
     * of TAG_Int, or some/all of the value of a TAG_Int_Array).
     *
     * @param value the int values
     */
    public NBTEncoder appendInts(@NonNull int[] value) throws IOException {
        return this.appendInts(value, 0, value.length);
    }

    /**
     * Writes a sequence of int values.
     * <p>
     * The current tag must be expecting the given number of TAG_Int values (i.e. the value of a single TAG_Int, the next entries in a TAG_List
     * of TAG_Int, or some/all of the value of a TAG_Int_Array).
     *
     * @param value the int values
     * @param off   the index of the first int in the array to write
     * @param len   the number of ints to write
     */
    public NBTEncoder appendInts(@NonNull int[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.component == TAG_INT, "Current tag cannot accept a int value!");
        checkState(this.context.length >= len, "Current tag cannot accept %d more values! (remaining: %d)", len, this.context.length);
        for (int i = 0; i < len; i++) {
            this.out.writeInt(value[off + i]);
        }
        this.context.length -= len;
        return this;
    }

    /**
     * Writes a sequence of long values.
     * <p>
     * The current tag must be expecting the given number of TAG_Long values (i.e. the value of a single TAG_Long, the next entries in a TAG_List
     * of TAG_Long, or some/all of the value of a TAG_Long_Array).
     *
     * @param value the long values
     */
    public NBTEncoder appendLongs(@NonNull long[] value) throws IOException {
        return this.appendLongs(value, 0, value.length);
    }

    /**
     * Writes a sequence of long values.
     * <p>
     * The current tag must be expecting the given number of TAG_Long values (i.e. the value of a single TAG_Long, the next entries in a TAG_List
     * of TAG_Long, or some/all of the value of a TAG_Long_Array).
     *
     * @param value the long values
     * @param off   the index of the first long in the array to write
     * @param len   the number of longs to write
     */
    public NBTEncoder appendLongs(@NonNull long[] value, int off, int len) throws IOException {
        checkRangeLen(value.length, off, len);
        checkState(this.context.component == TAG_LONG, "Current tag cannot accept a long value!");
        checkState(this.context.length >= len, "Current tag cannot accept %d more values! (remaining: %d)", len, this.context.length);
        for (int i = 0; i < len; i++) {
            this.out.writeLong(value[off + i]);
        }
        this.context.length -= len;
        return this;
    }

    /**
     * Writes an unnamed {@link Tag}.
     *
     * @param value the tag's value
     */
    public NBTEncoder appendTag(@NonNull Tag value) throws IOException {
        int id = value.id();
        checkState(this.context.component == id, "Current tag cannot accept a value of type %d!", id);
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        value.write(this.out);
        this.context.length--;
        return this;
    }

    //
    //
    // close tag/encoder
    //
    //

    /**
     * An unsafe alternative to {@link #close()}.
     * <p>
     * This method omits all tag length checks, meaning that it could be possible for a tag to be written with an invalid number of values. It's
     * only intended for use in a scenario where raw data is being manually appended to {@link #out()}.
     */
    public NBTEncoder closeUnsafe() throws IOException {
        if (this.context.id == TAG_COMPOUND) {
            this.out.writeByte(TAG_END);
        }

        if ((this.context = this.context.finish()) == null) {
            //root tag was finished!
            this.out.close();
        }
        return this;
    }

    /**
     * Exactly the same as {@link #close()}, but returns itself for chaining.
     *
     * @see #close()
     */
    public NBTEncoder closeTag() throws IOException {
        this.close();
        return this;
    }

    /**
     * Closes the current tag.
     * <p>
     * If the current tag is a compound tag, a TAG_End will be written and the tag closed.
     * <p>
     * When the root tag is closed, the encoder will consider itself to be complete and will close the destination {@link DataOut}.
     *
     * @throws IllegalStateException if the current tag is not a compound tag and expected more values to be written (such as for a list, or a
     *                               named tag whose value is being written separately)
     */
    @Override
    public void close() throws IOException {
        EncodeContext context = this.context;
        int id = context.id;
        if (id == TAG_COMPOUND) {
            this.out.writeByte(TAG_END);
        } else {
            int remaining = context.length;
            checkState(remaining == 0, "Current component (id: %d) expected %d more values!", id, remaining);
        }

        if ((this.context = context.finish()) == null) {
            //root tag was finished!
            this.out.close();
        }
    }
}
