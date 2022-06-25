/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.primitive.generator.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterType;
import net.daporkchop.lib.primitive.generator.param.custom.CustomParameterType;
import net.daporkchop.lib.primitive.generator.param.custom.CustomParameterValue;
import net.daporkchop.lib.primitive.generator.param.primitive.PrimitiveParameterType;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Getter
public final class ParametersConfig implements Configurable<ParametersConfig, JsonElement> {
    public static final ParametersConfig DEFAULT = builder()
            .type("primitive", new PrimitiveParameterType())
            .build();

    @Singular
    @NonNull
    private final Map<String, ParameterType<?>> types;

    @Singular
    @NonNull
    private final List<Parameter<?>> parameters;

    @Override
    public ParametersConfig mergeConfiguration(@NonNull JsonElement jsonElement) {
        ParametersConfigBuilder builder = this.toBuilder();

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.has("types")) {
                jsonObject.getAsJsonObject("types").entrySet().forEach(typeEntry -> {
                    String typeName = typeEntry.getKey();

                    builder.type(typeName, new CustomParameterType(Collections.unmodifiableMap(typeEntry.getValue().getAsJsonObject().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    parameterEntry -> new CustomParameterValue(
                                            parameterEntry.getKey(),
                                            Collections.unmodifiableMap(parameterEntry.getValue().getAsJsonObject().entrySet().stream()
                                                    .collect(Collectors.toMap(
                                                            Map.Entry::getKey,
                                                            propertyEntry -> propertyEntry.getValue().getAsString())))))))));
                });
            }

            if (jsonObject.has("params")) {
                return builder.build().mergeConfiguration(jsonObject.getAsJsonArray("params"));
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            builder.parameters(IntStream.range(0, jsonArray.size())
                    .mapToObj(i -> new Parameter<>(this, jsonArray.get(i), i))
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    @Override
    public Stream<Path> potentiallyAffectedByFiles() {
        return Stream.empty();
    }
}
