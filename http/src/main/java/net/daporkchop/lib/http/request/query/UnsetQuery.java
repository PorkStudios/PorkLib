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

package net.daporkchop.lib.http.request.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.http.HttpMethod;

import java.io.IOException;
import java.util.Map;

/**
 * An implementation of {@link Query} that simply throws an {@link UnsupportedOperationException} from every method.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnsetQuery implements Query {
    public static final UnsetQuery INSTANCE = new UnsetQuery();

    @Override
    public HttpMethod method() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String path() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String fragment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String param(@NonNull String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String param(@NonNull String name, String fallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> params() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence encoded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence encoded(boolean computeIfAbsent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendEncoded(@NonNull Appendable dst) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
