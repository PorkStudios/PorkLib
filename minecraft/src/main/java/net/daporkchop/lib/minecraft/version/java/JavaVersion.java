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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.collections.map.PMaps;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.minecraft.version.MinecraftEdition;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A version of Minecraft: Java Edition.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class JavaVersion extends MinecraftVersion {
    private static final Map<String, JavaVersion> NAME_CACHE = PMaps.readWriteAuto(new HashMap<>());

    public static JavaVersion fromName(@NonNull String name) {
        JavaVersion version = NAME_CACHE.get(name);
        if (version == null) { //compute
            try (InputStream in = JavaVersion.class.getResourceAsStream(name + ".json")) {
                if (in == null) { //file wasn't found on disk
                    version = new JavaVersion(name, -1L);
                } else {
                    JsonObject obj = new JsonParser().parse(new String(StreamUtil.toByteArray(in), StandardCharsets.UTF_8)).getAsJsonObject();
                    int protocol = obj.has("protocolVersion") ? obj.get("protocolVersion").getAsInt() : -1;
                    int data = obj.has("dataVersion") ? obj.get("dataVersion").getAsInt() : -1;
                    version = new JavaVersion(name, obj.get("releaseTime").getAsLong(), protocol, data);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            version = PorkUtil.fallbackIfNull(NAME_CACHE.putIfAbsent(name, version), version); //some shitty form of atomicity
        }
        return version;
    }

    protected final int protocol;
    protected final int data;
    protected final String toString;

    protected JavaVersion(String name, long releaseTime) {
        this(name, releaseTime, -1, -1);
    }

    protected JavaVersion(String name, long releaseTime, int protocol, int data) {
        super(MinecraftEdition.JAVA, name != null ? name : "", releaseTime);

        this.protocol = protocol;
        this.data = data;

        if (name != null) {
            this.toString = "Java Edition " + name;
        } else if (data > 0) {
            this.toString = "Java Edition [unknown] (data version " + data + ')';
        } else if (protocol > 0) {
            this.toString = "Java Edition [unknown] (protocol " + data + ')';
        } else {
            this.toString = "Java Edition [unknown]";
        }
    }
}
