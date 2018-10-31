/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

import java.io.Closeable;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public interface Column extends Closeable, IntVector2.AddressableXZ {
    Vec2i getPos();

    World getWorld();

    Chunk getChunk(int y);

    void setChunk(int y, Chunk chunk);

    boolean exists();

    boolean isLoaded();

    void load();

    default void load(boolean generate) {
        if (this.exists() || generate)  {
            this.load();
        }
    }

    boolean isDirty();

    void markDirty();

    void save();

    void unload();

    @Override
    default void close() throws IOException {
        if (this.isLoaded())    {
            this.unload();
        }
    }

    default int getBlockId(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            return 0;
        } else {
            return chunk.getBlockId(x, y & 0xF, z);
        }
    }

    default int getBlockMeta(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            return 0;
        } else {
            return chunk.getBlockMeta(x, y & 0xF, z);
        }
    }

    default int getBlockLight(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            return 0;
        } else {
            return chunk.getBlockLight(x, y & 0xF, z);
        }
    }

    default int getSkyLight(int x, int y, int z) {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            return 15;
        } else {
            return chunk.getSkyLight(x, y & 0xF, z);
        }
    }

    default void setBlockId(int x, int y, int z, int id)    {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            if (id == 0)    {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockId(x, y & 0xF, z, id);
    }

    default void setBlockMeta(int x, int y, int z, int meta)    {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            if (meta == 0)    {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockMeta(x, y & 0xF, z, meta);
    }

    default void setBlockLight(int x, int y, int z, int level)    {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            if (level == 0)    {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setBlockLight(x, y & 0xF, z, level);
    }

    default void setSkyLight(int x, int y, int z, int level)    {
        Chunk chunk = this.getChunk(y >> 4);
        if (chunk == null)  {
            if (level == 15)    {
                return; //don't create new chunk if setting default
            }
            this.setChunk(y >> 4, chunk = this.getWorld().getSave().getInitFunctions().getChunkCreator().apply(y >> 4, this));
        }
        chunk.setSkyLight(x, y & 0xF, z, level);
    }
}
