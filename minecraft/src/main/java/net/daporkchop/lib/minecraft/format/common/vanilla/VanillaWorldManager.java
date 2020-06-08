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

package net.daporkchop.lib.minecraft.format.common.vanilla;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldManager;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.primitive.map.LongObjMap;
import net.daporkchop.lib.primitive.map.concurrent.LongObjConcurrentHashMap;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.LongFunction;
import java.util.stream.Stream;

/**
 * Implementation of {@link WorldManager} which
 *
 * @author DaPorkchop_
 */
//TODO: this needs to be redone to conform to the new atomicity requirements in WorldManager
@RequiredArgsConstructor
public class VanillaWorldManager extends AbstractRefCounted implements WorldManager {
    @NonNull
    protected final World world;
    @NonNull
    protected final WorldStorage storage;
    @NonNull
    protected final Executor ioExecutor;

    protected final LongObjMap<PFuture<Chunk>> chunks = new LongObjConcurrentHashMap<>();
    protected final LongFunction<PFuture<Chunk>> computeChunkFunction = this::computeChunk0;

    protected PFuture<Chunk> computeChunk0(long key) {
        return PFutures.computeThrowableAsync(() -> this.storage.loadChunk(this.world, BinMath.unpackX(key), BinMath.unpackY(key)), this.ioExecutor);
    }

    @Override
    public Stream<Chunk> loadedChunks() {
        return this.chunks.values().stream()
                .map(PFuture::getNow)
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<Section> loadedSections() {
        /*return this.loadedChunks()
                .flatMap(Chunk::loadedSections);*/
        return null;
    }

    @Override
    public Stream<Section> loadedSections(@NonNull Chunk chunk) {
        return null;
    }

    @Override
    public Chunk getChunk(int x, int z) {
        PFuture<Chunk> future = this.chunks.get(BinMath.packXY(x, z));
        return future != null ? future.getNow().retain() : null;
    }

    @Override
    public Chunk getOrLoadChunk(int x, int z) {
        return this.chunks.computeIfAbsent(BinMath.packXY(x, z), this.computeChunkFunction).join().retain();
    }

    @Override
    public PFuture<Chunk> loadChunk(int x, int z) {
        return this.chunks.computeIfAbsent(BinMath.packXY(x, z), this.computeChunkFunction);
    }

    @Override
    public Section getSection(int x, int y, int z) {
        if (y < 0 || y >= 16)   {
            return null;
        }
        Chunk chunk = this.getChunk(x, z);
        return chunk != null ? chunk.getSection(y) : null;
    }

    @Override
    public Section getOrLoadSection(int x, int y, int z) {
        if (y < 0 || y >= 16)   {
            return null;
        }
        return this.getOrLoadChunk(x, z).getSection(y);
    }

    @Override
    public PFuture<Section> loadSection(int x, int y, int z) {
        if (y < 0 || y >= 16)   {
            return PFutures.successful(null, this.ioExecutor);
        }
        return this.loadChunk(x, z).thenApply(chunk -> chunk.getSection(y)); //yeah, this is inefficient :(
    }

    @Override
    public void gc(boolean full) {
        this.ensureNotReleased();
        if (full) {
            this.fullGc();
        } else {
            this.partialGc();
        }
    }

    protected void partialGc() {
        //TODO
    }

    protected void fullGc() {
        //TODO
    }

    @Override
    public WorldManager retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.fullGc();
        this.storage.release();
    }
}
