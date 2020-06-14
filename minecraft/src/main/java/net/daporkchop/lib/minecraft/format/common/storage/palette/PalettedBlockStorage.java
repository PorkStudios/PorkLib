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

package net.daporkchop.lib.minecraft.format.common.storage.palette;

import lombok.NonNull;
import net.daporkchop.lib.binary.bit.BitArray;
import net.daporkchop.lib.binary.bit.packed.PackedBitArray;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.format.common.storage.legacy.LegacyBlockStorage;
import net.daporkchop.lib.minecraft.util.palette.ArrayPalette;
import net.daporkchop.lib.minecraft.util.palette.IdentityPalette;
import net.daporkchop.lib.minecraft.util.palette.Palette;

import java.util.function.IntBinaryOperator;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BlockStorage} which uses a palette.
 *
 * @author DaPorkchop_
 */
public class PalettedBlockStorage extends LegacyBlockStorage implements IntBinaryOperator {
    protected final ArrayAllocator<long[]> alloc;
    protected BitArray array;
    protected Palette palette;
    protected int bits;

    public PalettedBlockStorage(@NonNull BlockRegistry blockRegistry) {
        this(blockRegistry, null);
    }

    public PalettedBlockStorage(@NonNull BlockRegistry blockRegistry, ArrayAllocator<long[]> alloc) {
        super(blockRegistry);

        this.alloc = alloc;
        this.setBits(4);
    }

    public PalettedBlockStorage(@NonNull BlockRegistry blockRegistry, @NonNull BitArray array, @NonNull Palette palette) {
        this(blockRegistry, null, array, palette);
    }

    public PalettedBlockStorage(@NonNull BlockRegistry blockRegistry, ArrayAllocator<long[]> alloc, @NonNull BitArray array, @NonNull Palette palette) {
        super(blockRegistry);

        this.alloc = alloc;
        checkArg(array.size() >= 4096, "array (%d) must be at least 4096 entries!", array.size());
        this.array = array;
        this.palette = palette;
    }

    protected void setBits(int bits) {
        if (bits == this.bits) {
            return;
        }

        if (bits <= 4) {
            this.palette = new ArrayPalette(this, this.bits = 4);
        } else if (bits < 9) {
            this.palette = new ArrayPalette(this, this.bits = bits); //vanilla uses a hashmap for this, i doubt it's much faster though...
        } else {
            this.bits = BinMath.getNumBitsNeededFor(this.blockRegistry.maxRuntimeId() + 1);
            this.palette = IdentityPalette.INSTANCE;
        }

        this.palette.get(0);
        this.array = this.alloc != null
                     ? new PackedBitArray(this.bits, 4096, this.alloc.atLeast(4096))
                     : new PackedBitArray(this.bits, 4096);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.blockRegistry.getState(this.getBlockRuntimeId(x, y, z)).legacyId();
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.blockRegistry.getState(this.getBlockRuntimeId(x, y, z)).meta();
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return this.getBlockRuntimeId(index(x, y, z));
    }

    protected int getBlockRuntimeId(int index) {
        return Math.max(this.palette.getReverse(this.array.get(index)), 0);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.setBlockRuntimeId(x, y, z, this.blockRegistry.getState(this.getBlockRuntimeId(x, y, z)).withMeta(meta).runtimeId());
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        this.setBlockRuntimeId(index(x, y, z), runtimeId);
    }

    protected void setBlockRuntimeId(int index, int runtimeId) {
        int paletteId = this.palette.get(runtimeId);
        this.array.set(index, paletteId); //this prevents this.array from being loaded first, in case it gets modified
    }

    /**
     * resizes this block storage
     * <p>
     * internal API, do not touch!
     */
    @Override
    @Deprecated
    public int applyAsInt(int nextBits, int nextValue) {
        BitArray array = this.array;
        Palette palette = this.palette;
        this.setBits(nextBits);

        //copy values
        for (int i = 0; i < 4096; i++) {
            this.setBlockRuntimeId(i, Math.max(palette.get(array.get(i)), 0));
        }
        array.release();

        return this.palette.get(nextValue);
    }

    @Override
    public BlockStorage clone() {
        return null;
    }

    @Override
    protected void doRelease() {
        if (this.array != null) {
            this.array.release();
        }
    }
}
