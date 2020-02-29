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

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.BlockAccess;
import net.daporkchop.lib.minecraft.world.format.WorldManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public interface World extends BlockAccess, AutoCloseable {
    int dimension();

    MinecraftSave getSave();

    WorldManager manager();

    Map<Vec2i, Chunk> loadedColumns();

    Chunk column(int x, int z);

    Chunk columnOrNull(int x, int z);

    Map<Vec3i, TileEntity> loadedTileEntities();

    default TileEntity tileEntity(int x, int y, int z) {
        return this.loadedTileEntities().get(new Vec3i(x, y, z));
    }

    void save();

    @Override
    void close() throws IOException;

    @Override
    default int getBlockId(int x, int y, int z) {
        Chunk col = this.columnOrNull(x >> 4, z >> 4);
        if (col == null) {
            return 0;
        } else {
            return col.getBlockId(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    default int getBlockMeta(int x, int y, int z) {
        Chunk col = this.columnOrNull(x >> 4, z >> 4);
        if (col == null) {
            return 0;
        } else {
            return col.getBlockMeta(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    default int getBlockLight(int x, int y, int z) {
        Chunk col = this.columnOrNull(x >> 4, z >> 4);
        if (col == null) {
            return 0;
        } else {
            return col.getBlockLight(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    default int getSkyLight(int x, int y, int z) {
        Chunk col = this.columnOrNull(x >> 4, z >> 4);
        if (col == null) {
            return 0;
        } else {
            return col.getSkyLight(x & 0xF, y, z & 0xF);
        }
    }

    @Override
    default void setBlockId(int x, int y, int z, int id) {
        Chunk col = this.column(x >> 4, z >> 4);
        if (!col.loaded()) {
            col.load();
        }
        col.setBlockId(x & 0xF, y, z & 0xF, id);
    }

    @Override
    default void setBlockMeta(int x, int y, int z, int meta) {
        Chunk col = this.column(x >> 4, z >> 4);
        if (!col.loaded()) {
            col.load();
        }
        col.setBlockMeta(x & 0xF, y, z & 0xF, meta);
    }

    @Override
    default void setBlockLight(int x, int y, int z, int level) {
        Chunk col = this.column(x >> 4, z >> 4);
        if (!col.loaded()) {
            col.load();
        }
        col.setBlockLight(x & 0xF, y, z & 0xF, level);
    }

    @Override
    default void setSkyLight(int x, int y, int z, int level) {
        Chunk col = this.column(x >> 4, z >> 4);
        if (!col.loaded()) {
            col.load();
        }
        col.setSkyLight(x & 0xF, y, z & 0xF, level);
    }

    @Override
    default int getHighestBlock(int x, int z) {
        Chunk col = this.columnOrNull(x >> 4, z >> 4);
        if (col == null) {
            return -1;
        } else {
            return col.getHighestBlock(x & 0xF, z & 0xF);
        }
    }
}
