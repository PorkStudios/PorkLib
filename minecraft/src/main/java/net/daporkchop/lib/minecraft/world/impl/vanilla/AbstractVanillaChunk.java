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

package net.daporkchop.lib.minecraft.world.impl.vanilla;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.World;

import java.util.Arrays;

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

    protected int[] heightMap;
    protected ArrayHandle<int[]> heightMapHandle;

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

    protected void doUnload() {
        this.heightMap = null;
        if (this.heightMapHandle != null) {
            this.heightMapHandle.release();
            this.heightMapHandle = null;
        }
    }

    @Override
    public int getHighestBlock(int x, int z) {
        return this.heightMap[z << 4 | x];
        //TODO: update heightmap
    }

    public void recalculateHeightMap() {
        if (this.heightMap == null) {
            this.heightMap(new int[16 * 16]);
            Arrays.fill(this.heightMap, -1);
        }
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                //call super since getHighestBlock implementation only reads from heightmap
                this.heightMap[z << 4 | x] = (byte) Chunk.Vanilla.super.getHighestBlock(x, z);
            }
        }
    }

    public void heightMap(int[] heightMap) {
        this.heightMap = heightMap;

        if (this.heightMapHandle != null) {
            this.heightMapHandle.release();
            this.heightMapHandle = null;
        }
    }

    public void heightMap(ArrayHandle<int[]> handle) {
        this.heightMap = null;
        if (this.heightMapHandle != null) {
            this.heightMapHandle.release();
            this.heightMapHandle = null;
        }

        if (handle != null) {
            this.heightMap = handle.retain().get();
            this.heightMapHandle = handle;
        }
    }
}
