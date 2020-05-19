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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.exception.ReadOnlyException;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldProvider;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.Spliterator;

/**
 * A wrapper around a {@link WorldProvider} to make it read-only.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class ReadOnlyWorldProvider implements WorldProvider {
    @NonNull
    protected final SaveOptions options;
    @NonNull
    protected final WorldProvider delegate;

    @Override
    public Chunk loadChunk(@NonNull World parent, int x, int z) throws IOException {
        return this.delegate.loadChunk(parent, x, z);
    }

    @Override
    public Section loadSection(@NonNull Chunk parent, int x, int y, int z) throws IOException {
        return this.delegate.loadSection(parent, x, y, z);
    }

    @Override
    public void saveChunk(@NonNull Chunk chunk) throws IOException {
        throw new ReadOnlyException();
    }

    @Override
    public void saveSection(@NonNull Section section) throws IOException {
        throw new ReadOnlyException();
    }

    @Override
    public PFuture<Void> saveChunkAsync(@NonNull Chunk chunk) {
        throw new UnsupportedOperationException("read-only", new ReadOnlyException());
    }

    @Override
    public PFuture<Void> saveSectionAsync(@NonNull Section section) {
        throw new UnsupportedOperationException("read-only", new ReadOnlyException());
    }

    @Override
    public void flush() throws IOException {
        //no-op
    }

    @Override
    public PFuture<Void> flushAsync() {
        return PFutures.successful(null, this.options.ioExecutor());
    }

    @Override
    public Spliterator<Chunk> allChunks() throws IOException {
        return this.delegate.allChunks();
    }

    @Override
    public Spliterator<Section> allSections() throws IOException {
        return this.delegate.allSections();
    }

    @Override
    public int refCnt() {
        return this.delegate.refCnt();
    }

    @Override
    public WorldProvider retain() throws AlreadyReleasedException {
        return this.delegate.retain();
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        return this.delegate.release();
    }
}
