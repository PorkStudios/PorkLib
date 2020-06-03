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

package net.daporkchop.lib.minecraft.format.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base implementation of {@link Chunk}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public abstract class AbstractChunk extends AbstractRefCounted implements Chunk {
    @Getter
    protected PFuture<Chunk> selfFuture = PFutures.successful(this);

    @Override
    public Chunk retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
    }

    //
    //
    // BlockAccess methods
    //
    //

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.getSection();
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return null;
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {

    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {

    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {

    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {

    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {

    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {

    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {

    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {

    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {

    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {

    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {

    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {

    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {

    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {

    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {

    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {

    }
}
