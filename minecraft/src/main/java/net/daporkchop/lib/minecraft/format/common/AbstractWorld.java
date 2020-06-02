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
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base implementation of {@link World}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractWorld<S extends Save, O extends SaveOptions> extends AbstractRefCounted implements World {
    protected final S parent;
    protected final O options;
    protected final Identifier id;
    protected final BlockRegistry blockRegistry;

    protected final ChunkManager chunkManager;
    protected final WorldStorage storage;

    public AbstractWorld(@NonNull S parent, @NonNull O options, @NonNull Identifier id) {
        this.parent = parent;
        this.options = options;

        this.id = id;

        this.blockRegistry = this.getBlockRegistry0();
        this.storage = this.getStorage0();
        this.chunkManager = this.getChunkManager0();
    }

    protected abstract BlockRegistry getBlockRegistry0();

    protected abstract WorldStorage getStorage0();

    protected ChunkManager getChunkManager0() {
        return new ChunkManager(this, this.options.ioExecutor());
    }

    @Override
    public Collection<Chunk> loadedChunks() {
        return this.chunkManager.loadedChunks().collect(Collectors.toList());
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return this.chunkManager.getChunk(x, z);
    }

    @Override
    public Chunk getOrLoadChunk(int x, int z) {
        return this.chunkManager.getOrLoadChunk(x, z);
    }

    @Override
    public PFuture<Chunk> loadChunk(int x, int z) {
        return this.chunkManager.loadChunk(x, z);
    }

    @Override
    public void unloadSomeChunks() {
        this.chunkManager.unloadSomeChunks();
    }

    @Override
    public void unloadAllChunks() {
        this.chunkManager.unloadAllChunks();
    }

    @Override
    public World retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.storage.release();
    }

    //
    //
    // BlockAccess methods
    //
    //

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockState(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockState(x & 0xF, y & 0xF, z & 0xF, layer);
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockId(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockId(x & 0xF, y & 0xF, z & 0xF, layer);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLegacyId(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLegacyId(x & 0xF, y & 0xF, z & 0xF, layer);
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockMeta(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockMeta(x & 0xF, y & 0xF, z & 0xF, layer);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockRuntimeId(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockRuntimeId(x & 0xF, y & 0xF, z & 0xF, layer);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, layer, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, layer, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, legacyId, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockState(x & 0xF, y & 0xF, z & 0xF, layer, legacyId, meta);
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockId(x & 0xF, y & 0xF, z & 0xF, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockId(x & 0xF, y & 0xF, z & 0xF, layer, id);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLegacyId(x & 0xF, y & 0xF, z & 0xF, legacyId);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLegacyId(x & 0xF, y & 0xF, z & 0xF, layer, legacyId);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockMeta(x & 0xF, y & 0xF, z & 0xF, meta);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockMeta(x & 0xF, y & 0xF, z & 0xF, layer, meta);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockRuntimeId(x & 0xF, y & 0xF, z & 0xF, runtimeId);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockRuntimeId(x & 0xF, y & 0xF, z & 0xF, layer, runtimeId);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getBlockLight(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).getSkyLight(x & 0xF, y & 0xF, z & 0xF);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setBlockLight(x & 0xF, y & 0xF, z & 0xF, level);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        this.getOrLoadChunk(x >> 4, z >> 4).getOrLoadSection(y >> 4).setSkyLight(x & 0xF, y & 0xF, z & 0xF, level);
    }
}
