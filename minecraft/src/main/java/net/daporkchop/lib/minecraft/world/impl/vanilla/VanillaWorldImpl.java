/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class VanillaWorldImpl implements World {
    private final int dimension;
    @NonNull
    private final WorldManager manager;
    @NonNull
    @Accessors(fluent = false)
    private final MinecraftSave save;

    public VanillaWorldImpl(int dimension, @NonNull WorldManager manager, @NonNull MinecraftSave save) {
        this.dimension = dimension;
        this.manager = manager;
        this.save = save;

        manager.setWorld(this);
    }

    @Override
    public Chunk column(int x, int z) {
        return VanillaWorldImpl.this.save.config().chunkFactory().create(new Vec2i(x, z), this);
        //return this.loadedColumns.computeIfAbsent(new Vec2i(x, z), pos -> this.save.config().getChunkFactory().apply(addr, this));
    }

    @Override
    public Chunk columnOrNull(int x, int z) {
        throw new UnsupportedOperationException();
        //return this.loadedColumns.get(new Vec2i(x, z));
    }

    @Override
    public void save() {
        //TODO
    }

    @Override
    public void close() throws IOException {
        this.save();
        //this.loadedColumns.values().forEach(Chunk::unload);
        this.manager.close();
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getHighestBlock(int x, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int minX() {
        return -30_000_000;
    }

    @Override
    public int minY() {
        return 0;
    }

    @Override
    public int minZ() {
        return -30_000_000;
    }

    @Override
    public int maxX() {
        return 30_000_000;
    }

    @Override
    public int maxY() {
        return 256;
    }

    @Override
    public int maxZ() {
        return 30_000_000;
    }
}
