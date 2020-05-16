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

package net.daporkchop.lib.minecraft.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
final class DefaultVersion implements MinecraftVersion {
    protected static final Map<String, DefaultVersion> FROM_NAME = new HashMap<>();
    protected static final Map<Integer, DefaultVersion> FROM_DATA_VERSION = new HashMap<>();

    static {
        try {
            JsonArray java;
            try (Reader reader = new BufferedReader(new InputStreamReader(DefaultVersion.class.getResourceAsStream("java.json")))) {
                java = new JsonParser().parse(reader).getAsJsonArray();
            }
            StreamSupport.stream(java.spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .map(obj -> new DefaultVersion(MinecraftEdition.JAVA, obj.get("name").getAsString(), obj.get("data").getAsInt(), obj.get("protocol").getAsInt(), false))
                    .forEach(version -> {
                        FROM_NAME.put(version.name, version);
                        FROM_DATA_VERSION.put(version.data, version);
                    });
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @NonNull
    protected final MinecraftEdition edition;
    protected final String name;
    protected final int data;
    protected final int protocol;
    protected final boolean snapshot;

    @Override
    public String name() {
        return PorkUtil.fallbackIfNull(this.name, "(unknown)");
    }

    @Override
    public int hashCode() {
        return ((this.edition.ordinal() * 31 + this.name().hashCode()) * 31 + this.data) * 31 + this.protocol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MinecraftVersion) {
            MinecraftVersion version = (MinecraftVersion) obj;
            return this.edition == version.edition() && (this.name().equals(version.name()) || this.data == version.data());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            builder.append(this.edition).append(" edition ");
            if (this.name != null) {
                builder.append('v').append(this.name).append(' ');
            } else {
                builder.append("(unknown version) ");
            }
            if (this.snapshot) {
                builder.append("(snapshot) ");
            }
            if (this.data > 0 || this.protocol > 0) {
                builder.append('(');
                if (this.data > 0) {
                    builder.append("data version ").append(this.data);
                }
                if (this.protocol > 0) {
                    if (this.data > 0) {
                        builder.append(',').append(' ');
                    }
                    builder.append("protocol ").append(this.protocol);
                }
                builder.append(')');
            }
            return builder.toString();
        }
    }

    @Override
    public int compareTo(@NonNull MinecraftVersion o) {
        if (this.edition == o.edition()) {
            if (this.data == o.data()) {
                return this.name.compareTo(o.name());
            } else {
                return Integer.compare(this.data, o.data());
            }
        } else {
            return Integer.compare(this.edition.ordinal(), o.edition().ordinal());
        }
    }
}
