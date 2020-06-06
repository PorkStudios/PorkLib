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

package net.daporkchop.lib.minecraft.format.anvil.storage;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.format.common.nibble.NibbleArray;
import net.daporkchop.lib.minecraft.block.BlockRegistry;

import java.util.Arrays;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Heap-based {@link LegacyBlockStorage} implementation.
 * <p>
 * This also stores block meta itself (without wrapping it into a {@link NibbleArray}) for performance reasons.
 *
 * @author DaPorkchop_
 */
public class HeapLegacyBlockStorage extends LegacyBlockStorage {
    protected final byte[] blocks;
    protected final int blocksOffset;
    protected final byte[] meta;
    protected final int metaOffset;

    protected final Handle<byte[]> blocksHandle;
    protected final ByteBuf blocksBuf;
    protected final Handle<byte[]> metaHandle;
    protected final ByteBuf metaBuf;

    public HeapLegacyBlockStorage(@NonNull BlockRegistry blockRegistry) {
        this(blockRegistry, new byte[NUM_BLOCKS], 0, new byte[NibbleArray.PACKED_SIZE], 0);
    }

    public HeapLegacyBlockStorage(@NonNull BlockRegistry blockRegistry, @NonNull byte[] blocks, int blocksOffset, @NonNull byte[] meta, int metaOffset) {
        super(blockRegistry);
        checkRangeLen(blocks.length, blocksOffset, NUM_BLOCKS);
        checkRangeLen(meta.length, metaOffset, NibbleArray.PACKED_SIZE);

        this.blocks = blocks;
        this.blocksOffset = blocksOffset;
        this.blocksHandle = null;
        this.blocksBuf = null;

        this.meta = meta;
        this.metaOffset = metaOffset;
        this.metaHandle = null;
        this.metaBuf = null;
    }

    public HeapLegacyBlockStorage(@NonNull BlockRegistry blockRegistry, @NonNull Handle<byte[]> blocks, @NonNull Handle<byte[]> meta) {
        super(blockRegistry);
        checkRange(blocks instanceof ArrayHandle ? ((ArrayHandle) blocks).length() : blocks.get().length, 0, NUM_BLOCKS);
        checkRange(meta instanceof ArrayHandle ? ((ArrayHandle) meta).length() : meta.get().length, 0, NibbleArray.PACKED_SIZE);

        this.blocks = blocks.retain().get();
        this.blocksOffset = 0;
        this.blocksHandle = blocks;
        this.blocksBuf = null;

        this.meta = meta.retain().get();
        this.metaOffset = 0;
        this.metaHandle = meta;
        this.metaBuf = null;
    }

    public HeapLegacyBlockStorage(@NonNull BlockRegistry blockRegistry, @NonNull ByteBuf blocks, @NonNull ByteBuf meta) {
        super(blockRegistry);
        checkArg(blocks.hasArray(), "blocks buffer doesn't have an array!");
        checkArg(meta.hasArray(), "meta buffer doesn't have an array!");
        checkRangeLen(blocks.capacity(), blocks.readerIndex(), NUM_BLOCKS);
        checkRangeLen(meta.capacity(), meta.readerIndex(), NibbleArray.PACKED_SIZE);

        this.blocks = blocks.retain().array();
        this.blocksOffset = blocks.arrayOffset() + blocks.readerIndex();
        this.blocksHandle = null;
        this.blocksBuf = blocks;

        this.meta = meta.retain().array();
        this.metaOffset = meta.arrayOffset() + meta.readerIndex();
        this.metaHandle = null;
        this.metaBuf = meta;
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.blocks[this.blocksOffset + index(x, y, z)] & 0xFF;
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        int index = index(x, y, z);
        return NibbleArray.extractNibble(index, this.meta[this.metaOffset + (index >> 1)]);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        int index = index(x, y, z);
        return ((this.blocks[this.blocksOffset + index(x, y, z)] & 0xFF) << 4)
                | NibbleArray.extractNibble(index, this.meta[this.metaOffset + (index >> 1)]);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        checkArg(meta >= 0 && meta < 16, "nibble value must be in range 0-15");
        int index = index(x, y, z);
        this.meta[this.metaOffset + (index >> 1)] = (byte) NibbleArray.insertNibble(index, this.meta[this.metaOffset + (index >> 1)], meta);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        checkArg(runtimeId >= 0 && runtimeId < 4096, "runtime ID must be in range 0-4095");
        int index = index(x, y, z);
        this.blocks[this.blocksOffset + index] = (byte) (runtimeId >> 4);
        this.meta[this.metaOffset + (index >> 1)] = (byte) NibbleArray.insertNibble(index, this.meta[this.metaOffset + (index >> 1)], runtimeId & 0xF);
    }

    @Override
    public BlockStorage clone() {
        return new HeapLegacyBlockStorage(
                this.blockRegistry,
                Arrays.copyOfRange(this.blocks, this.blocksOffset, this.blocksOffset + NUM_BLOCKS), 0,
                Arrays.copyOfRange(this.meta, this.metaOffset, this.metaOffset + NibbleArray.PACKED_SIZE), 0);
    }

    @Override
    protected void doRelease() {
        if (this.blocksHandle != null) {
            this.blocksHandle.release();
        }
        if (this.blocksBuf != null) {
            this.blocksBuf.release();
        }
        if (this.metaHandle != null) {
            this.metaHandle.release();
        }
        if (this.metaBuf != null) {
            this.metaBuf.release();
        }
    }

    /**
     * Extension of {@link HeapLegacyBlockStorage} with support for Anvil's extended block IDs.
     *
     * @author DaPorkchop_
     */
    public static class Add extends HeapLegacyBlockStorage {
        protected final byte[] add;
        protected final int addOffset;

        protected final Handle<byte[]> addHandle;
        protected final ByteBuf addBuf;

        public Add(@NonNull BlockRegistry blockRegistry) {
            this(blockRegistry, new byte[NUM_BLOCKS], 0, new byte[NibbleArray.PACKED_SIZE], 0, new byte[NibbleArray.PACKED_SIZE], 0);
        }

        public Add(@NonNull BlockRegistry blockRegistry, @NonNull byte[] blocks, int blocksOffset, @NonNull byte[] meta, int metaOffset, @NonNull byte[] add, int addOffset) {
            super(blockRegistry, blocks, blocksOffset, meta, metaOffset);
            checkRangeLen(add.length, addOffset, NibbleArray.PACKED_SIZE);

            this.add = add;
            this.addOffset = notNegative(addOffset, "addOffset");
            this.addHandle = null;
            this.addBuf = null;
        }

        public Add(@NonNull BlockRegistry blockRegistry, @NonNull Handle<byte[]> blocks, @NonNull Handle<byte[]> meta, @NonNull Handle<byte[]> add) {
            super(blockRegistry, blocks, meta);
            checkRange(add instanceof ArrayHandle ? ((ArrayHandle) add).length() : add.get().length, 0, NibbleArray.PACKED_SIZE);

            this.add = add.retain().get();
            this.addOffset = 0;
            this.addHandle = add;
            this.addBuf = null;
        }

        public Add(@NonNull BlockRegistry blockRegistry, @NonNull ByteBuf blocks, @NonNull ByteBuf meta, @NonNull ByteBuf add) {
            super(blockRegistry, blocks, meta);
            checkArg(add.hasArray(), "add buffer doesn't have an array!");
            checkRangeLen(add.capacity(), add.readerIndex(), NibbleArray.PACKED_SIZE);

            this.add = add.retain().array();
            this.addOffset = add.arrayOffset() + add.readerIndex();
            this.addHandle = null;
            this.addBuf = add;
        }

        @Override
        public int getBlockLegacyId(int x, int y, int z) {
            int index = index(x, y, z);
            return (this.blocks[this.blocksOffset + index] & 0xFF)
                    | (NibbleArray.extractNibble(index, this.add[this.addOffset + (index >> 1)]) << 8);
        }

        @Override
        public int getBlockRuntimeId(int x, int y, int z) {
            int index = index(x, y, z);
            return ((this.blocks[this.blocksOffset + index(x, y, z)] & 0xFF) << 4)
                    | NibbleArray.extractNibble(index, this.meta[this.metaOffset + (index >> 1)])
                    | (NibbleArray.extractNibble(index, this.add[this.addOffset + (index >> 1)]) << 12);
        }

        @Override
        public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
            checkArg(runtimeId >= 0 && runtimeId < 65536, "runtime ID must be in range 0-4095");
            int index = index(x, y, z);
            this.blocks[this.blocksOffset + index] = (byte) (runtimeId >> 4);
            this.meta[this.metaOffset + (index >> 1)] = (byte) NibbleArray.insertNibble(index, this.meta[this.metaOffset + (index >> 1)], runtimeId & 0xF);
            this.add[this.addOffset + (index >> 1)] = (byte) NibbleArray.insertNibble(index, this.add[this.addOffset + (index >> 1)], (runtimeId >> 12) & 0xF);
        }

        @Override
        public BlockStorage clone() {
            return new Add(
                    this.blockRegistry,
                    Arrays.copyOfRange(this.blocks, this.blocksOffset, this.blocksOffset + NUM_BLOCKS), 0,
                    Arrays.copyOfRange(this.meta, this.metaOffset, this.metaOffset + NibbleArray.PACKED_SIZE), 0,
                    Arrays.copyOfRange(this.add, this.addOffset, this.addOffset + NibbleArray.PACKED_SIZE), 0);
        }

        @Override
        protected void doRelease() {
            super.doRelease();
            if (this.addHandle != null) {
                this.addHandle.release();
            }
            if (this.addBuf != null) {
                this.addBuf.release();
            }
        }
    }
}
