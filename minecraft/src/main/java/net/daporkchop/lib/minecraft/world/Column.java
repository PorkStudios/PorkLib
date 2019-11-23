/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.math.vector.i.IntVector2;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.BlockDataAccess;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
//TODO: rename to Chunk
public interface Column extends BlockDataAccess, IntVector2.AddressableXZ, Closeable {
    Vec2i getPos();

    World getWorld();

    Chunk getChunk(int y);

    void setChunk(int y, Chunk chunk);

    boolean exists();

    boolean isLoaded();

    void load();

    default boolean load(boolean generate) {
        if (this.exists() || generate) {
            this.load();
            return true;
        }

        return false;
    }

    boolean isDirty();

    void markDirty();

    void save();

    void unload();

    @Override
    default void close() throws IOException {
        if (this.isLoaded()) {
            this.unload();
        }
    }

    @Override
    default int getBlockId(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            if (y == 0) {
                return 0;
            }
            return 0;
        } else {
            return chunk.getBlockId(x, y & 0xF, z);
        }
    }

    @Override
    default int getBlockMeta(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            return 0;
        } else {
            return chunk.getBlockMeta(x, y & 0xF, z);
        }
    }

    @Override
    default int getBlockLight(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            return 0;
        } else {
            return chunk.getBlockLight(x, y & 0xF, z);
        }
    }

    @Override
    default int getSkyLight(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            return 15;
        } else {
            return chunk.getSkyLight(x, y & 0xF, z);
        }
    }

    @Override
    default void setBlockId(int x, int y, int z, int id) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            if (id == 0) {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockId(x, y & 0xF, z, id);
    }

    @Override
    default void setBlockMeta(int x, int y, int z, int meta) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            if (meta == 0) {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockMeta(x, y & 0xF, z, meta);
    }

    @Override
    default void setBlockLight(int x, int y, int z, int level) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            if (level == 0) {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockLight(x, y & 0xF, z, level);
    }

    @Override
    default void setSkyLight(int x, int y, int z, int level) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null) {
            if (level == 15) {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setSkyLight(x, y & 0xF, z, level);
    }

    default int getHighestBlock(int x, int z) {
        if (!this.isLoaded()) {
            return -1;
        }
        for (int y = 255; y >= 0; y--) {
            if (this.getBlockId(x, y, z) != 0) {
                return y;
            }
        }
        return 0;
    }

    Collection<TileEntity> getTileEntities();
}
