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

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.stream.Stream;

/**
 * Handles loading and unloading of chunks for a {@link World}.
 *
 * @author DaPorkchop_
 */
public interface SectionManager extends RefCounted {
    /**
     * @return a stream over all currently loaded chunks
     */
    Stream<Chunk> loadedChunks();

    /**
     * Gets the already loaded {@link Chunk} at the given coordinates.
     *
     * @param x the X coordinate of the {@link Chunk}
     * @param z the Z coordinate of the {@link Chunk}
     * @return the {@link Chunk} at the given coordinates, or {@code null} if it wasn't loaded
     */
    Chunk getChunk(int x, int z);

    /**
     * Gets or loads the {@link Chunk} at the given coordinates.
     * <p>
     * If the {@link Chunk} wasn't already loaded, it will be loaded and the method will block until the load is complete.
     *
     * @param x the X coordinate of the {@link Chunk}
     * @param z the Z coordinate of the {@link Chunk}
     * @return the {@link Chunk} at the given coordinates
     */
    default Chunk getOrLoadChunk(int x, int z) {
        Chunk chunk = this.getChunk(x, z);
        return chunk != null ? chunk : this.loadChunk(x, z).join();
    }

    /**
     * Gets or loads the {@link Chunk} at the given coordinates.
     * <p>
     * If the {@link Chunk} was already loaded, the returned {@link PFuture} will be completed immediately.
     *
     * @param x the X coordinate of the {@link Chunk}
     * @param z the Z coordinate of the {@link Chunk}
     * @return a future which will be completed with the {@link Chunk} at the given coordinates
     */
    PFuture<Chunk> loadChunk(int x, int z);

    /**
     * Unloads some chunks and/or sections.
     *
     * @param full whether or not do do a full GC. If {@code true}, all loaded chunks and sections will be unloaded. If {@code false}, only chunks
     *             and sections deemed to be "no longer needed" by the implementation will be unloaded.
     */
    void gc(boolean full);

    @Override
    int refCnt();

    @Override
    SectionManager retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
