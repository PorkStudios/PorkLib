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

package net.daporkchop.lib.minecraft.world.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * An implementation of a Column for vanilla Minecraft
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ColumnImpl implements Column {
    @NonNull
    private final Vec2i pos;

    @NonNull
    private final World world;

    @NonNull
    private final Collection<TileEntity> tileEntities = new ArrayDeque<>();
    private final Chunk[] chunks = new Chunk[16];
    private volatile boolean dirty;
    private volatile boolean loaded;

    @Override
    public Chunk getChunk(int y) {
        return this.chunks[y];
    }

    @Override
    public void setChunk(int y, Chunk chunk) {
        this.chunks[y] = chunk;
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
            this.world.getLoadedColumns().remove(this.pos);
            this.world.getLoadedTileEntities().values().removeAll(this.tileEntities);
            for (int y = 15; y >= 0; y--) {
                this.chunks[y] = null;
            }
            //TODO
        }
    }

    @Override
    public int getX() {
        return this.pos.getX();
    }

    @Override
    public int getZ() {
        return this.pos.getY();
    }
}
