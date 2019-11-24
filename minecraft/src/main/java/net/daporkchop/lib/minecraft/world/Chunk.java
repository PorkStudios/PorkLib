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
import net.daporkchop.lib.minecraft.util.BlockAccess;
import net.daporkchop.lib.minecraft.util.world.Dirtiable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/**
 * A vertical column of 16³ {@link Section}s.
 * <p>
 * In vanilla Minecraft, a chunk consists of 16 {@link Section}s indexed from 0-15.
 *
 * @author DaPorkchop_
 */
public interface Chunk extends BlockAccess, IntVector2.AddressableXZ, Dirtiable, Closeable {
    Vec2i pos();

    World world();

    Section section(int y);

    void section(int y, Section section);

    boolean exists();

    boolean loaded();

    void load();

    default boolean load(boolean generate) {
        if (this.exists() || generate) {
            this.load();
            return true;
        }

        return false;
    }

    void save();

    void unload();

    @Override
    default void close() throws IOException {
        if (this.loaded()) {
            this.unload();
        }
    }

    @Override
    default int getBlockId(int x, int y, int z) {
        Section section = this.section(y >> 4);
        if (section == null) {
            if (y == 0) {
                return 0;
            }
            return 0;
        } else {
            return section.getBlockId(x, y & 0xF, z);
        }
    }

    @Override
    default int getBlockMeta(int x, int y, int z) {
        Section section = this.section(y >> 4);
        if (section == null) {
            return 0;
        } else {
            return section.getBlockMeta(x, y & 0xF, z);
        }
    }

    @Override
    default int getBlockLight(int x, int y, int z) {
        Section section = this.section(y >> 4);
        if (section == null) {
            return 0;
        } else {
            return section.getBlockLight(x, y & 0xF, z);
        }
    }

    @Override
    default int getSkyLight(int x, int y, int z) {
        Section section = this.section(y >> 4);
        if (section == null) {
            return 15;
        } else {
            return section.getSkyLight(x, y & 0xF, z);
        }
    }

    @Override
    default void setBlockId(int x, int y, int z, int id) {
        Section section = this.section(y >> 4);
        if (section == null) {
            if (id == 0) {
                return; //don't create new section if setting default
            }
            this.section(y >> 4, section = this.world().getSave().config().sectionFactory().create(y >> 4, this));
        }
        section.setBlockId(x, y & 0xF, z, id);
    }

    @Override
    default void setBlockMeta(int x, int y, int z, int meta) {
        Section section = this.section(y >> 4);
        if (section == null) {
            if (meta == 0) {
                return; //don't create new section if setting default
            }
            this.section(y >> 4, section = this.world().getSave().config().sectionFactory().create(y >> 4, this));
        }
        section.setBlockMeta(x, y & 0xF, z, meta);
    }

    @Override
    default void setBlockLight(int x, int y, int z, int level) {
        Section section = this.section(y >> 4);
        if (section == null) {
            if (level == 0) {
                return; //don't create new section if setting default
            }
            this.section(y >> 4, section = this.world().getSave().config().sectionFactory().create(y >> 4, this));
        }
        section.setBlockLight(x, y & 0xF, z, level);
    }

    @Override
    default void setSkyLight(int x, int y, int z, int level) {
        Section section = this.section(y >> 4);
        if (section == null) {
            if (level == 15) {
                return; //don't create new section if setting default
            }
            this.section(y >> 4, section = this.world().getSave().config().sectionFactory().create(y >> 4, this));
        }
        section.setSkyLight(x, y & 0xF, z, level);
    }

    default int getHighestBlock(int x, int z) {
        if (!this.loaded()) {
            return -1;
        }
        for (int y = 255; y >= 0; y--) {
            if (this.getBlockId(x, y, z) != 0) {
                return y;
            }
        }
        return 0;
    }

    Collection<TileEntity> tileEntities();

    @Override
    default int getX() {
        return this.pos().getX();
    }

    @Override
    default int getZ() {
        return this.pos().getY();
    }

    @Override
    default int minX() {
        return this.pos().getX() << 4;
    }

    @Override
    default int minZ() {
        return this.pos().getY() << 4;
    }

    @Override
    default int maxX() {
        return (this.pos().getX() << 4) + 16;
    }

    @Override
    default int maxZ() {
        return (this.pos().getY() << 4) + 16;
    }

    /**
     * A vanilla Minecraft chunk.
     *
     * @author DaPorkchop_
     */
    interface Vanilla extends Chunk {
        @Override
        default int minY() {
            return 0;
        }

        @Override
        default int maxY() {
            return 256;
        }
    }
}
