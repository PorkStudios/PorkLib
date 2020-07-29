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

package net.daporkchop.lib.minecraft.block.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.binary.oio.writer.UTF8FileWriter;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to dump the contents of the version.json file for every Minecraft version that supports it.
 *
 * @author DaPorkchop_
 */
class JavaConversionRegistryGenerator {
    public static final String[] VERSIONS = {
            "1.13",
            "1.13.1",
            "1.13.2",
            "1.14",
            "1.14.1",
            "1.14.2",
            "1.14.3",
            "1.14.4",
            "1.15",
            "1.15.1",
            "1.15.2",
            "1.16",
            "1.16.1"
    };

    public static final File OUT_ROOT = new File("/media/daporkchop/TooMuchStuff/PortableIDE/PorkLib/minecraft/src/main/resources/net/daporkchop/lib/minecraft/version/java/conv");
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String... args) throws IOException {
        for (int vi = 1; vi < VERSIONS.length; vi++)    {
            JavaVersion prevVersion = JavaVersion.fromName(VERSIONS[vi - 1]);
            JavaVersion currVersion = JavaVersion.fromName(VERSIONS[vi]);
            BlockRegistry prevRegistry = JavaBlockRegistry.forVersion(prevVersion);
            BlockRegistry currRegistry = JavaBlockRegistry.forVersion(currVersion);
            Map<Integer, Integer> map = new HashMap<>(prevRegistry.states());
            prevRegistry.forEachState(state -> {
                BlockState nextState = currRegistry.getDefaultState(state.id());
                for (Property property : state.properties())    {
                    nextState = nextState.withProperty(property.name(), state.propertyValue(property).toString());
                }
                map.put(state.runtimeId(), nextState.runtimeId());
            });
            try (PAppendable out = new UTF8FileWriter(PFiles.ensureFileExists(new File(OUT_ROOT, VERSIONS[vi] + ".json")))) {
                GSON.toJson(map, out);
            }
        }
    }
}
