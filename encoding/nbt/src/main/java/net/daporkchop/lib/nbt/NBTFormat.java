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

package net.daporkchop.lib.nbt;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.order.ReverseOrderedDataIn;
import net.daporkchop.lib.binary.stream.order.ReverseOrderedDataOut;
import net.daporkchop.lib.nbt.stream.encode.NBTEncoder;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.util.VarIntReverseOrderedDataIn;
import net.daporkchop.lib.nbt.util.VarIntReverseOrderedDataOut;

import java.io.IOException;
import java.nio.ByteOrder;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.Tag.*;

/**
 * The different NBT encoding formats.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum NBTFormat {
    /**
     * All integers are encoded using big-endian (network) byte ordering.
     * <p>
     * Java edition uses this format exclusively.
     */
    BIG_ENDIAN(ByteOrder.BIG_ENDIAN) {
        @Override
        public DataIn wrapIn(@NonNull DataIn in) throws IOException {
            return in;
        }

        @Override
        public DataOut wrapOut(@NonNull DataOut out) throws IOException {
            return out;
        }
    },
    /**
     * All integers are encoded using little-endian (Intel) byte ordering.
     * <p>
     * Bedrock edition uses this format for persistent data.
     */
    LITTLE_ENDIAN(ByteOrder.LITTLE_ENDIAN) {
        @Override
        public DataIn wrapIn(@NonNull DataIn in) throws IOException {
            return new ReverseOrderedDataIn(in);
        }

        @Override
        public DataOut wrapOut(@NonNull DataOut out) throws IOException {
            return new ReverseOrderedDataOut(out);
        }
    },
    /**
     * {@code int}s and {@code long}s are encoded as unsigned VarInts. All other integers are encoded using little-endian (Intel) byte ordering.
     * <p>
     * Bedrock edition uses this format for packet data.
     */
    VARINT(ByteOrder.LITTLE_ENDIAN) {
        @Override
        public DataIn wrapIn(@NonNull DataIn in) throws IOException {
            return new VarIntReverseOrderedDataIn(in);
        }

        @Override
        public DataOut wrapOut(@NonNull DataOut out) throws IOException {
            return new VarIntReverseOrderedDataOut(out);
        }
    };

    @NonNull
    private final ByteOrder order;

    /**
     * Wraps the given {@link DataIn} so that it can be used for reading NBT data in this format.
     * <p>
     * Note that this is not an infinitely chainable process: the returned {@link DataIn} may not be passed to this method again on any {@link NBTFormat},
     * as the implementation may break the {@link DataIn} contract.
     *
     * @param in the {@link DataIn} to wrap
     * @return the wrapped {@link DataIn}
     */
    public abstract DataIn wrapIn(@NonNull DataIn in) throws IOException;

    /**
     * Wraps the given {@link DataOut} so that it can be used for writing NBT data in this format.
     * <p>
     * Note that this is not an infinitely chainable process: the returned {@link DataOut} may not be passed to this method again on any {@link NBTFormat},
     * as the implementation may break the {@link DataOut} contract.
     *
     * @param out the {@link DataOut} to wrap
     * @return the wrapped {@link DataOut}
     */
    public abstract DataOut wrapOut(@NonNull DataOut out) throws IOException;

    /**
     * Reads a full NBT object tree where the root tag is a compound tag.
     *
     * @param in the {@link DataIn} to read the NBT data from
     * @return the parsed NBT data
     */
    public CompoundTag readCompound(@NonNull DataIn in) throws IOException {
        return this.readCompound(in, NBTOptions.DEFAULT);
    }

    /**
     * Reads a full NBT object tree where the root tag is a compound tag.
     *
     * @param in      the {@link DataIn} to read the NBT data from
     * @param options the {@link NBTOptions} to use for reading
     * @return the parsed NBT data
     */
    @SuppressWarnings("deprecation")
    public CompoundTag readCompound(@NonNull DataIn in, @NonNull NBTOptions options) throws IOException {
        in = this.wrapIn(in);
        checkState(in.readUnsignedByte() == TAG_COMPOUND, "Root tag was not a compound tag!");
        return new CompoundTag(in, options, in.readUTF());
    }

    /**
     * Writes a full NBT object tree where the root tag is a compound tag.
     *
     * @param out the {@link DataOut} to write the NBT data to
     * @param tag the {@link CompoundTag} to write
     */
    public void writeCompound(@NonNull DataOut out, @NonNull CompoundTag tag) throws IOException {
        checkArg(tag.name() != null, "root tag must have a name!");
        out = this.wrapOut(out);
        out.writeByte(TAG_COMPOUND);
        out.writeUTF(tag.name());
        tag.write(out);
    }

    /**
     * Gets an {@link NBTEncoder} for writing a full NBT object tree where the root tag is a compound tag.
     *
     * @param out      the {@link DataOut} to write the NBT data to
     * @param rootName the name of the root tag
     */
    public NBTEncoder encodeCompound(@NonNull DataOut out, @NonNull String rootName) throws IOException {
        return NBTEncoder.beginCompound(this.wrapOut(out), rootName);
    }

    /**
     * Reads a full NBT object tree where the root tag is a list tag.
     *
     * @param in the {@link DataIn} to read the NBT data from
     * @return the parsed NBT data
     */
    public <T extends Tag<T>> ListTag<T> readList(@NonNull DataIn in) throws IOException {
        return this.readList(in, NBTOptions.DEFAULT);
    }

    /**
     * Reads a full NBT object tree where the root tag is a list tag.
     *
     * @param in      the {@link DataIn} to read the NBT data from
     * @param options the {@link NBTOptions} to use for reading
     * @return the parsed NBT data
     */
    @SuppressWarnings("deprecation")
    public <T extends Tag<T>> ListTag<T> readList(@NonNull DataIn in, @NonNull NBTOptions options) throws IOException {
        in = this.wrapIn(in);
        checkState(in.readUnsignedByte() == TAG_LIST, "Root tag was not a list tag!");
        return new ListTag<>(in, options, in.readUTF());
    }

    /**
     * Writes a full NBT object tree where the root tag is a list tag.
     *
     * @param out the {@link DataOut} to write the NBT data to
     * @param tag the {@link ListTag} to write
     */
    public void writeList(@NonNull DataOut out, @NonNull ListTag tag) throws IOException {
        checkArg(tag.name() != null, "root tag must have a name!");
        out = this.wrapOut(out);
        out.writeByte(TAG_LIST);
        out.writeUTF(tag.name());
        tag.write(out);
    }

    /**
     * Gets an {@link NBTEncoder} for writing a full NBT object tree where the root tag is a list tag.
     *
     * @param out       the {@link DataOut} to write the NBT data to
     * @param rootName  the name of the root tag
     * @param component the component type of the root tag
     * @param length    the number of elements that should go in the root tag
     */
    public NBTEncoder encodeList(@NonNull DataOut out, @NonNull String rootName, @NonNull Class<? extends Tag> component, int length) throws IOException {
        return NBTEncoder.beginList(this.wrapOut(out), rootName, component, length);
    }
}
