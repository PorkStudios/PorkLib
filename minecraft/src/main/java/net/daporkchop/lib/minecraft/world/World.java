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
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.Collection;

/**
 * Representation of a Minecraft world, consisting of {@link Chunk}s identified by their integer X, Z coordinates.
 * <p>
 * Every {@link Chunk} loaded by a world keeps a reference to the world which is not released until the {@link Chunk} itself is released. Additionally,
 * world instances keep a reference to their {@link WorldProvider} which is not released until the world is released.
 *
 * @author DaPorkchop_
 */
public interface World extends BlockAccess, RefCounted {
    /**
     * @return the {@link Save} that loaded this world
     */
    Save parent();

    /**
     * @return the {@link Identifier} used to identify this world in its parent {@link Save}
     */
    Identifier identifier();

    /**
     * @return the ID used to identify this world in its parent {@link Save}
     */
    int id();

    /**
     * @return the {@link WorldProvider} used for handling I/O of chunks and cubes
     */
    WorldProvider provider();

    /**
     * @return a snapshot of all of the {@link Chunk}s currently loaded by this world
     */
    Collection<Chunk> loadedChunks();

    /**
     * Gets the already loaded {@link Chunk} at the given coordinates.
     *
     * @param x the X coordinate of the {@link Chunk}
     * @param z the Z coordinate of the {@link Chunk}
     * @return the {@link Chunk} at the given coordinates, or {@code null} if it wasn't loaded
     */
    Chunk chunk(int x, int z);

    /**
     * Gets or loads the {@link Chunk} at the given coordinates.
     * <p>
     * If {@code load} is {@code true} and the {@link Chunk} wasn't already loaded, it will be loaded and the method will block until the load is
     * complete.
     *
     * @param x    the X coordinate of the {@link Chunk}
     * @param z    the Z coordinate of the {@link Chunk}
     * @param load whether or not to load the {@link Chunk} if it wasn't loaded already
     * @return the {@link Chunk} at the given coordinates
     */
    default Chunk chunk(int x, int z, boolean load) {
        Chunk chunk = this.chunk(x, z);
        return chunk == null && load ? this.loadChunk(x, z).join() : chunk;
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

    @Override
    int refCnt();

    @Override
    World retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
