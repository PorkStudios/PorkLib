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

package net.daporkchop.lib.minecraft.region;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilWorldManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class WorldScanner {
    @NonNull
    @Getter
    private final World world;

    private final Collection<ColumnProcessor> processors = new ArrayList<>();
    private final Collection<ColumnProcessorNeighboring> processorsNeighboring = new ArrayList<>();

    public WorldScanner addProcessor(@NonNull Consumer<Column> processor) {
        this.processors.add((current, estimatedTotal, column) -> processor.accept(column));
        return this;
    }

    public WorldScanner addProcessor(@NonNull ColumnProcessor processor) {
        if (processor instanceof ColumnProcessorNeighboring) {
            this.addProcessor((ColumnProcessorNeighboring) processor);
        } else {
            this.processors.add(processor);
        }
        return this;
    }

    public WorldScanner addProcessor(@NonNull ColumnProcessorNeighboring processor) {
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
        WorldManager manager = this.world.getManager();
        if (manager instanceof AnvilWorldManager) {
            AnvilWorldManager anvilWorldManager = (AnvilWorldManager) manager;
            Collection<Vec2i> regions = anvilWorldManager.getRegions();
            estimatedTotal.set(regions.size() * 32L * 32L);
            Stream<Vec2i> stream = parallel ? regions.parallelStream() : regions.stream();
            if (!this.processorsNeighboring.isEmpty()) {
                ThreadLocal<Column[]> columnThreadLocal = ThreadLocal.withInitial(() -> new Column[34 * 34]);
                ThreadLocal<BorderingWorld> worldThreadLocal = ThreadLocal.withInitial(() -> new BorderingWorld(this.world.getId(), this.world.getSave(), this.world.getManager()));
                stream.forEach(pos -> {
                    int xx = pos.getX() << 5;
                    int zz = pos.getY() << 5;
                    Column[] columns = columnThreadLocal.get();
                    for (int x = -1; x <= 32; x++) {
                        for (int z = -1; z <= 32; z++) {
                            Column col = columns[(x + 1) * 34 + z + 1] = this.world.getColumn(xx + x, zz + z);
                            if (!col.load(false) && (x >= 0 && x <= 31 && z >= 0 && z <= 31)) {
                                estimatedTotal.decrementAndGet();
                            }
                        }
                    }
                    BorderingWorld world = worldThreadLocal.get();
                    world.setColumns(columns);
                    world.setOffsetX(xx);
                    world.setOffsetZ(zz);
                    for (int x = 31; x >= 0; x--) {
                        for (int z = 31; z >= 0; z--) {
                            Column column = columns[(x + 1) * 34 + z + 1];
                            if (!column.isLoaded()) {
                                continue;
                            }
                            long current = curr.getAndIncrement();
                            for (ColumnProcessor processor : this.processors) {
                                processor.handle(current, estimatedTotal.get(), column);
                            }
                            for (ColumnProcessorNeighboring processor : this.processorsNeighboring) {
                                processor.handle(current, estimatedTotal.get(), world, (x + xx) << 4, (z + zz) << 4);
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
                            Column column = this.world.getColumn(xx + x, zz + z);
                            if (column.load(false)) {
                                long current = curr.getAndIncrement();
                                for (ColumnProcessor processor : this.processors) {
                                    processor.handle(current, estimatedTotal.get(), column);
                                }
                                column.unload();
                            } else {
                                estimatedTotal.decrementAndGet();
                            }
                        }
                    }
                });
            }
        } else {
            throw new UnsupportedOperationException(String.format("Iteration over %s", manager.getClass().getCanonicalName()));
        }

        return this;
    }

    @FunctionalInterface
    public interface ColumnProcessor {
        void handle(long current, long estimatedTotal, @NonNull Column column);
    }

    @FunctionalInterface
    public interface ColumnProcessorNeighboring extends ColumnProcessor {
        @Override
        default void handle(long current, long estimatedTotal, Column column) {
            this.handle(current, estimatedTotal, column.getWorld(), column.getX() << 4, column.getZ() << 4);
        }

        void handle(long current, long estimatedTotal, @NonNull World world, int x, int z);
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    private class BorderingWorld implements World {
        private final int id;

        @NonNull
        private final MinecraftSave save;

        @NonNull
        private final WorldManager manager;

        private Column[] columns;
        private int offsetX;
        private int offsetZ;

        @Override
        public Map<Vec2i, Column> getLoadedColumns() {
            return null;
        }

        @Override
        public Column getColumn(int x, int z) {
            return this.columns[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public Column getColumnOrNull(int x, int z) {
            return this.columns[(x - this.offsetX + 1) * 34 + z - this.offsetZ + 1];
        }

        @Override
        public Map<Vec3i, TileEntity> getLoadedTileEntities() {
            return null;
        }

        @Override
        public void save() {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
