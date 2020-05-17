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

import lombok.NonNull;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.format.anvil.AnvilSaveOptions;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.EmptyRegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.MemoryMappedRegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.impl.OverclockedRegionFile;
import net.daporkchop.lib.minecraft.util.WriteAccess;

import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A cache for {@link RegionFile}s to prevent having too many files open at once.
 *
 * @author DaPorkchop_
 */
public final class RegionFileCache implements IOFunction<Vec2i, RegionFile>, Flushable, AutoCloseable {
    protected final AnvilSaveOptions options;
    protected final Map<Vec2i, RegionFile> internalCache = new ConcurrentHashMap<>();
    protected final int maxSize;

    protected final File root;

    protected final Lock readLock;
    protected final Lock writeLock;

    public RegionFileCache(@NonNull AnvilSaveOptions options, @NonNull File root, int maxSize) {
        this.options = options;
        this.maxSize = notNegative(maxSize, "maxSize");
        this.root = PFiles.ensureDirectoryExists(root);

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    public RegionFile get(int x, int z) throws IOException {
        this.readLock.lock();
        try {
            //TODO: actually close regions after some time (will require some kind of linked cache)
            return this.internalCache.computeIfAbsent(new Vec2i(x, z), this);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void flush() throws IOException {
        this.readLock.lock();
        try {
            for (RegionFile region : this.internalCache.values()) {
                region.flush();
            }
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.writeLock.lock();
        try {
            for (RegionFile region : this.internalCache.values()) {
                region.close();
            }
            this.internalCache.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public RegionFile applyThrowing(Vec2i pos) throws IOException {
        File file = new File(this.root, String.format("r.%d.%d.mca", pos.getX(), pos.getY()));
        if (this.options.access() == WriteAccess.READ_ONLY) {
            if (PFiles.checkFileExists(file)) {
                return new MemoryMappedRegionFile(file, true);
            } else {
                return EmptyRegionFile.INSTANCE;
            }
        } else {
            return new OverclockedRegionFile(file, this.options.nettyAlloc(), false);
        }
    }
}
