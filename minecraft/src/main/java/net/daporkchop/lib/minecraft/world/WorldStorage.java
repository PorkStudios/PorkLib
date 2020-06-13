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

import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.Collections;
import java.util.Spliterator;

/**
 * Interface for loading/saving chunks/sections.
 * <p>
 * Multiple save operations on the same chunks/section are ALWAYS guaranteed to be written to disk in order. If a chunk is saved, modified, and saved
 * again before the first save operation was completed, the implementation must ensure that, once both save operations are complete, it is the second
 * one that is present on disk.
 * <p>
 * When a world storage is released, it must block until all queued write operations are completed.
 * <p>
 * Implementations of this class are expected to be safely usable from multiple threads.
 *
 * @author DaPorkchop_
 */
public interface WorldStorage extends RefCounted {
    /**
     * Loads the {@link Chunk} at the given coordinates.
     *
     * @param x the X coordinate of the chunk to load
     * @param z the Z coordinate of the chunk to load
     * @return the loaded {@link Chunk}, or {@code null} if the chunk doesn't exist
     */
    Chunk loadChunk(int x, int z) throws IOException;

    /**
     * Loads the {@link Section} at the given coordinates.
     *
     * @param x the X coordinate of the section to load
     * @param y the Y coordinate of the section to load
     * @param z the Z coordinate of the section to load
     * @return the loaded {@link Section}, or {@code null} if the section doesn't exist
     * @throws IllegalArgumentException if the given {@link Chunk} does not belong to this storage
     */
    Section loadSection(int x, int y, int z) throws IOException;

    /**
     * Loads the {@link Chunk} at the given coordinates asynchronously.
     *
     * @param x the X coordinate of the chunk to load
     * @param z the Z coordinate of the chunk to load
     * @return a {@link PFuture} which will be completed with the loaded chunk, or {@code null} if the chunk doesn't exist
     */
    PFuture<Chunk> loadChunkAsync(int x, int z);

    /**
     * Loads the {@link Section} at the given coordinates asynchronously.
     *
     * @param x the X coordinate of the section to load
     * @param y the Y coordinate of the section to load
     * @param z the Z coordinate of the section to load
     * @return a {@link PFuture} which will be completed with the loaded section, or {@code null} if the section doesn't exist
     */
    PFuture<Section> loadSectionAsync(int x, int y, int z);

    /**
     * Saves all of the {@link Chunk}s in the given {@link Iterable}.
     * <p>
     * This method will block until all of the given chunks have been completely written to disk.
     * <p>
     * Chunks that are not dirty will be ignored.
     *
     * @param chunks the {@link Chunk}s to save
     * @see #save(Iterable, Iterable)
     */
    default void saveChunks(@NonNull Iterable<Chunk> chunks) throws IOException {
        this.save(chunks, Collections.emptySet());
    }

    /**
     * Saves all of the {@link Section}s in the given {@link Iterable}.
     * <p>
     * This method will block until all of the given sections have been completely written to disk.
     * <p>
     * Sections that are not dirty will be ignored.
     *
     * @param sections the {@link Section}s to save
     * @see #save(Iterable, Iterable)
     */
    default void saveSections(@NonNull Iterable<Section> sections) throws IOException {
        this.save(Collections.emptySet(), sections);
    }

    /**
     * A merged version of {@link #saveChunks(Iterable)} and {@link #saveSections(Iterable)}.
     * <p>
     * Due to the fact that some implementations may save sections and chunks at the same time, this method may be significantly faster than
     * calling both of the methods individually.
     * <p>
     * This method will block until all of the given chunks and sections have been completely written to disk.
     * <p>
     * Chunks and sections that are not dirty will be ignored.
     *
     * @param chunks   the {@link Chunk}s to save
     * @param sections the {@link Section}s to save
     */
    void save(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections) throws IOException;

    /**
     * Saves all of the {@link Chunk}s in the given {@link Iterable} asynchronously.
     * <p>
     * Unlike {@link #saveChunks(Iterable)}, this method makes no guarantees as to when the chunks will be saved. An implementation may choose to make the
     * entire operation block, returning a completed {@link PFuture}, or it may do everything asynchronously, causing the chunk's dirty flag to be
     * reset at an unknown point in the future.
     * <p>
     * Chunks that are not dirty will be ignored.
     *
     * @param chunks the {@link Chunk}s to save
     * @return a {@link PFuture} which will be completed after the chunks were written to disk
     * @see #saveAsync(Iterable, Iterable)
     */
    default PFuture<Void> saveChunksAsync(@NonNull Iterable<Chunk> chunks) {
        return this.saveAsync(chunks, Collections.emptySet());
    }

    /**
     * Saves all of the {@link Section}s in the given {@link Iterable} asynchronously.
     * <p>
     * Unlike {@link #saveSections(Iterable)}, this method makes no guarantees as to when the sections will be saved. An implementation may choose to make
     * the entire operation block, returning a completed {@link PFuture}, or it may do everything asynchronously, causing the section's dirty flag to be
     * reset at an unknown point in the future.
     * <p>
     * Sections that are not dirty will be ignored.
     *
     * @param sections the {@link Section}s to save
     * @return a {@link PFuture} which will be completed after the sections were written to disk
     * @see #saveAsync(Iterable, Iterable)
     */
    default PFuture<Void> saveSectionsAsync(@NonNull Iterable<Section> sections) {
        return this.saveAsync(Collections.emptySet(), sections);
    }

    /**
     * A merged version of {@link #saveChunksAsync(Iterable)} and {@link #saveSectionsAsync(Iterable)}.
     * <p>
     * Due to the fact that some implementations may save sections and chunks at the same time, this method may be significantly faster than
     * calling both of the methods individually.
     * <p>
     * Unlike {@link #save(Iterable, Iterable)}, this method makes no guarantees as to when the sections will be saved. An implementation may choose to make
     * the entire operation block, returning a completed {@link PFuture}, or it may do everything asynchronously, causing the section's dirty flag to be
     * reset at an unknown point in the future.
     * <p>
     * Chunks and sections that are not dirty will be ignored.
     *
     * @param chunks   the {@link Chunk}s to save
     * @param sections the {@link Section}s to save
     */
    PFuture<Void> saveAsync(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections);

    /**
     * Flushes any data that may be queued for writing to disk.
     * <p>
     * This method is guaranteed to block at least until all operations that were queued at the time this method was called, although an implementation
     * may choose to block until the save queue is empty.
     * <p>
     * This method will do nothing if the implementation does not queue writes.
     */
    void flush() throws IOException;

    /**
     * Asynchronously any data that may be queued for writing to disk.
     * <p>
     * This method is guaranteed to block at least until all operations that were queued at the time this method was called, although an implementation
     * may choose to block until the save queue is empty.
     * <p>
     * This method will return an already completed {@link PFuture} if the implementation does not queue writes.
     *
     * @return a {@link PFuture} which will be completed once the chunks have been flushed
     */
    PFuture<Void> flushAsync();

    /**
     * Gets a {@link Spliterator} over all the {@link Chunk}s in the world.
     * <p>
     * The order in which {@link Chunk}s are returned is up to the implementation, which may choose any order most efficient for parallel iteration.
     * <p>
     * Note that all of the {@link Chunk}s returned by the {@link Spliterator} will have been newly loaded from disk, and must be released manually.
     *
     * @return a {@link Spliterator} over all the {@link Chunk}s in the world
     */
    Spliterator<Chunk> allChunks() throws IOException;

    /**
     * Gets a {@link Spliterator} over all the {@link Section}s in the world.
     * <p>
     * The order in which {@link Section}s are returned is up to the implementation, which may choose any order most efficient for parallel iteration.
     * <p>
     * Note that all of the {@link Section}s returned by the {@link Spliterator} will have been newly loaded from disk, and must be released manually.
     *
     * @return a {@link Spliterator} over all the {@link Section}s in the world
     */
    Spliterator<Section> allSections() throws IOException;

    @Override
    int refCnt();

    @Override
    WorldStorage retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
