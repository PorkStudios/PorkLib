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

package net.daporkchop.lib.minecraft.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class WorldScanner {
    /*@NonNull
    private final World world;

    private final Collection<ChunkProcessor>            processors            = new ArrayList<>();
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
            Collection<Vec2i> regions = anvilWorldManager.getRegions();
            estimatedTotal.set(regions.size() * 32L * 32L);
            Stream<Vec2i> stream = parallel ? regions.parallelStream() : regions.stream();
            if (!this.processorsNeighboring.isEmpty()) {
                ThreadLocal<BorderingWorld> worldThreadLocal = ThreadLocal.withInitial(() -> new BorderingWorld(this.world.dimension(), this.world.getSave(), this.world.manager()));
                stream.forEach(pos -> {
                    int xx = pos.getX() << 5;
                    int zz = pos.getY() << 5;
                    BorderingWorld world = worldThreadLocal.get();
                    world.offsetX = xx;
                    world.offsetZ = zz;
                    Chunk[] chunks = world.chunks;
                    for (int x = -1; x <= 32; x++) {
                        for (int z = -1; z <= 32; z++) {
                            Chunk col = chunks[(x + 1) * 34 + z + 1] = this.world.column(xx + x, zz + z);
                            if (!col.load(false) && (x >= 0 && x <= 31 && z >= 0 && z <= 31)) {
                                estimatedTotal.decrementAndGet();
                            }
                        }
                    }
                    for (int x = 31; x >= 0; x--) {
                        for (int z = 31; z >= 0; z--) {
                            Chunk chunk = chunks[(x + 1) * 34 + z + 1];
                            if (!chunk.loaded()) {
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
                });
            } else {
                stream.forEach(pos -> {
                    int xx = pos.getX() << 5;
                    int zz = pos.getY() << 5;
                    for (int x = 31; x >= 0; x--) {
                        for (int z = 31; z >= 0; z--) {
                            Chunk chunk = this.world.column(xx + x, zz + z);
                            if (chunk.load(false)) {
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
        private int     offsetX;
        private int     offsetZ;

        @Override
        public Map<Vec2i, Chunk> loadedColumns() {
            return Collections.emptyMap();
        }

        @Override
        public Chunk column(int x, int z) {
            return this.chunks[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public Chunk columnOrNull(int x, int z) {
            return this.chunks[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public Map<Vec3i, TileEntity> loadedTileEntities() {
            return Collections.emptyMap();
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
    }*/
}
