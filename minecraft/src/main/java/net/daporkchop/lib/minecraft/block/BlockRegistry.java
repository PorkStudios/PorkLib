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

package net.daporkchop.lib.minecraft.block;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

/**
 * Extension of {@link Registry} for blocks.
 * <p>
 * Note that all methods inherited from {@link Registry} (except {@link Registry#id()}) consider only mappings between block {@link Identifier}s and
 * legacy block IDs.
 * <p>
 * A block registry consists of a number of different mappings to and from different types, making it a fair bit more complicated than most other
 * registries. A brief description of each type follows:
 * <p>
 * - "legacy IDs" are {@code int} IDs given to each block, for example 1 (Stone) or 56 (Diamond Ore)
 * <p>
 * - "block IDs" or "block {@link Identifier}s" are user-friendly {@link Identifier}s given to each block, for example {@code minecraft:stone} (Stone)
 * or {@code minecraft:diamond_ore} (Diamond Ore)
 * <p>
 * - "metadata" are {@code int} values, generally in range 0-15, which describe the a block state. For example, {@code minecraft:stone} uses metadata
 * to differentiate between the different stone varieties (Andesite, etc.), or {@code minecraft:redstone} uses 16 metadata values to store its current
 * power level
 * <p>
 * - "block states" (normally referred to as {@link BlockState}) are simply a combination of a block (defined either by legacy or block ID) and a
 * metadata value
 * <p>
 * - "runtime IDs" are {@code int} IDs given to each block state, and serve no purpose beyond performance
 *
 * @author DaPorkchop_
 */
public interface BlockRegistry extends Registry {
    Identifier ID = Identifier.fromString("minecraft:blocks");

    @Override
    default Identifier id() {
        return ID;
    }

    @Override
    int size();

    /**
     * @return the {@link BlockState} used to represent air
     */
    BlockState air();

    /**
     * Checks whether or not the given block ID is registered.
     *
     * @param blockId the block ID to check for
     * @return whether or not the given block ID is registered
     */
    boolean containsBlockId(@NonNull Identifier blockId);

    /**
     * Checks whether or not the given legacy ID is registered.
     *
     * @param legacyId the legacy ID to check for
     * @return whether or not the given legacy ID is registered
     */
    boolean containsLegacyId(int legacyId);

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param blockId the block ID of the block state to check for
     * @param meta    the metadata of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(@NonNull Identifier blockId, int meta);

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param legacyId the legacy ID of the block state to check for
     * @param meta     the metadata of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(int legacyId, int meta);

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param runtimeId the runtime ID of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(int runtimeId);

    /**
     * Gets the legacy ID belonging to the given block.
     *
     * @param blockId the block ID of the block
     * @return the block's legacy ID
     */
    int getLegacyId(@NonNull Identifier blockId);

    /**
     * Gets the block ID belonging to the given block.
     *
     * @param legacyId the legacy ID of the block
     * @return the block's ID
     */
    Identifier getBlockId(int legacyId);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param blockId the block ID of the block state to get
     * @param meta    the metadata of the block state to get
     * @return the {@link BlockState} for the given block state
     */
    BlockState getState(@NonNull Identifier blockId, int meta);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param legacyId the legacy ID of the block state to get
     * @param meta     the metadata of the block state to get
     * @return the {@link BlockState} for the given block state
     */
    BlockState getState(int legacyId, int meta);

    /**
     * Gets the default {@link BlockState} for to the given block ID.
     *
     * @param blockId the block ID of the block state to get
     * @return the default {@link BlockState} for the given block ID
     */
    BlockState getDefaultState(@NonNull Identifier blockId);

    /**
     * Gets the default {@link BlockState} for to the given legacy block ID.
     *
     * @param legacyId the legacy ID of the block state to get
     * @return the default {@link BlockState} for the given legacy block ID
     */
    BlockState getDefaultState(int legacyId);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param runtimeId the runtime ID of the block state to get
     * @return the {@link BlockState} for the given block state
     */
    BlockState getState(int runtimeId);

    /**
     * Gets the runtime ID for the given block state.
     *
     * @param blockId the block ID of the block state to get
     * @param meta    the metadata of the block state to get
     * @return the runtime ID for the given block state
     */
    int getRuntimeId(@NonNull Identifier blockId, int meta);

    /**
     * Gets the runtime ID for the given block state.
     *
     * @param legacyId the legacy ID of the block state to get
     * @param meta     the metadata of the block state to get
     * @return the runtime ID for the given block state
     */
    int getRuntimeId(int legacyId, int meta);

    void forEachBlockId(@NonNull Consumer<? super Identifier> callback);

    void forEachLegacyId(@NonNull IntConsumer callback);

    void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action);

    void forEachState(@NonNull Consumer<? super BlockState> callback);

    void forEachRuntimeId(@NonNull IntConsumer callback);

    @Override
    @Deprecated
    default boolean contains(@NonNull Identifier identifier) {
        return this.containsBlockId(identifier);
    }

    @Override
    @Deprecated
    default int get(@NonNull Identifier identifier) {
        return this.getLegacyId(identifier);
    }

    @Override
    @Deprecated
    default boolean contains(int id) {
        return this.containsLegacyId(id);
    }

    @Override
    @Deprecated
    default Identifier get(int id) {
        return this.getBlockId(id);
    }

    @Override
    @Deprecated
    Iterator<Identifier> iterator();

    @Override
    @Deprecated
    default void forEach(@NonNull Consumer<? super Identifier> action) {
        this.forEachBlockId(action);
    }

    @Override
    @Deprecated
    default void forEach(@NonNull IntConsumer action) {
        this.forEachLegacyId(action);
    }

    @Override
    @Deprecated
    default void forEach(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.forEachBlockId(action);
    }
}
