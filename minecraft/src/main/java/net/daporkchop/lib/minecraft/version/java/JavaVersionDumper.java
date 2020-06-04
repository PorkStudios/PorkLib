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
    public static final File IN = new File("/media/daporkchop/TooMuchStuff/PortableIDE/PorkLib/minecraft/src/main/resources/net/daporkchop/lib/minecraft/version/java/versions_in.json");
    public static final File OUT_ROOT = new File("/media/daporkchop/TooMuchStuff/PortableIDE/PorkLib/minecraft/src/main/resources/net/daporkchop/lib/minecraft/version/java");
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String... args) throws IOException {
        Map<String, ManifestVersion> manifest = getManifestVersions();
        Map<String, DataVersion> data = getDataVersions();
        data.values().parallelStream()
                .map(dataVersion -> {
                    ManifestVersion manifestVersion = manifest.get(dataVersion.id);
                    if (manifestVersion != null)    {
                        return new MergedVersion(manifestVersion, dataVersion);
                    } else {
                        System.out.printf("No version found for \"%s\"\n", dataVersion.id);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach((IOConsumer<MergedVersion>) version -> {
                    try (PAppendable out = new UTF8FileWriter(PFiles.ensureFileExists(new File(OUT_ROOT, version.id + ".json"))))   {
                        GSON.toJson(version, out);
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
                .collect(Collectors.toMap(obj -> obj.get("id").getAsString(), ManifestVersion::new));
    }

    public static Map<String, DataVersion> getDataVersions() throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(IN), StandardCharsets.UTF_8))) {
            return JSON_PARSER.parse(reader).getAsJsonObject().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new DataVersion(e.getKey(), e.getValue().getAsJsonObject())));
        }
    }

    @AllArgsConstructor
    private static final class ManifestVersion {
        @NonNull
        private final String id;
        private final long releaseTime;
        private final boolean snapshot;

        public ManifestVersion(@NonNull JsonObject obj) {
            this(obj.get("id").getAsString(),
                    ZonedDateTime.parse(obj.get("releaseTime").getAsString()).toInstant().toEpochMilli(),
                    "snapshot".equals(obj.get("type").getAsString()));
        }
    }

    @AllArgsConstructor
    private static final class DataVersion {
        @NonNull
        private final String id;
        private final int protocol;
        private final int data;

        public DataVersion(@NonNull String id, @NonNull JsonObject obj) {
            this(id, obj.get("protocol").getAsInt(), obj.get("data").getAsInt());
        }
    }

    @AllArgsConstructor
    private static final class MergedVersion {
        @NonNull
        private transient final String id;
        private final long releaseTime;
        private final int protocol;
        private final int data;
        private final boolean snapshot;

        public MergedVersion(@NonNull ManifestVersion manifest, @NonNull DataVersion data) {
            this(data.id, manifest.releaseTime, data.protocol, data.data, manifest.snapshot);
        }
    }
}
