/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2023 DaPorkchop_
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

package net.daporkchop.lib.minecraft.region;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.region.util.ChunkProcessor;
import net.daporkchop.lib.minecraft.region.util.NeighboringChunkProcessor;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilWorldManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class WorldScanner {
    @NonNull
    private final World world;

    private final Collection<ChunkProcessor> processors = new ArrayList<>();
    private final Collection<NeighboringChunkProcessor> processorsNeighboring = new ArrayList<>();

    public WorldScanner addProcessor(@NonNull Consumer<Chunk> processor) {
        this.processors.add((current, estimatedTotal, column) -> processor.accept(column));
        return this;
    }

    public WorldScanner addProcessor(@NonNull ChunkProcessor processor) {
        if (processor instanceof NeighboringChunkProcessor) {
            this.addProcessor((NeighboringChunkProcessor) processor);
        } else {
            this.processors.add(processor);
        }
        return this;
    }

    public WorldScanner addProcessor(@NonNull NeighboringChunkProcessor processor) {
        this.processorsNeighboring.add(processor);
        return this;
    }

    public WorldScanner clear() {
        this.processors.clear();
        return this;
    }

    public WorldScanner run() {
        return this.run(false);
    }

    public WorldScanner run(boolean parallel) {
        AtomicLong curr = new AtomicLong(0L);
        AtomicLong estimatedTotal = new AtomicLong(0L);
        WorldManager manager = this.world.manager();
        if (manager instanceof AnvilWorldManager) {
            AnvilWorldManager anvilWorldManager = (AnvilWorldManager) manager;
            Collection<Vec2i> regions = this.getRegionPositions(anvilWorldManager);
            estimatedTotal.set(regions.size() * (32L * 32L));
            Stream<Vec2i> regionStream = parallel ? regions.parallelStream() : regions.stream();

            ThreadLocal<BitSet> visitableChunksInRegionThreadLocal = ThreadLocal.withInitial(() -> new BitSet(32 * 32));
            if (!this.processorsNeighboring.isEmpty()) {
                ThreadLocal<BorderingWorld> worldThreadLocal = ThreadLocal.withInitial(() -> new BorderingWorld(this.world.dimension(), this.world.getSave(), this.world.manager()));
                regionStream.forEach(pos -> {
                    int xx = pos.getX() << 5;
                    int zz = pos.getY() << 5;

                    //compute mask indicating which chunks should be visited
                    BitSet visitableChunksMask = visitableChunksInRegionThreadLocal.get();
                    visitableChunksMask.set(0, 32 * 32);
                    this.maskVisitableChunksInRegion(pos.getX(), pos.getY(), xx, zz, visitableChunksMask);

                    if (visitableChunksMask.isEmpty()) { //no chunks are being visited, thus nothing needs to be loaded
                        estimatedTotal.addAndGet(-32L * 32L);
                        return;
                    }
                    //TODO: we could avoid loading some chunks if not all chunks are being visited

                    BorderingWorld world = worldThreadLocal.get();
                    world.offsetX = xx;
                    world.offsetZ = zz;
                    for (int x = -1; x <= 32; x++) {
                        for (int z = -1; z <= 32; z++) {
                            Chunk col = world.chunks[(x + 1) * 34 + z + 1] = this.world.column(xx + x, zz + z);
                            if (!col.load(false) && (x >= 0 && x <= 31 && z >= 0 && z <= 31)) {
                                estimatedTotal.decrementAndGet();
                            }
                        }
                    }
                    for (int x = 0; x < 32; x++) {
                        for (int z = 0; z < 32; z++) {
                            Chunk chunk = world.chunks[(x + 1) * 34 + z + 1];
                            if (!visitableChunksMask.get(x * 32 + z) || !chunk.loaded()) {
                                continue;
                            }
                            long current = curr.getAndIncrement();
                            for (ChunkProcessor processor : this.processors) {
                                processor.handle(current, estimatedTotal.get(), chunk);
                            }
                            for (NeighboringChunkProcessor processor : this.processorsNeighboring) {
                                processor.handle(current, estimatedTotal.get(), chunk, world);
                            }
                        }
                    }
                    for (Chunk col : world.chunks) {
                        if (col.loaded()) {
                            col.unload();
                        }
                    }
                });
            } else {
                regionStream.forEach(pos -> {
                    int xx = pos.getX() << 5;
                    int zz = pos.getY() << 5;

                    //compute mask indicating which chunks should be visited
                    BitSet visitableChunksMask = visitableChunksInRegionThreadLocal.get();
                    visitableChunksMask.set(0, 32 * 32);
                    this.maskVisitableChunksInRegion(pos.getX(), pos.getY(), xx, zz, visitableChunksMask);

                    for (int x = 0; x < 32; x++) {
                        for (int z = 0; z < 32; z++) {
                            Chunk chunk;
                            if (visitableChunksMask.get(x * 32 + z) && (chunk = this.world.column(xx + x, zz + z)).load(false)) {
                                long current = curr.getAndIncrement();
                                for (ChunkProcessor processor : this.processors) {
                                    processor.handle(current, estimatedTotal.get(), chunk);
                                }
                                chunk.unload();
                            } else {
                                estimatedTotal.decrementAndGet();
                            }
                        }
                    }
                });
            }
        } else {
            throw new UnsupportedOperationException(String.format("Cannot iterate over chunks in a world loaded by \"%s\"!", PorkUtil.className(manager)));
        }

        return this;
    }

    protected Collection<Vec2i> getRegionPositions(AnvilWorldManager anvilWorldManager) {
        return anvilWorldManager.getRegions();
    }

    protected void maskVisitableChunksInRegion(int regionX, int regionZ, int baseChunkX, int baseChunkZ, BitSet mask) {
        //no-op
    }

    @RequiredArgsConstructor
    @Accessors(fluent = true)
    private class BorderingWorld implements World {
        @Getter
        private final int dimension;

        @NonNull
        @Getter
        @Accessors(fluent = false)
        private final MinecraftSave save;

        @NonNull
        @Getter
        private final WorldManager manager;

        private final Chunk[] chunks = new Chunk[34 * 34];
        private int offsetX;
        private int offsetZ;

        @Override
        public Chunk column(int x, int z) {
            return this.chunks[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public Chunk columnOrNull(int x, int z) {
            return this.chunks[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public void save() {
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public int minX() {
            return (this.offsetX - 1) << 4;
        }

        @Override
        public int minY() {
            return 0;
        }

        @Override
        public int minZ() {
            return (this.offsetZ - 1) << 4;
        }

        @Override
        public int maxX() {
            return (this.offsetX + 33) << 4;
        }

        @Override
        public int maxY() {
            return 256;
        }

        @Override
        public int maxZ() {
            return (this.offsetZ + 33) << 4;
        }
    }
}
