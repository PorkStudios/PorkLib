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
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.Collection;

/**
 * Representation of a Minecraft chunk, consisting of {@link Section}s identified by their integer Y coordinate.
 * <p>
 * In vanilla Minecraft, a chunk has a fixed limit of 16 sections (with coordinates between 0 and 15), which are always loaded as long as the chunk
 * itself is loaded.
 * <p>
 * Every {@link Section} loaded by a chunk keeps a reference to the chunk which is not released until the {@link Section} itself is released.
 *
 * @author DaPorkchop_
 */
public interface Chunk extends BlockAccess, LightAccess, IntHolderXZ, RefCounted {
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
     * @return a snapshot of all of the {@link Section}s currently loaded by this chunk
     */
    Collection<Section> loadedSections();

    /**
     * Gets the already loaded {@link Section} at the given Y coordinate.
     *
     * @param y the Y coordinate of the {@link Section}
     * @return the {@link Section} at the given Y coordinate, or {@code null} if it wasn't loaded
     */
    Section section(int y);

    /**
     * Gets or loads the {@link Section} at the given Y coordinate.
     * <p>
     * If {@code load} is {@code true} and the {@link Section} wasn't already loaded, it will be loaded and the method will block until the load is
     * complete.
     *
     * @param y    the Y coordinate of the {@link Section}
     * @param load whether or not to load the {@link Section} if it wasn't loaded already
     * @return the {@link Section} at the given Y coordinate
     */
    default Section section(int y, boolean load) {
        Section section = this.section(y);
        return section == null && load ? this.loadSection(y).join() : section;
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

    @Override
    int refCnt();

    @Override
    Chunk retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
