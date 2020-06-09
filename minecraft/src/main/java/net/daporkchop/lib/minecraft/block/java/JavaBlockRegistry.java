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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.property.BooleanPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.EnumPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.IntPropertyImpl;
import net.daporkchop.lib.minecraft.format.common.block.legacy.LegacyBlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.DataVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.concurrent.ObjObjConcurrentHashMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class JavaBlockRegistry {
    private static final Map<String, JavaBlockRegistry> CACHE = new ObjObjConcurrentHashMap<>(); //this has a faster computeIfAbsent implementation

    public static JavaBlockRegistry forVersion(@NonNull JavaVersion versionIn) {
        if (versionIn.data() < DataVersion.DATA_1_12_2) {
            versionIn = JavaVersion.fromName("1.12.2"); //1.12.2 is used as an intermediate translation point for all previous versions
        }
        return CACHE.computeIfAbsent(versionIn.name(), (IOFunction<String, JavaBlockRegistry>) version -> {
            Map<String, JsonBlock> map;
            try (InputStream in = JavaBlockRegistry.class.getResourceAsStream(version + "/blocks.json")) {
                checkArg(in != null, "no registry stored for version: %s", version);
                map = InstancePool.getInstance(Gson.class).fromJson(
                        new InputStreamReader(in, StandardCharsets.UTF_8),
                        new TypeToken<Map<String, JsonBlock>>() {
                        }.getType());
            }
            throw new UnsupportedOperationException();
        });
    }

    @Getter
    private static final class JsonBlock {
        public Map<String, List<String>> properties;
        public List<JsonState> states;
    }

    @Getter
    private static final class JsonState {
        public Map<String, String> properties;

        public int id;

        @SerializedName("default")
        public boolean isDefault = false;
        @SerializedName("virtual")
        public boolean isVirtual = false;
    }
}
