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

import lombok.NonNull;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A value for the HTTP "Content-Type" header.
 *
 * @author DaPorkchop_
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type">Content-Type at Mozilla</a>
 */
public interface ContentType {
    Pattern _PATTERN_SIMPLE_CONTENT_TYPE = Pattern.compile("^([a-z0-9-]+\\/[a-z0-9-]+)(; ?charset=([a-zA-Z0-9-]+))?$");

    /**
     * Parses a {@link ContentType} from the formatted value (as would be found in an HTTP header).
     *
     * @param contentType the formatted value to parse
     * @return a {@link ContentType} from the formatted value
     * @throws IllegalArgumentException if the given value could not be parsed as a valid {@link ContentType}
     */
    static ContentType parse(@NonNull CharSequence contentType) throws IllegalArgumentException {
        Matcher matcher = _PATTERN_SIMPLE_CONTENT_TYPE.matcher(contentType);
        if (matcher.matches()) {
            return of(matcher.group(1), matcher.group(3)); //group 3 will be null if not found
        } else {
            throw new IllegalArgumentException(String.format("Invalid content type: \"%s\"!", contentType));
        }
    }

    /**
     * Gets a {@link ContentType} which matches the given MIME type.
     *
     * @param mimeType the MIME type
     * @return a {@link ContentType} which matches the given MIME type
     */
    static ContentType of(@NonNull String mimeType) {
        return of(mimeType, null);
    }

    /**
     * Gets a {@link ContentType} which matches the given MIME type and charset.
     *
     * @param mimeType the MIME type
     * @param charset  the charset
     * @return a {@link ContentType} which matches the given MIME type and charset
     */
    static ContentType of(@NonNull String mimeType, String charset) {
        {
            Map<String, StandardContentType> map1 = StandardContentType.TYPES_LOOKUP.get(mimeType);
            if (map1 != null) {
                StandardContentType type = map1.get(charset == null ? "" : charset);
                if (type != null) {
                    return type;
                }
            }
        }
        return new UnknownContentType(mimeType, charset);
    }

    /**
     * Gets the content's MIME type.
     * <p>
     * This value will never be {@code null}.
     *
     * @return the content's MIME type
     */
    String mimeType();

    /**
     * Gets the content's charset.
     * <p>
     * This value may be {@code null} (as it's an optional component of the "Content-Type" header).
     *
     * @return the content's charset
     */
    String charsetName();

    /**
     * Gets the content's charset.
     * <p>
     * This value may be {@code null} (as it's an optional component of the "Content-Type" header), or if the charset denoted by {@link #charsetName()}
     * is not known, or for some other reason.
     *
     * @return the content's charset
     */
    Charset charset();

    /**
     * Gets the the formatted value of this {@link ContentType}.
     * <p>
     * This will return a value that may be used as the body of a "Content-Type" HTTP header.
     *
     * @return the the formatted value of this {@link ContentType}
     */
    default String formatted() {
        return this.charsetName() == null ? this.mimeType() : String.format("%s; charset=%s", this.mimeType(), this.charsetName());
    }
}
