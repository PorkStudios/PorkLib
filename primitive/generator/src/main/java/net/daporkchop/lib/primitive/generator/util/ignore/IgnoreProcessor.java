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

package net.daporkchop.lib.primitive.generator.util.ignore;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Builder(toBuilder = true)
@Accessors(fluent = true)
public final class IgnoreProcessor {
    public static IgnoreProcessor begin(@NonNull Path rootDirectory) {
        IgnoreProcessorBuilder builder = builder().rootDirectory(rootDirectory).currentDirectory(rootDirectory);
        insertIgnoreEntries(builder, parseIgnoreEntries(rootDirectory).stream());
        return builder.build();
    }

    @SneakyThrows(IOException.class)
    private static List<IgnoreEntry> parseIgnoreEntries(@NonNull Path directory) {
        List<IgnoreEntry> ignoreEntries = Collections.emptyList();
        if (PFiles.checkFileExists(directory.resolve(".generatorignore"))) {
            ignoreEntries = Files.readAllLines(directory.resolve(".generatorignore"), StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isEmpty() && line.charAt(0) != '#')
                    .map(line -> {
                        if (line.startsWith("**/")) { // leading "**/" is redundant
                            line = line.substring(3);
                        }

                        checkState(line.indexOf('*') < 0, "wildcards not supported!");

                        Path path = Paths.get(line);

                        boolean matchOnlyDirectories = line.endsWith("/");
                        boolean absolute = path.isAbsolute();
                        if (absolute) { //make the path not absolute
                            path = path.subpath(0, path.getNameCount());
                        }

                        return path.getNameCount() == 1
                                ? new SingleNameIgnoreEntry(path, absolute, matchOnlyDirectories)
                                : new MultiNameIgnoreEntry(path, absolute, matchOnlyDirectories);
                    })
                    .collect(Collectors.toList());
        }
        return ignoreEntries;
    }

    private static void insertIgnoreEntries(@NonNull IgnoreProcessorBuilder builder, @NonNull Stream<IgnoreEntry> entries) {
        Map<Path, List<IgnoreEntry>> entriesBySimpleName = entries.collect(Collectors.groupingBy(IgnoreEntry::simpleFileName, Collectors.toList()));

        builder.complexIgnoreEntries(PorkUtil.fallbackIfNull(entriesBySimpleName.remove(null), Collections.emptyList()));
        builder.namedIgnoreEntries(entriesBySimpleName);
    }

    @NonNull
    private final Path rootDirectory;
    @NonNull
    private final Path currentDirectory;

    @Singular
    @NonNull
    private final Map<Path, List<IgnoreEntry>> namedIgnoreEntries;
    @Singular
    @NonNull
    private final Collection<IgnoreEntry> complexIgnoreEntries;

    public IgnoreProcessor enterDirectory(@NonNull Path directory) {
        assert directory.startsWith(this.currentDirectory) : this.currentDirectory + " doesn't contain " + directory;

        Path relativePath = this.rootDirectory.relativize(directory);

        IgnoreProcessorBuilder builder = builder().rootDirectory(this.rootDirectory).currentDirectory(directory);
        insertIgnoreEntries(builder, Stream.concat(
                Stream.concat(
                        this.namedIgnoreEntries.values().stream().flatMap(List::stream),
                        this.complexIgnoreEntries.stream())
                        .flatMap(entry -> entry.tryAdvance(this.rootDirectory, directory, relativePath, true).stream())
                        .filter(Objects::nonNull),
                parseIgnoreEntries(directory).stream()));
        return builder.build();
    }

    public boolean shouldIgnore(@NonNull Path path, @NonNull BasicFileAttributes attrs) {
        assert path.startsWith(this.currentDirectory) : this.currentDirectory + " doesn't contain " + path;

        Path relativePath = this.rootDirectory.relativize(path);
        List<? extends IgnoreEntry> namedEntries = this.namedIgnoreEntries.get(path.getFileName());
        return (namedEntries != null
                && namedEntries.stream().anyMatch(entry -> entry.shouldIgnore(this.rootDirectory, path, relativePath, attrs.isDirectory())))
               || this.complexIgnoreEntries.stream().anyMatch(entry -> entry.shouldIgnore(this.rootDirectory, path, relativePath, attrs.isDirectory()));
    }

    /**
     * @author DaPorkchop_
     */
    private interface IgnoreEntry {
        Path simpleFileName();

        boolean shouldIgnore(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory);

        List<IgnoreEntry> tryAdvance(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory);
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    private static class SingleNameIgnoreEntry implements IgnoreEntry {
        @NonNull
        private final Path simpleFileName;
        private final boolean absolute;
        private final boolean directory;

        @Override
        public boolean shouldIgnore(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory) {
            return this.simpleFileName.equals(originalPath.getFileName()) && (!this.directory || directory);
        }

        @Override
        public List<IgnoreEntry> tryAdvance(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory) {
            return this.absolute ? Collections.emptyList() : Collections.singletonList(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    private static class MultiNameIgnoreEntry implements IgnoreEntry {
        @NonNull
        private final Path path;
        private final boolean absolute;
        private final boolean directory;

        @Override
        public Path simpleFileName() {
            return this.path.getName(0);
        }

        @Override
        public boolean shouldIgnore(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory) {
            return false;
        }

        @Override
        public List<IgnoreEntry> tryAdvance(@NonNull Path rootDirectory, @NonNull Path originalPath, @NonNull Path relativePath, boolean directory) {
            if (directory && this.path.getName(0).equals(originalPath.getFileName())) {
                IgnoreEntry next = this.path.getNameCount() == 2
                        ? new SingleNameIgnoreEntry(this.path.getFileName(), true, this.directory)
                        : new MultiNameIgnoreEntry(this.path.subpath(1, this.path.getNameCount()), true, this.directory);

                return this.absolute ? Collections.singletonList(next) : Arrays.asList(this, next);
            } else {
                return this.absolute ? Collections.emptyList() : Collections.singletonList(this);
            }
        }
    }
}
