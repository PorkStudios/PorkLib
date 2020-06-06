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

package net.daporkchop.lib.minecraft.format.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.minecraft.world.Chunk.*;

/**
 * Base implementation of {@link Chunk}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractChunk extends AbstractRefCounted implements Chunk {
    protected final World parent;
    protected final int x;
    protected final int z;
    @Getter(AccessLevel.NONE)
    protected PFuture<Chunk> selfFuture;
    protected final BlockRegistry blockRegistry;
    protected final boolean hasSkyLight;

    public AbstractChunk(@NonNull World parent, int x, int z) {
        this.parent = parent;
        this.x = x;
        this.z = z;

        this.blockRegistry = parent.blockRegistry();
        this.hasSkyLight = parent.hasSkyLight();
    }

    @Override
    public PFuture<Chunk> selfFuture() {
        PFuture<Chunk> selfFuture = this.selfFuture;
        return selfFuture == null ? this.selfFuture = PFutures.successful(this) : selfFuture;
    }

    @Override
    public Chunk retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.parent.release();
    }

    //
    //
    // BlockAccess methods
    //
    //

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockState(x, y & 0xF, z);
        }
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockState(x, y & 0xF, z, layer);
        }
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockId(x, y & 0xF, z);
        }
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockId(x, y & 0xF, z, layer);
        }
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockLegacyId(x, y & 0xF, z);
        }
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockLegacyId(x, y & 0xF, z, layer);
        }
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockMeta(x, y & 0xF, z);
        }
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockMeta(x, y & 0xF, z, layer);
        }
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockRuntimeId(x, y & 0xF, z);
        }
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockRuntimeId(x, y & 0xF, z, layer);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, state);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, layer, state);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, id, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, layer, id, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, legacyId, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockState(x, y & 0xF, z, layer, legacyId, meta);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockId(x, y & 0xF, z, id);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockId(x, y & 0xF, z, layer, id);
        }
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockLegacyId(x, y & 0xF, z, legacyId);
        }
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockLegacyId(x, y & 0xF, z, layer, legacyId);
        }
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockMeta(x, y & 0xF, z, meta);
        }
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockMeta(x, y & 0xF, z, layer, meta);
        }
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockRuntimeId(x, y & 0xF, z, runtimeId);
        }
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockRuntimeId(x, y & 0xF, z, layer, runtimeId);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getBlockLight(x, y & 0xF, z);
        }
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            return section.getSkyLight(x, y & 0xF, z);
        }
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setBlockLight(x, y & 0xF, z, level);
        }
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        checkCoords(x, z);
        try (Section section = this.getOrLoadSection(y >> 4)) {
            section.setSkyLight(x, y & 0xF, z, level);
        }
    }
}
