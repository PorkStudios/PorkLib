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

package net.daporkchop.lib.minecraft.block.java;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.property.BooleanPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.EnumPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.IntPropertyImpl;
import net.daporkchop.lib.minecraft.format.common.block.AbstractBlockRegistry;
import net.daporkchop.lib.minecraft.format.common.block.DefaultBlockState;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.java.JavaRegistries;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.DataVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.primitive.map.concurrent.ObjObjConcurrentHashMap;
import net.daporkchop.lib.reflection.type.PTypes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class JavaBlockRegistry extends AbstractBlockRegistry {
    public static JavaBlockRegistry.Builder builder() {
        return new Builder();
    }

    protected JavaBlockRegistry(@NonNull Builder builder) {
        super(builder);
    }

    private static final Map<String, BlockRegistry> CACHE = new ObjObjConcurrentHashMap<>(); //this has a faster computeIfAbsent implementation
    private static final Type BLOCK_MAP_TYPE = PTypes.parameterized(Map.class, String.class, JsonBlock.class);

    public static BlockRegistry forVersion(@NonNull JavaVersion versionIn) {
        if (versionIn.data() < DataVersion.DATA_1_12_2) {
            versionIn = JavaVersion.fromName("1.12.2"); //1.12.2 is used as an intermediate translation point for all legacy versions
        }
        return CACHE.computeIfAbsent(versionIn.name(), (IOFunction<String, BlockRegistry>) version -> {
            Map<String, JsonBlock> map;
            try (InputStream in = JavaBlockRegistry.class.getResourceAsStream(version + "/blocks.json")) {
                checkArg(in != null, "no registry stored for version: %s", version);
                map = InstancePool.getInstance(Gson.class).fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), BLOCK_MAP_TYPE);
            }

            Registry legacyBlockRegistry = JavaRegistries.forVersion(JavaVersion.fromName(version)).get(BlockRegistry.ID);

            Builder builder = builder();
            map.forEach((name, block) -> {
                Identifier id = Identifier.fromString(name);
                BlockBuilder blockBuilder = builder.startBlock(id);

                Map<String, Property<?>> propertyLookup = block.properties.entrySet().stream()
                        .map(e -> makeProperty(Identifier.fromString(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Property::name, PFunctions.identity()));

                blockBuilder.propertyLookup(propertyLookup)
                        .states(block.states)
                        .legacyId(legacyBlockRegistry.get(id));
            });
            return builder.build();
        });
    }

    private static Property<?> makeProperty(@NonNull Identifier name, @NonNull List<String> values) {
        try {
            int min = values.stream().mapToInt(Integer::parseUnsignedInt).min().orElse(0);
            int max = values.stream().mapToInt(Integer::parseUnsignedInt).max().orElse(0) + 1;
            return new IntPropertyImpl(name.getName(), min, max);
        } catch (NumberFormatException ignored) {
        }

        if (values.size() == 2 && values.contains("true") && values.contains("false")) {
            return new BooleanPropertyImpl(name.getName());
        } else {
            return new EnumPropertyImpl(name.getName(), values);
        }
    }

    @Getter
    public static final class JsonBlock {
        public Map<String, List<String>> properties = Collections.emptyMap();
        public List<JsonState> states;
    }

    @Getter
    public static final class JsonState {
        public Map<String, String> properties = Collections.emptyMap();

        public int id;

        @SerializedName("default")
        public boolean isDefault = false;
        @SerializedName("virtual")
        public boolean isVirtual = false;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder extends AbstractBlockRegistry.Builder<JavaBlockRegistry.Builder, JavaBlockRegistry.BlockBuilder, JavaBlockRegistry> {
        @Override
        protected JavaBlockRegistry.BlockBuilder blockBuilder(@NonNull Identifier id) {
            return new JavaBlockRegistry.BlockBuilder(this, id);
        }

        @Override
        public JavaBlockRegistry build() {
            return new JavaBlockRegistry(this);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class BlockBuilder extends AbstractBlockRegistry.BlockBuilder<JavaBlockRegistry.BlockBuilder, JavaBlockRegistry.Builder, JavaBlockRegistry> {
        protected Map<String, Property<?>> propertyLookup;
        protected List<JsonState> statesList;
        protected Map<Map<Property<?>, ?>, JsonState> states;
        protected int legacyId = -1;
        protected int firstRuntimeId = -1;

        protected BlockBuilder(JavaBlockRegistry.Builder parent, Identifier id) {
            super(parent, id);
        }

        public JavaBlockRegistry.BlockBuilder legacyId(int legacyId) {
            this.legacyId = notNegative(legacyId, "legacyId");
            return this;
        }

        public JavaBlockRegistry.BlockBuilder propertyLookup(@NonNull Map<String, Property<?>> propertyLookup) {
            this.propertyLookup = propertyLookup;
            this.properties = propertyLookup.values().toArray(new Property[propertyLookup.size()]);
            return this;
        }

        public JavaBlockRegistry.BlockBuilder states(@NonNull List<JsonState> states) {
            this.firstRuntimeId = states.stream().mapToInt(JsonState::id).min().orElseThrow(IllegalArgumentException::new);
            this.statesList = states;
            return this;
        }

        @Override
        protected void validateState() {
            checkState(this.legacyId >= 0, "legacyId must be set! (block: %s)", this.id);
            checkState(this.propertyLookup != null, "propertyLookup must be set! (block: %s)", this.id);
            checkState(this.statesList != null, "states must be set! (block: %s)", this.id);

            this.states = this.statesList.stream().collect(Collectors.toMap(
                    state -> state.properties.entrySet().stream().collect(Collectors.toMap(
                            e -> this.propertyLookup.get(e.getKey()),
                            e -> this.propertyLookup.get(e.getKey()).decodeValue(e.getValue()))),
                    PFunctions.identity()));
        }

        @Override
        protected DefaultBlockState makeState(@NonNull JavaBlockRegistry registry, @NonNull Map<Property<?>, ?> properties) {
            JsonState state = this.states.get(properties);
            int meta = state.id - this.firstRuntimeId;
            return new DefaultBlockState(registry, this.id, this.legacyId, meta, state.id);
        }

        @Override
        protected BlockState[] getMetaArray(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates) {
            BlockState[] arr = new BlockState[propertiesToStates.size()];
            propertiesToStates.values().forEach(state -> arr[state.runtimeId() - this.firstRuntimeId] = state);
            return arr;
        }

        @Override
        protected DefaultBlockState getDefaultState(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates, @NonNull BlockState[] metas) {
            return propertiesToStates.entrySet().stream()
                    .filter(e -> this.states.get(e.getKey()).isDefault)
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElseThrow(IllegalStateException::new);
        }
    }
}
