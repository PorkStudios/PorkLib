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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.protocol.generator.Cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A (rather incomplete) parser for SRG mappings. Currently only used to translate class names
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Mappings {
    private static final Set<String> FLIPPED = new HashSet<String>() {
        {
            this.add("1.12.2");
        }
    };

    private Map<String, String> classes = new HashMap<>();
    private Map<String, Map<String, String>> fields = new HashMap<>();

    public static Mappings getMappings(@NonNull String version) {
        try {
            Mappings mappings = new Mappings();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Cache.INSTANCE.getBytes(Cache.InfoType.JAVA_SRG, version))))) {
                String s;
                boolean flipped = FLIPPED.contains(version);
                while ((s = reader.readLine()) != null && !s.isEmpty()) {
                    String[] split = s.split(" ");
                    switch (split[0]) {
                        case "CL:": {
                            if (flipped) {
                                mappings.classes.put(split[2], split[1].replace('/', '.'));
                            } else {
                                mappings.classes.put(split[1], split[2].replace('/', '.'));
                            }
                        }
                        break;
                        case "FD:": {
                            if (flipped) {
                                String clazz = split[1].substring(0, split[1].lastIndexOf('/')).replace('/', '.');
                                String field = split[1].substring(split[1].lastIndexOf('/') + 1, split[1].length());
                                mappings.fields.computeIfAbsent(clazz, a -> new HashMap<>()).put(split[2].split("/")[1], field);
                            } else {
                                String clazz = split[2].substring(0, split[2].lastIndexOf('/')).replace('/', '.');
                                String field = split[2].substring(split[2].lastIndexOf('/') + 1, split[2].length());
                                mappings.fields.computeIfAbsent(clazz, a -> new HashMap<>()).put(split[1].split("/")[1], field);
                            }
                        }
                        break;
                    }
                }
            }
            return mappings;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClass(@NonNull String name) {
        return this.classes.getOrDefault(name, name);
    }

    public String getField(@NonNull String clazz, @NonNull String field) {
        return this.fields.getOrDefault(clazz, Collections.emptyMap()).get(field);
    }
}
