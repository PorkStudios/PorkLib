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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BlockRegistry} used by implementations of the Anvil save format.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class AnvilBlockRegistry implements BlockRegistry {
    protected final Map<Identifier, Integer> blockToLegacy;
    protected final Identifier[] legacyToBlock;
    protected final AnvilBlockState[] legacyToState;

    @Getter
    protected final int size;

    protected AnvilBlockRegistry(@NonNull Builder builder) {
        this.size = builder.toIds.size();

        this.blockToLegacy = new IdentityHashMap<>(this.size);
        this.blockToLegacy.putAll(builder.toIds);

        this.legacyToBlock = new Identifier[this.blockToLegacy.values().stream().mapToInt(Integer::intValue).max().orElse(0)];
        this.blockToLegacy.forEach((blockId, legacyId) -> this.legacyToBlock[legacyId] = blockId);

        this.legacyToState = new AnvilBlockState[this.legacyToBlock.length];
        this.blockToLegacy.forEach((blockId, legacyId) -> {
            AnvilBlockState[] states = new AnvilBlockState[16];
            for (int meta = 0; meta < 16; meta++)   {
                states[meta] = new AnvilBlockState(blockId, legacyId, meta, (legacyId << 4) | meta, states, this);
            }
            this.legacyToState[legacyId] = states[0];
        });
    }

    @Override
    public boolean containsBlockId(@NonNull Identifier blockId) {
        return this.blockToLegacy.containsKey(blockId);
    }

    @Override
    public boolean containsLegacyId(int legacyId) {
        return legacyId >= 0 && legacyId < this.legacyToBlock.length && this.legacyToBlock[legacyId] != null;
    }

    @Override
    public boolean containsState(@NonNull Identifier blockId, int meta) {
        return meta >= 0 && meta < 16 && this.containsBlockId(blockId);
    }

    @Override
    public boolean containsState(int legacyId, int meta) {
        return meta >= 0 && meta < 16 && this.containsLegacyId(legacyId);
    }

    @Override
    public boolean containsState(int runtimeId) {
        return this.containsLegacyId(runtimeId >> 4);
    }

    @Override
    public int getLegacyId(@NonNull Identifier blockId) {
        Integer legacyId = this.blockToLegacy.get(blockId);
        checkArg(legacyId != null, "unregistered block ID \"%s\"", blockId);
        return legacyId;
    }

    @Override
    public Identifier getBlockId(int legacyId) {
        Identifier blockId = null;
        checkArg(legacyId >= 0 && legacyId < this.legacyToBlock.length && (blockId = this.legacyToBlock[legacyId]) != null, "unregistered legacy ID %d", legacyId);
        return blockId;
    }

    @Override
    public BlockState getState(@NonNull Identifier blockId, int meta) {
        BlockState state = this.legacyToState[this.getLegacyId(blockId)];
        return meta == 0 ? state : state.withMeta(meta);
    }

    @Override
    public BlockState getState(int legacyId, int meta) {
        BlockState state = null;
        checkArg(legacyId >= 0 && legacyId < this.legacyToState.length && (state = this.legacyToState[legacyId]) != null, "unregistered legacy ID %d", legacyId);
        return meta == 0 ? state : state.withMeta(meta);
    }

    @Override
    public BlockState getState(int runtimeId) {
        return this.getState(runtimeId >> 4, runtimeId & 0xF);
    }

    @Override
    public int getRuntimeId(@NonNull Identifier blockId, int meta) {
        checkArg(meta >= 0 && meta < 16, "meta (%d) must be in range 0-15!", meta);
        return (this.getLegacyId(blockId) << 4) | meta;
    }

    @Override
    public int getRuntimeId(int legacyId, int meta) {
        checkArg(meta >= 0 && meta < 16, "meta (%d) must be in range 0-15!", meta);
        checkArg(this.containsLegacyId(legacyId), "unregistered legacy ID %d", legacyId);
        return (legacyId << 4) | meta;
    }

    @Override
    public void forEachBlockId(@NonNull Consumer<? super Identifier> callback) {
        this.blockToLegacy.keySet().forEach(callback);
    }

    @Override
    public void forEachLegacyId(@NonNull IntConsumer callback) {
        this.blockToLegacy.values().forEach(callback::accept);
    }

    @Override
    public void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.blockToLegacy.forEach(action::accept);
    }

    @Override
    public void forEachState(@NonNull Consumer<? super BlockState> callback) {
        for (AnvilBlockState defaultState : this.legacyToState)  {
            if (defaultState != null)   {
                AnvilBlockState[] states = defaultState.states;
                for (AnvilBlockState state : states)    {
                    callback.accept(state);
                }
            }
        }
    }

    @Override
    public void forEachRuntimeId(@NonNull IntConsumer callback) {
        for (BlockState defaultState : this.legacyToState)  {
            if (defaultState != null)   {
                int baseId = defaultState.legacyId() << 4;
                for (int meta = 0; meta < 16; meta++)   {
                    callback.accept(baseId | meta);
                }
            }
        }
    }

    @Override
    public Iterator<Identifier> iterator() {
        return this.blockToLegacy.keySet().iterator();
    }

    public static class Builder {
        protected final Map<Identifier, Integer> toIds = new IdentityHashMap<>();
        protected final Map<Integer, Identifier> fromIds = new HashMap<>();

        /**
         * Registers the given {@link Identifier} to numeric ID pair.
         *
         * @param identifier the {@link Identifier}
         * @param id         the numeric ID
         * @return this builder
         */
        public synchronized Builder register(@NonNull Identifier identifier, int id) {
            checkState(!this.toIds.containsKey(identifier), "ID %s is already registered!", identifier);
            checkState(!this.fromIds.containsKey(id), "ID %s is already registered!", id); //use %s to avoid having a second string instance, the result should be the same
            this.toIds.put(identifier, id);
            this.fromIds.put(id, identifier);
            return this;
        }

        /**
         * Builds the completed registry.
         *
         * @return the completed registry
         */
        public synchronized AnvilBlockRegistry build() {
            return new AnvilBlockRegistry(this);
        }
    }
}
