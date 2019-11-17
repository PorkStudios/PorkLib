/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.entity.content.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

/**
 * An enum containing some common content types.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public enum StandardContentType implements ContentType {
    TEXT_PLAIN("text/plain"),
    TEXT_PLAIN_UTF8("text/plain", "UTF-8"),
    TEXT_PLAIN_ASCII("text/plain", "US-ASCII"),
    TEXT_HTML("text/html"),
    TEXT_HTML_UTF8("text/html", "UTF-8"),
    TEXT_HTML_ASCII("text/html", "US-ASCII"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_UTF8("application/json", "UTF-8"),
    APPLICATION_JSON_ASCII("application/json", "US-ASCII"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    protected static final Map<String, Map<String, StandardContentType>> TYPES_LOOKUP = Arrays.stream(StandardContentType.values())
            .collect(Collector.of(
                    HashMap::new,
                    (map, type) -> map.computeIfAbsent(type.mimeType, s -> new HashMap<>()).put(type.charset == null ? "" : type.charset, type),
                    (a, b) -> {
                        a.putAll(b);
                        return a;
                    }
            ));

    protected final String mimeType;
    protected final String charset;
    protected final String formatted;

    StandardContentType(@NonNull String mimeType) {
        this(mimeType, null);
    }

    StandardContentType(@NonNull String mimeType, String charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.formatted = this.charset == null ? this.mimeType : String.format("%s; charset=%s", this.mimeType, this.charset);
    }
}
