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

import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.format.WorldManager;

import java.io.Closeable;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public interface World extends Closeable {
    int getId();

    MinecraftSave getSave();

    WorldManager getManager();

    Map<Vec2i, Column> getLoadedColumns();

    Column getColumn(int x, int z);

    Column getColumnOrNull(int x, int z);

    Map<Vec3i, TileEntity> getLoadedTileEntities();

    default TileEntity getTileEntity(int x, int y, int z)   {
        return this.getLoadedTileEntities().get(new Vec3i(x, y, z));
    }

    void save();

    default int getBlockId(int x, int y, int z) {
        Column col = this.getColumnOrNull(x >> 4, z >> 4);
        if (col == null)    {
            return 0;
        } else {
            return col.getBlockId(x & 0xF, y, z & 0xF);
        }
    }

    default int getBlockMeta(int x, int y, int z) {
        Column col = this.getColumnOrNull(x >> 4, z >> 4);
        if (col == null)    {
            return 0;
        } else {
            return col.getBlockMeta(x & 0xF, y, z & 0xF);
        }
    }

    default int getBlockLight(int x, int y, int z) {
        Column col = this.getColumnOrNull(x >> 4, z >> 4);
        if (col == null)    {
            return 0;
        } else {
            return col.getBlockLight(x & 0xF, y, z & 0xF);
        }
    }

    default int getSkyLight(int x, int y, int z) {
        Column col = this.getColumnOrNull(x >> 4, z >> 4);
        if (col == null)    {
            return 0;
        } else {
            return col.getSkyLight(x & 0xF, y, z & 0xF);
        }
    }

    default void setBlockId(int x, int y, int z, int id)    {
        Column col = this.getColumn(x >> 4, z >> 4);
        if (!col.isLoaded())    {
            col.load();
        }
        col.setBlockId(x & 0xF, y, z & 0xF, id);
    }

    default void setBlockMeta(int x, int y, int z, int meta)    {
        Column col = this.getColumn(x >> 4, z >> 4);
        if (!col.isLoaded())    {
            col.load();
        }
        col.setBlockMeta(x & 0xF, y, z & 0xF, meta);
    }

    default void setBlockLight(int x, int y, int z, int level)    {
        Column col = this.getColumn(x >> 4, z >> 4);
        if (!col.isLoaded())    {
            col.load();
        }
        col.setBlockLight(x & 0xF, y, z & 0xF, level);
    }

    default void setSkyLight(int x, int y, int z, int level)    {
        Column col = this.getColumn(x >> 4, z >> 4);
        if (!col.isLoaded())    {
            col.load();
        }
        col.setSkyLight(x & 0xF, y, z & 0xF, level);
    }

    default int getHighestBlock(int x, int z) {
        Column col = this.getColumnOrNull(x >> 4, z >> 4);
        if (col == null)    {
            return -1;
        } else {
            return col.getHighestBlock(x & 0xF, z & 0xF);
        }
    }
}
