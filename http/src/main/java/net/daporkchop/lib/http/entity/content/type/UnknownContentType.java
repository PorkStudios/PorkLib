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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Represents a {@link ContentType} which is not known to PorkLib http.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Accessors(fluent = true)
public final class UnknownContentType implements ContentType {
    @NonNull
    protected final String mimeType;
    protected final String charsetName;

    public UnknownContentType(@NonNull String mimeType) {
        this(mimeType, null);
    }

    @Override
    public Charset charset() {
        if (this.charsetName != null) {
            try {
                return Charset.forName(this.charsetName);
            } catch (UnsupportedCharsetException e) {
                //swallow
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.formatted();
    }
}
