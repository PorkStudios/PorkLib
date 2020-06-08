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

package net.daporkchop.lib.minecraft.format.common.block.legacy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.format.common.block.AbstractBlockRegistry;
import net.daporkchop.lib.minecraft.format.common.block.DefaultBlockState;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.ObjIntMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BlockRegistry} which only supports 4-bit metadata values.
 *
 * @author DaPorkchop_
 */
public class LegacyBlockRegistry extends AbstractBlockRegistry {
    public static Builder builder() {
        return new Builder();
    }

    protected LegacyBlockRegistry(@NonNull Builder builder) {
        super(builder);
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder extends AbstractBlockRegistry.Builder<Builder, BlockBuilder, LegacyBlockRegistry> {
        @Override
        protected BlockBuilder blockBuilder(@NonNull Identifier id) {
            return new BlockBuilder(this, id);
        }

        @Override
        public LegacyBlockRegistry build() {
            return new LegacyBlockRegistry(this);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class BlockBuilder extends AbstractBlockRegistry.BlockBuilder<BlockBuilder, Builder, LegacyBlockRegistry> {
        protected ObjIntMap<Map<Property<?>, ?>> stateToMeta = null;
        protected Map<Property<?>, ?>[] metaToState = null;
        protected Map<Property<?>, ?> defaultState = null;
        protected int legacyId = -1;

        protected BlockBuilder(Builder parent, Identifier id) {
            super(parent, id);
        }

        public BlockBuilder legacyId(int legacyId) {
            this.legacyId = notNegative(legacyId, "legacyId");
            return this;
        }

        public BlockBuilder stateToMeta(@NonNull ObjIntMap<Map<Property<?>, ?>> stateToMeta) {
            this.stateToMeta = stateToMeta;
            return this;
        }

        @SuppressWarnings("unchecked")
        public BlockBuilder metaToState(@NonNull Map<Property<?>, ?>[] metaToState) {
            this.metaToState = Arrays.stream(metaToState).peek(Objects::requireNonNull).toArray(Map[]::new);
            return this;
        }

        public BlockBuilder defaultState(@NonNull Map<Property<?>, ?> defaultState) {
            this.defaultState = defaultState;
            return this;
        }

        @Override
        protected void validateState() {
            checkState(this.legacyId >= 0, "legacyId must be set! (block: %s)", this.id);
            checkState(this.properties.length == 0 || this.stateToMeta != null, "stateToMeta must be set for blocks with properties!");
        }

        @Override
        protected DefaultBlockState makeState(@NonNull LegacyBlockRegistry registry, @NonNull Map<Property<?>, ?> properties) {
            int meta = this.stateToMeta == null ? 0 : this.stateToMeta.get(properties);
            checkState((meta & 0xF) == meta, "invalid meta (%d) for properties: %s", meta, properties);
            return new DefaultBlockState(registry, this.id, this.legacyId, meta, (this.legacyId << 4) | meta);
        }

        @Override
        protected BlockState[] getMetaArray(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates) {
            if (this.metaToState == null) {
                return super.getMetaArray(propertiesToStates);
            }
            return Arrays.stream(this.metaToState)
                    .map(propertiesToStates::get)
                    .peek(Objects::requireNonNull)
                    .toArray(BlockState[]::new);
        }

        @Override
        protected DefaultBlockState getDefaultState(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates, @NonNull BlockState[] metas) {
            if (this.defaultState == null) {
                return super.getDefaultState(propertiesToStates, metas);
            }
            DefaultBlockState state = propertiesToStates.get(this.defaultState);
            checkState(state != null, this.defaultState);
            return state;
        }
    }
}
