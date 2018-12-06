/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.protocol.generator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.StreamUtil;
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cache implements Logging {
    public static final Cache INSTANCE = new Cache();

    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    private static final Map<String, DataInfo> INFO = new HashMap<String, DataInfo>() {
        //Java edition stuff
        {
            //SRG mappings
            this.register(InfoType.JAVA_SRG, "1.10", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.10/1.10-mappings.srg"))
                    .url("https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/d1f0b110533fe549df5725e81c7f01c1a517157e/1.10.srg")
                    .build());
            this.register(InfoType.JAVA_SRG, "1.11.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.11.2/1.11.2-mappings.srg"))
                    .url("https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/d1f0b110533fe549df5725e81c7f01c1a517157e/1.11.2.srg")
                    .build());
            this.register(InfoType.JAVA_SRG, "1.12.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.12.2/1.12.2-mappings.srg"))
                    .url("https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/47aa4d0626855f7be579c1fa2ffeec92655a9def/1.12.2.srg")
                    .build());
            //Burger output
            this.register(InfoType.JAVA_BURGER, "1.10", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.10/1.10-burger.json"))
                    .url("https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.10.json")
                    .build());
            this.register(InfoType.JAVA_BURGER, "1.11.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.11.2/1.11.2-burger.json"))
                    .url("https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.11.2.json")
                    .build());
            this.register(InfoType.JAVA_BURGER, "1.12.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.12.2/1.12.2-burger.json"))
                    .url("https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.12.2.json")
                    .build());
            //Client.jar
            this.register(InfoType.JAVA_CLIENT, "1.10", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.10/1.10-client.jar"))
                    .url("https://launcher.mojang.com/v1/objects/ba038efbc6d9e4a046927a7658413d0276895739/client.jar")
                    .build());
            this.register(InfoType.JAVA_CLIENT, "1.11.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.11.2/1.11.2-client.jar"))
                    .url("https://launcher.mojang.com/v1/objects/db5aa600f0b0bf508aaf579509b345c4e34087be/client.jar")
                    .build());
            this.register(InfoType.JAVA_CLIENT, "1.12.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.12.2/1.12.2-client.jar"))
                    .url("https://launcher.mojang.com/v1/objects/0f275bc1547d01fa5f56ba34bdc87d981ee12daf/client.jar")
                    .build());
            //Soupply data
            this.register(InfoType.JAVA_SOUPPLY, "1.10", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.10/1.10-soupply.xml"))
                    .url("https://raw.githubusercontent.com/sel-project/soupply/master/data/java210.xml")
                    .build());
            this.register(InfoType.JAVA_SOUPPLY, "1.11.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.11.2/1.11.2-soupply.xml"))
                    .url("https://raw.githubusercontent.com/sel-project/soupply/master/data/java316.xml")
                    .build());
            this.register(InfoType.JAVA_SOUPPLY, "1.12.2", DataInfo.builder().file(new File(DataGenerator.IN_ROOT, "java/1.12.2/1.12.2-soupply.xml"))
                    .url("https://raw.githubusercontent.com/sel-project/soupply/master/data/java340.xml")
                    .build());
        }

        private void register(@NonNull InfoType type, @NonNull String version, @NonNull DataInfo info) {
            this.put(String.format("%s-%s", type.name(), version), info);
        }
    };

    public Stream<DataInfo> getAllInfo(@NonNull InfoType type) {
        return INFO.entrySet().stream()
                .filter(e -> e.getKey().startsWith(type.name()))
                .map(Map.Entry::getValue);
    }

    public byte[] getBytes(@NonNull InfoType type, @NonNull String version) {
        return this.getBytes(String.format("%s-%s", type.name(), version));
    }

    public byte[] getBytes(@NonNull String name) {
        //public byte[] getBytes(@NonNull File file, @NonNull String url) {
        return CACHE.computeIfAbsent(name, sdkljflkdjf -> {
            try {
                byte[] b;
                DataInfo info = INFO.get(name);
                if (info.file.exists()) {
                    try (InputStream in = new FileInputStream(info.file)) {
                        b = StreamUtil.readFully(in, -1, false);
                    }
                } else {
                    File parent = info.file.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        throw this.exception("Couldn't create directory: ${0}", parent);
                    } else if (!info.file.createNewFile()) {
                        throw this.exception("Couldn't create file: ${0}", info.file);
                    } else {
                        logger.trace("Downloading ${0}...", info.url);
                        try (OutputStream out = new FileOutputStream(info.file)) {
                            out.write(b = SimpleHTTP.get(info.url));
                        }
                    }
                }
                return b;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public File getFile(@NonNull InfoType type, @NonNull String version) throws IOException {
        return this.getFile(String.format("%s-%s", type.name(), version));
    }

    public File getFile(@NonNull String name) throws IOException {
        DataInfo info = INFO.get(name);
        if (info.file.exists()) {
            try (InputStream in = new FileInputStream(info.file)) {
                StreamUtil.readFully(in, -1, false);
            }
        } else {
            File parent = info.file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw this.exception("Couldn't create directory: ${0}", parent);
            } else if (!info.file.createNewFile()) {
                throw this.exception("Couldn't create file: ${0}", info.file);
            } else {
                logger.trace("Downloading ${0}...", info.url);
                try (OutputStream out = new FileOutputStream(info.file)) {
                    out.write(SimpleHTTP.get(info.url));
                }
            }
        }
        return info.file;
    }

    public enum InfoType {
        JAVA_SRG,
        JAVA_BURGER,
        JAVA_SOUPPLY,
        JAVA_CLIENT;
    }

    @Builder
    @RequiredArgsConstructor
    @Getter
    public static class DataInfo {
        @NonNull
        private final String url;
        @NonNull
        private final File file;
    }
}
