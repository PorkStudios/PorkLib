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
import net.daporkchop.lib.math.access.IntHolderXZ;
import net.daporkchop.lib.minecraft.block.BlockAccess;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Representation of a Minecraft chunk, consisting of {@link Section}s identified by their integer Y coordinate.
 * <p>
 * In vanilla Minecraft, a chunk has a fixed limit of 16 sections (with coordinates between 0 and 15), which are always loaded as long as the chunk
 * itself is loaded.
 * <p>
 * Semantics for (un)loading of chunks:
 * <p>
 * Although a chunk is loosely defined as a simple container for sections, possibly with some additional metadata, implementations of {@link WorldStorage}
 * may choose to load some or all contained sections when loading a chunk. This behavior is necessary as some vanilla-style formats store sections and
 * chunks together, and loading them individually would result in the same data being parsed multiple times. Sections which are loaded in this manner
 * will be loaded immediately, and will remain in memory for the entire lifetime of the chunk, as the chunk will hold a single reference to them.
 * <p>
 * Every {@link Section} loaded by a chunk keeps a reference to the chunk which is not released until the {@link Section} itself is released.
 *
 * @author DaPorkchop_
 */
public interface Chunk extends BlockAccess, LightAccess, IntHolderXZ, RefCounted {
    static void checkCoords(int x, int z) {
        checkIndex(x >= 0 && x < 16, "x (%d)", x);
        checkIndex(z >= 0 && z < 16, "z (%d)", z);
    }

    /**
     * @return the {@link World} that loaded this chunk
     */
    World parent();

    /**
     * @return this chunk's X coordinate
     */
    @Override
    int x();

    /**
     * @return this chunk's Z coordinate
     */
    @Override
    int z();

    /**
     * Gets the already loaded {@link Section} at the given Y coordinate.
     *
     * @param y the Y coordinate of the {@link Section}
     * @return the {@link Section} at the given Y coordinate, or {@code null} if it wasn't loaded
     */
    Section getSection(int y);

    /**
     * Gets or loads the {@link Section} at the given Y coordinate.
     * <p>
     * If the {@link Section} wasn't already loaded, it will be loaded and the method will block until the load is complete.
     *
     * @param y the Y coordinate of the {@link Section}
     * @return the {@link Section} at the given Y coordinate
     */
    default Section getOrLoadSection(int y) {
        Section section = this.getSection(y);
        return section != null ? section : this.loadSection(y).join();
    }

    /**
     * Gets or loads the {@link Section} at the given Y coordinate.
     * <p>
     * If the {@link Section} was already loaded, the returned {@link PFuture} will be completed immediately.
     *
     * @param y the Y coordinate of the {@link Section}
     * @return a future which will be completed with the {@link Section} at the given Y coordinate
     */
    PFuture<Section> loadSection(int y);

    /**
     * @return a completed {@link PFuture} with this chunk, shared in order to minimize allocations
     */
    PFuture<Chunk> selfFuture();

    @Override
    int refCnt();

    @Override
    Chunk retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
