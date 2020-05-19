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

package net.daporkchop.lib.minecraft.format.anvil;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFileCache;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldProvider;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
public class AnvilWorldProvider extends AbstractRefCounted implements WorldProvider {
    protected final AnvilSaveOptions options;
    protected final File root;

    protected final Lock readLock;
    protected final Lock writeLock;

    protected final RegionFile regionCache;

    public AnvilWorldProvider(@NonNull File root, @NonNull AnvilSaveOptions options) {
        this.root = PFiles.ensureDirectoryExists(root);
        this.options = options;

        this.regionCache = new RegionFileCache(options, root);

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public Chunk loadChunk(@NonNull World parent, int x, int z) throws IOException {
        return null;
    }

    @Override
    @Deprecated
    public Section loadSection(@NonNull Chunk parent, int x, int y, int z) throws IOException {
        Section section = parent.section(y);
        return section != null ? section.retain() : null;
    }

    @Override
    public void saveChunk(@NonNull Chunk chunk) throws IOException {
    }

    @Override
    @Deprecated
    public void saveSection(@NonNull Section section) throws IOException {
        this.saveChunk(section.parent());
    }

    @Override
    public PFuture<Void> saveChunkAsync(@NonNull Chunk chunk) {
        return null;
    }

    @Override
    public PFuture<Void> saveSectionAsync(@NonNull Section section) {
        return this.saveChunkAsync(section.parent());
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public PFuture<Void> flushAsync() {
        return null;
    }

    @Override
    public Spliterator<Chunk> allChunks() throws IOException {
        return null;
    }

    @Override
    public Spliterator<Section> allSections() throws IOException {
        return null;
    }

    @Override
    public WorldProvider retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
    }
}
