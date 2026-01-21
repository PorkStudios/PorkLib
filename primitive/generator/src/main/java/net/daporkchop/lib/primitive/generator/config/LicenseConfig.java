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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.primitive.generator.Generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Getter
public final class LicenseConfig implements Configurable<LicenseConfig, JsonObject> {
    public static final LicenseConfig DEFAULT = builder().build();

    private static final Map<String, String> REPLACEMENTS = new HashMap<>();
    private static final Cached<Matcher> REPLACEMENT_MATCHER_CACHE;

    static {
        //register all replacements
        REPLACEMENTS.put("$today.year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        //build a pattern which matches any of the replacement keys
        REPLACEMENT_MATCHER_CACHE = Cached.regex(REPLACEMENTS.keySet().stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|")));
    }

    @Singular
    @NonNull
    private final List<String> lines;

    @Builder.Default
    private final Path licenseFilePath = null;

    @Override
    @SneakyThrows(IOException.class)
    public LicenseConfig mergeConfiguration(@NonNull JsonObject jsonObject) {
        LicenseConfigBuilder builder = builder();

        if (jsonObject.has("licenseFile")) { //read all lines from the given license file
            Path licenseFilePath = PFiles.assertFileExists(Paths.get(jsonObject.getAsJsonPrimitive("licenseFile").getAsString()));
            builder.licenseFilePath(licenseFilePath);
            builder.lines(Files.readAllLines(licenseFilePath, StandardCharsets.UTF_8));
        } else if (jsonObject.has("licenseContents")) { //set the license to the contents of the given json object
            for (JsonElement line : jsonObject.getAsJsonArray("licenseContents")) {
                builder.line(line.getAsString());
            }
        }

        //run replacer on all license lines
        builder.lines.replaceAll(original -> Generator.replace(REPLACEMENT_MATCHER_CACHE.get(), original, matcher -> REPLACEMENTS.get(matcher.group())));

        return builder.build();
    }

    @Override
    public Stream<Path> potentiallyAffectedByFiles() {
        return this.licenseFilePath != null ? Stream.of(this.licenseFilePath) : Stream.empty();
    }

    @SneakyThrows(IOException.class)
    public void appendLicense(@NonNull Appendable dst) {
        if (!this.lines.isEmpty()) {
            dst.append("/*");
            this.lines.forEach((IOConsumer<String>) line -> dst.append("\n * ").append(line));
            dst.append("\n */\n");
        }
    }
}
