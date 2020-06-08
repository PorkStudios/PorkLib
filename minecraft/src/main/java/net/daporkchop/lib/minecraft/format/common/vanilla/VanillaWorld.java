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

package net.daporkchop.lib.minecraft.format.common.vanilla;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.format.common.AbstractWorld;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.Chunk;

/**
 * Base implementation of {@link net.daporkchop.lib.minecraft.world.World} for vanilla chunks with exactly 16 sections.
 *
 * @author DaPorkchop_
 */
public abstract class VanillaWorld<S extends Save, O extends SaveOptions> extends AbstractWorld<S, O> {
    public VanillaWorld(S parent, O options, Identifier id) {
        super(parent, options, id);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockState(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockState(x & 0xF, y, z & 0xF, layer);
        }
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockId(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockId(x & 0xF, y, z & 0xF, layer);
        }
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockLegacyId(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockLegacyId(x & 0xF, y, z & 0xF, layer);
        }
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockMeta(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockMeta(x & 0xF, y, z & 0xF, layer);
        }
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockRuntimeId(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockRuntimeId(x & 0xF, y, z & 0xF, layer);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, state);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, layer, state);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, id, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, layer, id, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, legacyId, meta);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockState(x & 0xF, y, z & 0xF, layer, legacyId, meta);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockId(x & 0xF, y, z & 0xF, id);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockId(x & 0xF, y, z & 0xF, layer, id);
        }
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockLegacyId(x & 0xF, y, z & 0xF, legacyId);
        }
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockLegacyId(x & 0xF, y, z & 0xF, layer, legacyId);
        }
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockMeta(x & 0xF, y, z & 0xF, meta);
        }
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockMeta(x & 0xF, y, z & 0xF, meta);
        }
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockRuntimeId(x & 0xF, y, z & 0xF, runtimeId);
        }
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockRuntimeId(x & 0xF, y, z & 0xF, layer, runtimeId);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getBlockLight(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            return chunk.getSkyLight(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setBlockLight(x & 0xF, y, z & 0xF, level);
        }
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        try (Chunk chunk = this.manager.getOrLoadChunk(x >> 4, z >> 4)) {
            chunk.setSkyLight(x & 0xF, y, z & 0xF, level);
        }
    }
}
