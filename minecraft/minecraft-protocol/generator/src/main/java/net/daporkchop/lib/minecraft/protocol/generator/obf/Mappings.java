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

package net.daporkchop.lib.minecraft.protocol.generator.obf;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.minecraft.protocol.generator.Cache;
import net.daporkchop.lib.minecraft.protocol.generator.DataGenerator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A (rather incomplete) parser for SRG mappings. Currently only used to translate class names
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mappings {
    private static final Map<String, String> URLS = new HashMap<String, String>() {
        {
            this.put("1.10", "https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/d1f0b110533fe549df5725e81c7f01c1a517157e/1.10.srg");
            this.put("1.11.2", "https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/d1f0b110533fe549df5725e81c7f01c1a517157e/1.11.2.srg");
            this.put("1.12.2", "https://gist.githubusercontent.com/DaMatrix/cc72576c095d6099794f9ada5e7aa381/raw/076945713b3ad73ced1b87c791a243f3035d318b/1.12.2.srg");
        }
    };
    private static final Map<String, Mappings> MAPPINGS = new ConcurrentHashMap<>();

    private Map<String, String> classes = new HashMap<>();
    private Map<String, String> fields = new HashMap<>();

    public static Mappings getMappings(@NonNull String inVersion) {
        return MAPPINGS.computeIfAbsent(inVersion, version -> {
            try {
                Mappings mappings = new Mappings();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Cache.INSTANCE.getOrLoad(new File(DataGenerator.IN_ROOT, String.format("../mappings/%s.srg", version)), URLS.get(version)))))) {
                    String s;
                    while ((s = reader.readLine()) != null && !s.isEmpty()) {
                        String[] split = s.split(" ");
                        switch (split[0]) {
                            case "CL:": {
                                mappings.classes.put(split[1], split[2].replace('/', '.'));
                            }
                            break;
                            case "FD:": {
                                mappings.fields.put(split[1], split[2].replace('/', '.'));
                            }
                            break;
                        }
                    }
                }
                return mappings;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getClass(@NonNull String name)    {
        return this.classes.getOrDefault(name, name);
    }

    public String getField(@NonNull String name)    {
        return this.classes.getOrDefault(name, name);
    }
}
