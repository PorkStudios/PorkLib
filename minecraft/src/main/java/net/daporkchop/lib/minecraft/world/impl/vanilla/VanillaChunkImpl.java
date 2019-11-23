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

package net.daporkchop.lib.minecraft.world.impl.vanilla;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * An implementation of a Chunk for vanilla Minecraft.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class VanillaChunkImpl implements Chunk.Vanilla {
    @NonNull
    private final Vec2i pos;

    @NonNull
    private final World world;

    private final Collection<TileEntity> tileEntities = new ArrayDeque<>();
    private final Section[]              sections     = new Section[16];
    private volatile boolean dirty;
    private volatile boolean loaded;
    @Setter
    private byte[] heightMap;

    @Override
    public Section getSection(int y) {
        return this.sections[y];
    }

    @Override
    public void setSection(int y, Section section) {
        this.sections[y] = section;
    }

    @Override
    public boolean exists() {
        return this.loaded || this.world.getManager().hasColumn(this.pos.getX(), this.pos.getY());
    }

    @Override
    public void load() {
        synchronized (this) {
            if (!this.loaded) {
                this.loaded = true;
                this.world.getManager().loadColumn(this);
                if (this.heightMap == null) {
                    this.recalculateHeightMap();
                }
            }
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public void save() {
        synchronized (this) {
            if (this.dirty) {
                this.dirty = false;
                //TODO
            }
        }
    }

    @Override
    public void unload() {
        synchronized (this) {
            this.loaded = false;
            this.save();
            //this.world.getLoadedColumns().remove(this.pos);
            this.world.getLoadedTileEntities().values().removeAll(this.tileEntities);
            for (int y = 15; y >= 0; y--) {
                this.sections[y] = null;
            }
            this.heightMap = null;
            //TODO
        }
    }

    @Override
    public int getHighestBlock(int x, int z) {
        return this.heightMap[z << 4 | x] & 0xFF;
        //TODO: update heightmap
    }

    public void recalculateHeightMap() {
        if (this.heightMap == null) {
            this.heightMap = new byte[16 * 16];
        }
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                //call super since getHighestBlock implementation only reads from heightmap
                this.heightMap[z << 4 | x] = (byte) Vanilla.super.getHighestBlock(x, z);
            }
        }
    }
}
