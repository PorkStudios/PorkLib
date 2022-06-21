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

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Getter
public final class GeneratorConfig implements Configurable<GeneratorConfig, JsonObject> {
    public static final GeneratorConfig DEFAULT = builder().build();

    @Singular
    @NonNull
    private final Set<String> ignoredTokens;

    @Builder.Default
    @NonNull
    private final LicenseConfig license = LicenseConfig.DEFAULT;

    @Builder.Default
    @NonNull
    private final ImportsConfig imports = ImportsConfig.DEFAULT;

    @Builder.Default
    @NonNull
    private final OverrideReplacer overrideReplacer = OverrideReplacer.DEFAULT;

    @Override
    public GeneratorConfig mergeConfiguration(@NonNull JsonObject jsonObject) {
        GeneratorConfigBuilder builder = this.toBuilder();

        if (jsonObject.has("ignored_tokens")) {
            jsonObject.getAsJsonArray("ignored_tokens").forEach(element -> builder.ignoredToken(element.getAsString()));
        }

        if (jsonObject.has("license")) {
            builder.license(this.license.mergeConfiguration(jsonObject.getAsJsonObject("license")));
        }

        if (jsonObject.has("imports")) {
            builder.imports(this.imports.mergeConfiguration(jsonObject.getAsJsonArray("imports")));
        }

        if (jsonObject.has("overrides")) {
            builder.overrideReplacer(this.overrideReplacer.mergeConfiguration(jsonObject.getAsJsonObject("overrides")));
        }

        return builder.build();
    }

    @Override
    public Stream<Path> potentiallyAffectedByFiles() {
        return Stream.of(this.license, this.imports, this.overrideReplacer).flatMap(Configurable::potentiallyAffectedByFiles);
    }
}
