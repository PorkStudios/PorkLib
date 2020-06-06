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

package net.daporkchop.lib.minecraft.format.common.block;

import jdk.nashorn.internal.ir.Block;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
@Accessors(fluent = true)
public abstract class AbstractBlockRegistry implements BlockRegistry {
    protected final Map<Identifier, BlockState> idToDefaultState = new HashMap<>();
    @Getter
    protected final BlockState air;

    protected <I extends Builder<I, B, R>, B extends BlockBuilder<B, I, R>, R extends BlockRegistry> AbstractBlockRegistry(@NonNull I builder) {
        builder.blocks.forEach((id, block) -> this.idToDefaultState.put(id, block.bake(uncheckedCast(this))));

        this.air = this.getDefaultState(Identifier.fromString("minecraft:air"));
    }

    @Override
    public boolean containsBlockId(@NonNull Identifier blockId) {
        return this.idToDefaultState.containsKey(blockId);
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
    public static abstract class BlockBuilder<I extends BlockBuilder<I, P, R>, P extends Builder<P, I, R>, R extends BlockRegistry> {
        @NonNull
        protected final P parent;
        @NonNull
        protected final Identifier id;
        protected Property<?>[] properties = new Property[0];

        public I properties(@NonNull Property<?>... properties) {
            this.properties = Arrays.stream(properties).distinct().toArray(Property[]::new);
            return uncheckedCast(this);
        }

        protected BlockState bake(@NonNull R registry) {
            LinkedHashMap<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates = new LinkedHashMap<>();
            this.allProperties().forEach(p -> propertiesToStates.put(p, this.makeState(registry, p)));
            propertiesToStates.forEach((p, state) -> {
                for (Property<?> property : this.properties)    {
                    state.otherProperties.put(property, property.propertyMap(value -> {
                        Map<Property<?>, Object> p2 = new HashMap<>(p);
                        p2.put(property, value);
                        return propertiesToStates.get(p2);
                    }));
                }
            });
            return propertiesToStates.values().iterator().next();
        }

        protected abstract DefaultBlockState makeState(@NonNull R registry, @NonNull Map<Property<?>, ?> properties);

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
