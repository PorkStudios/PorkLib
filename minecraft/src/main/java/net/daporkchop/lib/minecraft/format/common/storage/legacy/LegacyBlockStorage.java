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

package net.daporkchop.lib.minecraft.format.common.storage.legacy;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.common.storage.AbstractBlockStorage;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.block.BlockRegistry;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link BlockStorage} for the legacy block format used in Anvil chunk sections prior to The Flatting™️.
 *
 * @author DaPorkchop_
 */
public abstract class LegacyBlockStorage extends AbstractBlockStorage {
    protected static int index(int x, int y, int z) {
        BlockStorage.checkCoords(x, y, z);
        return (y << 8) | (z << 4) | x;
    }

    public LegacyBlockStorage(@NonNull BlockRegistry blockRegistry) {
        super(blockRegistry);
    }

    @Override
    public int layers() {
        return 1;
    }

    @Override
    public abstract int getBlockLegacyId(int x, int y, int z);

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        return this.getBlockLegacyId(x, y, z);
    }

    @Override
    public abstract int getBlockMeta(int x, int y, int z);

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        return this.getBlockMeta(x, y, z);
    }

    @Override
    public abstract int getBlockRuntimeId(int x, int y, int z);

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        return this.getBlockRuntimeId(x, y, z);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.setBlockRuntimeId(x, y, z, legacyId << 4);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        this.setBlockRuntimeId(x, y, z, legacyId << 4);
    }

    @Override
    public abstract void setBlockMeta(int x, int y, int z, int meta);

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        this.setBlockMeta(x, y, z, meta);
    }

    @Override
    public abstract void setBlockRuntimeId(int x, int y, int z, int runtimeId);

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        checkIndex(layer == 0, "non-zero layer index (%d)", layer);
        this.setBlockRuntimeId(x, y, z, runtimeId);
    }
}
