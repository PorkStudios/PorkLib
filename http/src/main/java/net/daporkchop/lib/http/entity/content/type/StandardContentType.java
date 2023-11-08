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

package net.daporkchop.lib.http.entity.content.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    TEXT_PLAIN_UTF8("text/plain", StandardCharsets.UTF_8),
    TEXT_PLAIN_ASCII("text/plain", StandardCharsets.US_ASCII),
    TEXT_HTML("text/html"),
    TEXT_HTML_UTF8("text/html", StandardCharsets.UTF_8),
    TEXT_HTML_ASCII("text/html", StandardCharsets.US_ASCII),
    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_UTF8("application/json", StandardCharsets.UTF_8),
    APPLICATION_JSON_ASCII("application/json", StandardCharsets.US_ASCII),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    protected static final Map<String, Map<String, StandardContentType>> TYPES_LOOKUP = Arrays.stream(StandardContentType.values())
            .collect(Collector.of(
                    HashMap::new,
                    (map, type) -> map.computeIfAbsent(type.mimeType, s -> new HashMap<>()).put(type.charset == null ? "" : type.charset.name(), type),
                    (a, b) -> {
                        a.putAll(b);
                        return a;
                    }
            ));

    protected final String  mimeType;
    protected final Charset charset;
    protected final String  formatted;

    StandardContentType(@NonNull String mimeType) {
        this(mimeType, null);
    }

    StandardContentType(@NonNull String mimeType, Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.formatted = this.charset == null ? this.mimeType : String.format("%s; charset=%s", this.mimeType, this.charset.name());
    }

    @Override
    public String charsetName() {
        return this.charset == null ? null : this.charset.name();
    }
}
