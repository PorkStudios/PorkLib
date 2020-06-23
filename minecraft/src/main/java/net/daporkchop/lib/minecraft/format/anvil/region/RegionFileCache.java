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

package net.daporkchop.lib.minecraft.format.anvil.region;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.collections.map.MaxSizeLinkedHashMap;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.common.util.exception.ReadOnlyException;
import net.daporkchop.lib.concurrent.lock.NoopLock;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.format.anvil.AnvilSaveOptions;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.EmptyRegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.MemoryMappedRegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.OverclockedRegionFile;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.WriteAccess;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * A cache for {@link RegionFile}s to prevent having too many files open at once.
 *
 * @author DaPorkchop_
 */
//this has some pretty complex locking, i don't THINK it should be able to deadlock but don't quote me on that
@Accessors(fluent = true)
public class RegionFileCache implements RegionFile, IOFunction<Vec2i, RegionFile> {
    protected static Vec2i toRegionCoords(int chunkX, int chunkZ) {
        return new Vec2i(chunkX >> 5, chunkZ >> 5);
    }

    protected final SaveOptions options;
    protected final Map<Vec2i, RegionFile> internalCache;
    protected final File root;

    @Getter
    protected final boolean readOnly;

    protected boolean closed = false;

    public RegionFileCache(@NonNull SaveOptions options, @NonNull File root) {
        this.options = options;
        this.readOnly = options.get(SaveOptions.ACCESS) == WriteAccess.READ_ONLY;

        this.internalCache = new MaxSizeLinkedHashMap.Closing<>(options.get(AnvilSaveOptions.REGION_CACHE_SIZE), true);
        this.root = PFiles.ensureDirectoryExists(root);
    }

    @Override
    public RawChunk read(int x, int z) throws IOException {
        RegionFile region;
        Lock lock;
        synchronized (this.internalCache) {
            region = this.internalCache.computeIfAbsent(toRegionCoords(x, z), this);
            (lock = region.readLock()).lock();
        }
        try {
            return region.read(x & 0x1F, z & 0x1F);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean write(int x, int z, @NonNull ByteBuf data, int version, long timestamp, boolean forceOverwrite) throws ReadOnlyException, IOException {
        this.assertWritable();
        RegionFile region;
        Lock lock;
        synchronized (this.internalCache) {
            region = this.internalCache.computeIfAbsent(toRegionCoords(x, z), this);
            (lock = region.writeLock()).lock();
        }
        try {
            return region.write(x & 0x1F, z & 0x1F, data, version, timestamp, forceOverwrite);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean delete(int x, int z) throws ReadOnlyException, IOException {
        this.assertWritable();
        RegionFile region;
        Lock lock;
        synchronized (this.internalCache) {
            region = this.internalCache.computeIfAbsent(toRegionCoords(x, z), this);
            (lock = region.writeLock()).lock();
        }
        try {
            return region.delete(x & 0x1F, z & 0x1F);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(int x, int z) throws IOException {
        RegionFile region;
        Lock lock;
        synchronized (this.internalCache) {
            region = this.internalCache.computeIfAbsent(toRegionCoords(x, z), this);
            (lock = region.readLock()).lock();
        }
        try {
            return region.contains(x & 0x1F, z & 0x1F);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long timestamp(int x, int z) throws IOException {
        RegionFile region;
        Lock lock;
        synchronized (this.internalCache) {
            region = this.internalCache.computeIfAbsent(toRegionCoords(x, z), this);
            (lock = region.readLock()).lock();
        }
        try {
            return region.timestamp(x & 0x1F, z & 0x1F);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void defrag() throws ReadOnlyException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File file() {
        return this.root;
    }

    @Override
    public Lock readLock() {
        return NoopLock.INSTANCE;
    }

    @Override
    public Lock writeLock() {
        return NoopLock.INSTANCE;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this.internalCache) {
            this.internalCache.values().forEach((IOConsumer<RegionFile>) RegionFile::flush);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this.internalCache) {
            for (Iterator<RegionFile> itr = this.internalCache.values().iterator(); itr.hasNext(); ) {
                itr.next().close();
                itr.remove();
            }
        }
    }

    /**
     * Attempts to open a region.
     *
     * @param pos the region's position
     * @return the opened region
     * @deprecated internal method, should not be called by user code
     */
    @Override
    @Deprecated
    public RegionFile applyThrowing(Vec2i pos) throws IOException {
        File file = new File(this.root, PStrings.fastFormat("r.%d.%d.mca", pos.getX(), pos.getY()));
        if (this.readOnly()) {
            if (PFiles.checkFileExists(file)) {
                if (this.options.get(AnvilSaveOptions.MMAP_REGIONS)) {
                    return new MemoryMappedRegionFile(file, this.options.get(AnvilSaveOptions.PREFETCH_REGIONS));
                } else {
                    return new OverclockedRegionFile(file, this.options.get(SaveOptions.NETTY_ALLOC), true);
                }
            } else {
                return EmptyRegionFile.INSTANCE;
            }
        } else {
            return new OverclockedRegionFile(file, this.options.get(SaveOptions.NETTY_ALLOC), false);
        }
    }

    protected void assertOpen() throws IOException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
    }
}
