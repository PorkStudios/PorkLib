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
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.World;

/**
 * Base implementation of {@link net.daporkchop.lib.minecraft.world.Chunk.Vanilla}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractVanillaChunk implements Chunk.Vanilla {
    @NonNull
    protected final World world;

    @Setter
    protected byte[] heightMap;

    protected volatile boolean dirty;
    protected volatile boolean loaded;

    @Override
    public boolean exists() {
        return this.loaded || this.world.manager().hasColumn(this.getX(), this.getZ());
    }

    @Override
    public boolean markDirty() {
        boolean old = this.dirty;
        this.dirty = true;
        return old;
    }

    @Override
    public synchronized void load() {
        if (!this.loaded) {
            this.loaded = true;
            this.world.manager().loadColumn(this);
            if (this.heightMap == null) {
                this.recalculateHeightMap();
            }
            this.doLoad();
        }
    }

    protected void doLoad() {
    }

    @Override
    public synchronized void save() {
        if (this.dirty) {
            this.dirty = false;
            //TODO
        }
    }

    @Override
    public synchronized void unload() {
        if (this.loaded) {
            this.loaded = false;
            this.save();

            this.world.loadedColumns().remove(this.pos());
            this.heightMap = null;

            this.doUnload();
        }
    }

    protected void doUnload()   {
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
                this.heightMap[z << 4 | x] = (byte) Chunk.Vanilla.super.getHighestBlock(x, z);
            }
        }
    }
}
