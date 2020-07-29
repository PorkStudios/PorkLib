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

package net.daporkchop.lib.minecraft.block.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Base implementation of {@link BlockRegistry}.
 *
 * @author DaPorkchop_
 */
//augh... this is super slow, but most of it only has to run once so it's no big deal
@Getter
@Accessors(fluent = true)
public abstract class AbstractBlockRegistry implements BlockRegistry {
    @Getter(AccessLevel.NONE)
    protected final Map<Identifier, DefaultBlockState> idToDefaultState = new HashMap<>();
    @Getter(AccessLevel.NONE)
    protected final IntObjMap<DefaultBlockState> legacyIdToDefaultState = new IntObjOpenHashMap<>();
    @Getter(AccessLevel.NONE)
    protected final IntObjMap<BlockState> runtimeIdToState = new IntObjOpenHashMap<>();

    protected final BlockState air;

    protected final int blocks;
    protected final int states;
    protected final int maxRuntimeId;

    protected <I extends Builder<I, B, R>, B extends BlockBuilder<B, I, R>, R extends BlockRegistry> AbstractBlockRegistry(@NonNull I builder) {
        builder.blocks.forEach((id, block) -> this.idToDefaultState.put(id, block.bake(uncheckedCast(this))));

        this.maxRuntimeId = this.idToDefaultState.values().stream()
                .peek(state -> {
                    if (state.hasLegacyId()) {
                        this.legacyIdToDefaultState.put(state.legacyId(), state);
                    }
                })
                .map(DefaultBlockState::otherMeta)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull) //legacy anvil has unused meta values (which are null)
                .peek(state -> this.runtimeIdToState.put(state.runtimeId(), state))
                .mapToInt(BlockState::runtimeId)
                .max().orElseThrow(IllegalStateException::new);

        this.air = this.getDefaultState(Identifier.fromString("minecraft:air"));

        this.blocks = this.idToDefaultState.size();
        this.states = toInt(this.idToDefaultState.values().stream().map(DefaultBlockState::otherMeta).flatMap(Arrays::stream).count());
    }

    @Override
    public boolean containsBlockId(@NonNull Identifier blockId) {
        return this.idToDefaultState.containsKey(blockId);
    }

    @Override
    public boolean containsLegacyId(int legacyId) {
        return this.legacyIdToDefaultState.containsKey(legacyId);
    }

    @Override
    public boolean containsState(@NonNull Identifier blockId, int meta) {
        if (meta < 0) {
            return false;
        }
        DefaultBlockState state = this.idToDefaultState.get(blockId);
        return state != null && meta < state.otherMeta.length;
    }

    @Override
    public boolean containsState(int legacyId, int meta) {
        if (meta < 0) {
            return false;
        }
        DefaultBlockState state = this.legacyIdToDefaultState.get(legacyId);
        return state != null && meta < state.otherMeta.length;
    }

    @Override
    public boolean containsState(int runtimeId) {
        return this.runtimeIdToState.containsKey(runtimeId);
    }

    @Override
    public boolean hasLegacyId(@NonNull Identifier blockId) {
        DefaultBlockState state = this.idToDefaultState.get(blockId);
        return state != null && state.hasLegacyId();
    }

    @Override
    public int getLegacyId(@NonNull Identifier blockId) {
        return this.getDefaultState(blockId).legacyId();
    }

    @Override
    public Identifier getBlockId(int legacyId) {
        DefaultBlockState state = this.legacyIdToDefaultState.get(legacyId);
        checkState(state != null, legacyId);
        return state.id();
    }

    @Override
    public BlockState getState(@NonNull Identifier blockId, int meta) {
        return this.getDefaultState(blockId).withMeta(meta);
    }

    @Override
    public BlockState getState(int legacyId, int meta) {
        return this.getDefaultState(legacyId).withMeta(meta);
    }

    @Override
    public BlockState getDefaultState(@NonNull Identifier blockId) {
        BlockState state = this.idToDefaultState.get(blockId);
        checkState(state != null, blockId);
        return state;
    }

    @Override
    public BlockState getDefaultState(int legacyId) {
        BlockState state = this.legacyIdToDefaultState.get(legacyId);
        checkState(state != null, legacyId);
        return state;
    }

    @Override
    public BlockState getState(int runtimeId) {
        BlockState state = this.runtimeIdToState.get(runtimeId);
        checkState(state != null, runtimeId);
        return state;
    }

    @Override
    public int getRuntimeId(@NonNull Identifier blockId, int meta) {
        return this.getState(blockId, meta).runtimeId();
    }

    @Override
    public int getRuntimeId(int legacyId, int meta) {
        return this.getState(legacyId, meta).runtimeId();
    }

    @Override
    public void forEachBlockId(@NonNull Consumer<? super Identifier> action) {
        this.idToDefaultState.keySet().forEach(action);
    }

    @Override
    public void forEachLegacyId(@NonNull IntConsumer action) {
        this.idToDefaultState.values().forEach(state -> action.accept(state.legacyId()));
    }

    @Override
    public void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.idToDefaultState.forEach((id, state) -> action.accept(id, state.legacyId()));
    }

    @Override
    public void forEachState(@NonNull Consumer<? super BlockState> action) {
        this.idToDefaultState.values().forEach(defaultState -> {
            for (BlockState state : defaultState.otherMeta) {
                action.accept(state);
            }
        });
    }

    @Override
    public void forEachRuntimeId(@NonNull IntConsumer action) {
        this.idToDefaultState.values().forEach(defaultState -> {
            for (BlockState state : defaultState.otherMeta) {
                action.accept(state.runtimeId());
            }
        });
    }

    public static abstract class Builder<I extends Builder<I, B, R>, B extends BlockBuilder<B, I, R>, R extends BlockRegistry> {
        protected final Map<Identifier, B> blocks = new HashMap<>();

        public B startBlock(@NonNull Identifier id) {
            B builder = this.blockBuilder(id);
            checkArg(this.blocks.putIfAbsent(id, builder) == null, "ID %s already registered!", id);
            return builder;
        }

        protected abstract B blockBuilder(@NonNull Identifier id);

        public abstract R build();
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static abstract class BlockBuilder<I extends BlockBuilder<I, P, R>, P extends Builder<P, I, R>, R extends BlockRegistry> {
        @NonNull
        protected final P parent;
        @NonNull
        protected final Identifier id;
        @Getter(AccessLevel.NONE)
        protected Property<?>[] properties = new Property[0];

        public I properties(@NonNull Property<?>... properties) {
            this.properties = Arrays.stream(properties).distinct().toArray(Property[]::new);
            return uncheckedCast(this);
        }

        protected DefaultBlockState bake(@NonNull R registry) {
            this.validateState();
            if (this.properties.length == 0) {
                DefaultBlockState state = this.makeState(registry, Collections.emptyMap());
                Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates = new HashMap<>();
                propertiesToStates.put(Collections.emptyMap(), state);
                state.otherMeta = this.getMetaArray(propertiesToStates);
                state.propertiesByName = Collections.emptyMap();
                state.propertyValues = Collections.emptyMap();
                return state;
            } else {
                LinkedHashMap<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates = new LinkedHashMap<>();
                this.allProperties().forEach(p -> propertiesToStates.put(p, this.makeState(registry, p)));
                propertiesToStates.forEach((p, state) -> {
                    for (Property<?> property : this.properties) {
                        state.otherProperties.put(property, property.propertyMap(value -> {
                            Map<Property<?>, Object> p2 = new HashMap<>(p);
                            p2.put(property, value);
                            return propertiesToStates.get(p2);
                        }));
                    }
                });

                BlockState[] metas = this.getMetaArray(propertiesToStates);
                Collection<Property<?>> properties = Arrays.asList(this.properties);
                Map<String, Property<?>> propertiesByName = properties.stream().collect(Collectors.toMap(Property::name, PFunctions.identity()));
                propertiesToStates.forEach((map, state) -> {
                    state.otherMeta = metas;
                    state.propertiesByName = propertiesByName;
                    state.propertyValues = map;
                });
                return this.getDefaultState(propertiesToStates, metas);
            }
        }

        protected abstract void validateState();

        protected abstract DefaultBlockState makeState(@NonNull R registry, @NonNull Map<Property<?>, ?> properties);

        protected BlockState[] getMetaArray(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates) {
            BlockState[] metas = new BlockState[propertiesToStates.size()];
            propertiesToStates.values().forEach(state -> {
                int meta = state.meta();
                checkState(metas[meta] != null, "duplicate meta value: %d", meta);
                metas[meta] = state;
            });
            return metas;
        }

        protected DefaultBlockState getDefaultState(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates, @NonNull BlockState[] metas) {
            return (DefaultBlockState) metas[0];
        }

        protected List<Map<Property<?>, ?>> allProperties() {
            if (this.properties.length == 0) {
                return Collections.emptyList();
            }
            List<Map<Property<?>, ?>> allProperties = new ArrayList<>((int) Arrays.stream(this.properties).map(Property::values).mapToLong(Stream::count).reduce(1L, (a, b) -> a * b));
            this.forEachRecursive(values -> {
                Map<Property<?>, Object> map = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    map.put(this.properties[i], values[i]);
                }
                allProperties.add(map);
            });
            return allProperties;
        }

        protected void forEachRecursive(@NonNull Consumer<Object[]> callback, @NonNull Object... args) {
            if (args.length == this.properties.length) {
                callback.accept(args);
            } else {
                Object[] big = new Object[args.length + 1];
                System.arraycopy(args, 0, big, 0, args.length);
                this.properties[args.length].values().forEach(value -> {
                    big[args.length] = value;
                    this.forEachRecursive(callback, big);
                });
            }
        }
    }
}
