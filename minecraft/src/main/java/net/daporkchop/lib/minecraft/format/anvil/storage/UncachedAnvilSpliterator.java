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

package net.daporkchop.lib.minecraft.format.anvil.storage;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.anvil.AnvilSaveOptions;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.MemoryMappedRegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.OverclockedRegionFile;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of a {@link Spliterator} over the contents of an Anvil world.
 *
 * @author DaPorkchop_
 */
public abstract class UncachedAnvilSpliterator<T> implements Spliterator<T> {
    protected final AnvilWorldStorage storage;
    protected final File[] regions;
    protected int index;
    protected int fence;

    protected RegionFile region;
    protected int chunkX;
    protected int chunkZ;

    public UncachedAnvilSpliterator(@NonNull AnvilWorldStorage storage) {
        storage.retain();
        this.storage = storage;
        this.regions = storage.listRegions();
        this.index = 0;
        this.fence = this.regions.length;
    }

    protected UncachedAnvilSpliterator(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
        storage.retain();
        this.storage = storage;
        this.regions = regions;
        this.index = index;
        this.fence = fence;
    }

    protected boolean nextRegion() throws IOException {
        checkState(this.region == null);
        if (this.index < this.fence) {
            File file = this.regions[this.index++];
            this.region = this.storage.options.get(AnvilSaveOptions.MMAP_REGIONS)
                          ? new MemoryMappedRegionFile(file, this.storage.options.get(AnvilSaveOptions.PREFETCH_REGIONS))
                          : new OverclockedRegionFile(file, this.storage.options.get(SaveOptions.NETTY_ALLOC), true);
            this.chunkX = this.chunkZ = 0; //reset chunk positions
            return true;
        } else {
            return false;
        }
    }

    protected AnvilCachedChunk next() {
        try {
            while (this.region != null || this.nextRegion()) {
                for (; this.chunkX < 32; this.chunkX++, this.chunkZ = 0) { //try to find the next chunk
                    while (this.chunkZ < 32) {
                        AnvilCachedChunk chunk = this.storage.load(this.region, this.chunkX, this.chunkZ++);
                        if (chunk != null) {
                            return chunk;
                        }
                    }
                }

                //if we got this far, the region has been completed, so close it
                try {
                    this.region.close();
                } finally {
                    this.region = null;
                }
            }

            //there is nothing left, release storage
            this.storage.release();
            return null;
        } catch (IOException e) {
            try {
                this.storage.release(); //make sure that storage is released again in case of exception
            } finally {
                PUnsafe.throwException(e);
                throw new RuntimeException(e); //unreachable
            }
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        int low = this.index;
        int high = this.fence;
        int mid = (low + high) >>> 1;
        return low < mid ? this.sub(this.storage, this.regions, this.fence = mid, high) : null;
    }

    @Override
    public long estimateSize() {
        return (this.fence - this.index) * 1024L;
    }

    @Override
    public int characteristics() {
        return DISTINCT | NONNULL | IMMUTABLE; //we can't report sized because there's no guarantees that every region contains all 1024 chunks
    }

    @Override
    public abstract boolean tryAdvance(Consumer<? super T> action);

    protected abstract Spliterator<T> sub(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence);

    /**
     * Implementation of a {@link Spliterator} over the chunks in an Anvil world.
     *
     * @author DaPorkchop_
     */
    public static class OfChunk extends UncachedAnvilSpliterator<Chunk> {
        public OfChunk(@NonNull AnvilWorldStorage storage) {
            super(storage);
        }

        protected OfChunk(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
            super(storage, regions, index, fence);
        }

        @Override
        public boolean tryAdvance(@NonNull Consumer<? super Chunk> action) {
            try (AnvilCachedChunk cachedChunk = this.next())  {
                if (cachedChunk == null)  {
                    return false;
                }
                try (Chunk chunk = cachedChunk.chunk()) {
                    action.accept(chunk);
                }
                return true;
            }
        }

        @Override
        protected Spliterator<Chunk> sub(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
            return new OfChunk(storage, regions, index, fence);
        }
    }

    /**
     * Implementation of a {@link Spliterator} over the chunks in an Anvil world.
     *
     * @author DaPorkchop_
     */
    public static class OfSection extends UncachedAnvilSpliterator<Section> {
        protected AnvilCachedChunk chunk;
        protected int sectionY;

        public OfSection(@NonNull AnvilWorldStorage storage) {
            super(storage);
        }

        protected OfSection(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
            super(storage, regions, index, fence);
        }

        protected boolean nextChunk() {
            checkState(this.chunk == null);
            if ((this.chunk = this.next()) != null)  {
                this.sectionY = 0;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean tryAdvance(@NonNull Consumer<? super Section> action) {
            while (this.chunk != null || this.nextChunk())    {
                while (this.sectionY < 16)  {
                    try (Section section = this.chunk.section(this.sectionY++)) {
                        if (section != null)    {
                            action.accept(section);
                            return true;
                        }
                    }
                }

                //if we get this far the chunk is complete
                try {
                    this.chunk.release();
                } finally {
                    this.chunk = null;
                }
            }
            return false;
        }

        @Override
        protected Spliterator<Section> sub(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
            return new OfSection(storage, regions, index, fence);
        }
    }
}
