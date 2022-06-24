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

package net.daporkchop.lib.primitive.generator.param;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.primitive.generator.Generator;
import net.daporkchop.lib.primitive.generator.config.ParametersConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.NONE)
@Data
@Accessors(fluent = true)
public class Parameter<O extends ParameterOptions> {
    private final int index;

    private final ParameterType<O> type;
    private final Set<ParameterValue<O>> values;
    private final O options;

    public Parameter(@NonNull ParametersConfig config, @NonNull JsonElement element, int index) {
        this.index = index;

        if (element.isJsonPrimitive()) {
            JsonObject object = new JsonObject();
            object.add("type", element);
            element = object;
        }

        checkArg(element.isJsonObject(), "not a json object: %s", element);
        JsonObject object = element.getAsJsonObject();

        this.type = uncheckedCast(config.getTypes().get(object.getAsJsonPrimitive("type").getAsString()));

        if (object.has("whitelist")) {
            this.values = Collections.unmodifiableSet(StreamSupport.stream(object.getAsJsonArray("whitelist").spliterator(), false)
                    .map(JsonElement::getAsString)
                    .map(this.type.getValuesByName()::get)
                    .peek(Objects::requireNonNull)
                    .collect(Collectors.toSet()));
        } else if (object.has("blacklist")) {
            this.values = Collections.unmodifiableSet(this.type.getValuesByName().values().stream()
                    .filter(((Predicate<? super ParameterValue<O>>) StreamSupport.stream(object.getAsJsonArray("blacklist").spliterator(), false)
                            .map(JsonElement::getAsString)
                            .map(this.type.getValuesByName()::get)
                            .peek(Objects::requireNonNull)
                            .collect(Collectors.toSet())::contains).negate())
                    .collect(Collectors.toSet()));
        } else {
            this.values = Collections.unmodifiableSet(new HashSet<>(this.type.getValuesByName().values()));
        }

        this.options = object.has("options")
                ? Generator.GSON.fromJson(object.get("options"), this.type.optionsClass())
                : this.type.defaultOptions();
    }
}
