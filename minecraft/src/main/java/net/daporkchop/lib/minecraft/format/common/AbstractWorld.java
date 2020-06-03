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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;
import net.daporkchop.lib.minecraft.world.SectionManager;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link World}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractWorld<S extends Save, O extends SaveOptions> extends AbstractRefCounted implements World {
    @NonNull
    protected final S parent;
    @NonNull
    protected final O options;
    @NonNull
    protected final Identifier id;

    protected BlockRegistry blockRegistry;
    protected SectionManager manager;
    protected WorldStorage storage;

    /**
     * Ensures that the implementation constructor has initialized all the required fields.
     */
    protected void validateState() {
        checkState(this.blockRegistry != null, "blockRegistry must be set!");
        checkState(this.manager != null, "manager must be set!");
        checkState(this.storage != null, "storage must be set!");
    }

    @Override
    public World retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.manager.release();
        this.storage.release();
    }

    //
    //
    // BlockAccess methods
    //
    //

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockState(x & 0xF, y, z & 0xF);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockState(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockId(x & 0xF, y, z & 0xF);
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockId(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLegacyId(x & 0xF, y, z & 0xF);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLegacyId(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockMeta(x & 0xF, y, z & 0xF);
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockMeta(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockRuntimeId(x & 0xF, y, z & 0xF);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockRuntimeId(x & 0xF, y, z & 0xF, layer);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, layer, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, layer, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, legacyId, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y, z & 0xF, layer, legacyId, meta);
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockId(x & 0xF, y, z & 0xF, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockId(x & 0xF, y, z & 0xF, layer, id);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLegacyId(x & 0xF, y, z & 0xF, legacyId);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLegacyId(x & 0xF, y, z & 0xF, layer, legacyId);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockMeta(x & 0xF, y, z & 0xF, meta);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockMeta(x & 0xF, y, z & 0xF, layer, meta);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockRuntimeId(x & 0xF, y, z & 0xF, runtimeId);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockRuntimeId(x & 0xF, y, z & 0xF, layer, runtimeId);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLight(x & 0xF, y, z & 0xF);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getSkyLight(x & 0xF, y, z & 0xF);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLight(x & 0xF, y, z & 0xF, level);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        this.manager.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setSkyLight(x & 0xF, y, z & 0xF, level);
    }
}
