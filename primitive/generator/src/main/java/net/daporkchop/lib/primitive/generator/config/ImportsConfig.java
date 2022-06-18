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
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.function.io.IOConsumer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedSet;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Getter
public final class ImportsConfig implements Configurable<ImportsConfig, JsonArray> {
    public static final ImportsConfig DEFAULT = builder().build();

    @Singular
    @NonNull
    private final SortedSet<String> regularImports;

    @Singular
    @NonNull
    private final SortedSet<String> staticImports;

    @Override
    public ImportsConfig mergeConfiguration(@NonNull JsonArray jsonElements) {
        ImportsConfigBuilder builder = this.toBuilder();
        jsonElements.forEach(element -> {
            String entry = element.getAsString();
            assert entry.indexOf('.') >= 0 : "invalid import: " + entry;

            if (entry.startsWith("static ")) {
                assert entry.endsWith(".*") : "invalid static import: " + entry;

                builder.staticImport(entry.substring("static ".length()));
            } else {
                builder.regularImport(entry);
            }
        });
        return builder.build();
    }

    @Override
    public Stream<Path> potentiallyAffectedByFiles() {
        return Stream.empty();
    }

    @SneakyThrows(IOException.class)
    public void appendImports(@NonNull Appendable dst) {
        this.regularImports.forEach((IOConsumer<String>) entry -> dst.append("import ").append(entry).append(";\n"));

        if (!this.regularImports.isEmpty() && !this.staticImports.isEmpty()) {
            dst.append('\n');
        }

        this.staticImports.forEach((IOConsumer<String>) entry -> dst.append("import static ").append(entry).append(";\n"));
    }
}
