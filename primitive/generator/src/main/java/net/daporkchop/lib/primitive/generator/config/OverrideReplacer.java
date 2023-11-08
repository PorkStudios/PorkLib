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

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.primitive.generator.Replacer;

import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Getter
public final class OverrideReplacer implements Replacer, Configurable<OverrideReplacer, JsonObject> {
    private static final Cached<Matcher> NAME_MATCHER_CACHE = Cached.regex(Pattern.compile("(?:[a-z]+\\.)*(?<![a-zA-Z])([A-Z][a-zA-Z]+)"));

    public static final OverrideReplacer DEFAULT = builder().build();

    @Singular
    @NonNull
    private final Map<String, String> overrides;

    @Override
    public OverrideReplacer mergeConfiguration(@NonNull JsonObject jsonObject) {
        OverrideReplacerBuilder builder = this.toBuilder();
        jsonObject.entrySet().forEach(entry -> builder.override(entry.getKey(), entry.getValue().getAsString()));
        return builder.build();
    }

    @Override
    public Stream<Path> potentiallyAffectedByFiles() {
        return Stream.empty();
    }

    @Override
    public String processName(@NonNull String name, int index, StringBuffer buffer) {
        return this.overrides.containsKey(name) ? null : name;
    }

    @Override
    public String processCode(@NonNull String code, int index, StringBuffer buffer) {
        Matcher matcher = NAME_MATCHER_CACHE.get().reset(code);
        if (matcher.find()) {
            buffer.setLength(0);
            do {
                String overriddenName = this.overrides.get(matcher.group(1));
                matcher.appendReplacement(buffer, overriddenName == null ? matcher.group() : overriddenName);
            } while (matcher.find());
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return code;
    }
}
