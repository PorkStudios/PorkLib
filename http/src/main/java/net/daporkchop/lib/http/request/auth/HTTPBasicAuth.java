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

package net.daporkchop.lib.http.request.auth;

import lombok.NonNull;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Implementation of HTTP basic authentication.
 *
 * @author DaPorkchop_
 */
public final class HTTPBasicAuth implements Authentication {
    private final String encoded;

    public HTTPBasicAuth(@NonNull String username, @NonNull String password) {
        this.encoded = Base64.getUrlEncoder().encodeToString(String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void rewrite(@NonNull MutableHeaderMap headers) {
        if (headers.put("authorization", this.encoded) != null) {
            throw new IllegalStateException("Authorization header has already been set!");
        }
    }
}
