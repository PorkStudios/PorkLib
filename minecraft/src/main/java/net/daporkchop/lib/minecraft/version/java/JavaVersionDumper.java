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

package net.daporkchop.lib.minecraft.version.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.binary.oio.writer.UTF8FileWriter;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.misc.file.PFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility to dump the contents of the version.json file for every Minecraft version that supports it.
 *
 * @author DaPorkchop_
 */
final class JavaVersionDumper {
    public static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    public static final File IN = new File("/media/daporkchop/TooMuchStuff/PortableIDE/PorkLib/minecraft/src/main/resources/net/daporkchop/lib/minecraft/version/java_versions_in.json");
    public static final File OUT_ROOT = new File("/media/daporkchop/TooMuchStuff/PortableIDE/PorkLib/minecraft/src/main/resources/net/daporkchop/lib/minecraft/version/java");
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String... args) throws IOException {
        Map<String, ManifestVersion> manifest = getManifestVersions();
        Map<String, DataVersion> data = getDataVersions();
        manifest.values().parallelStream()
                .map(manifestVersion -> {
                    DataVersion dataVersion = data.get(manifestVersion.id);
                    if (dataVersion != null) {
                        return new MergedVersion(manifestVersion, dataVersion);
                    } else {
                        System.out.printf("No version found for \"%s\"\n", manifestVersion.id);
                        return manifestVersion;
                    }
                })
                .forEach((IOConsumer<ManifestVersion>) version -> {
                    try (PAppendable out = new UTF8FileWriter(PFiles.ensureFileExists(new File(OUT_ROOT, version.id + ".json")))) {
                        GSON.toJson(version, out);
                        out.appendLn();
                    }
                });
    }

    public static Map<String, ManifestVersion> getManifestVersions() throws IOException {
        JsonArray versions;
        try (Reader reader = new BufferedReader(new InputStreamReader(new URL(VERSION_MANIFEST_URL).openStream(), StandardCharsets.UTF_8))) {
            versions = JSON_PARSER.parse(reader).getAsJsonObject().getAsJsonArray("versions");
        }
        return StreamSupport.stream(versions.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(obj -> !"snapshot".equals(obj.get("type").getAsString()))
                .map(ManifestVersion::new)
                .collect(Collectors.toMap(v -> v.id, PFunctions.identity()));
    }

    public static Map<String, DataVersion> getDataVersions() throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(IN), StandardCharsets.UTF_8))) {
            return JSON_PARSER.parse(reader).getAsJsonObject().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new DataVersion(e.getKey(), e.getValue().getAsJsonObject())));
        }
    }

    @AllArgsConstructor
    private static class ManifestVersion {
        @NonNull
        private transient final String id;
        private final long releaseTime;

        public ManifestVersion(@NonNull JsonObject obj) {
            this(obj.get("id").getAsString(),
                    ZonedDateTime.parse(obj.get("releaseTime").getAsString()).toInstant().toEpochMilli());
        }
    }

    @AllArgsConstructor
    private static class DataVersion {
        @NonNull
        private final String id;
        private final int protocol;
        private final int data;

        public DataVersion(@NonNull String id, @NonNull JsonObject obj) {
            this(id, obj.get("protocol").getAsInt(), obj.get("data").getAsInt());
        }
    }

    private static class MergedVersion extends ManifestVersion {
        private final int protocolVersion;
        private final int dataVersion;

        public MergedVersion(@NonNull ManifestVersion manifest, @NonNull DataVersion data) {
            super(manifest.id, manifest.releaseTime);

            this.protocolVersion = data.protocol;
            this.dataVersion = data.data;
        }
    }
}
