/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.impl.netty.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.EmptyHttpEntity;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.ArrayHeaderMap;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;
import net.daporkchop.lib.http.server.ResponseBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ResponseBuilder} for {@link NettyHttpServer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public final class NettyResponseBuilder implements ResponseBuilder {
    protected static final String[] RESERVED_HEADERS = {
            "connection",
            "content-encoding",
            "content-type",
            "transfer-encoding"
    };
    protected static final Set<String> RESERVED_HEADER_SET;

    static {
        RESERVED_HEADER_SET = new HashSet<>();

        for (int i = 0, length = RESERVED_HEADERS.length; i < length; i++)  {
            RESERVED_HEADER_SET.add(RESERVED_HEADERS[i]);
        }
    }

    protected static String validateKey(@NonNull String key) {
        if (RESERVED_HEADER_SET.contains(key.toLowerCase()))  {
            throw new IllegalArgumentException("Reserved header key: " + key);
        }
        return key;
    }

    @NonNull
    protected StatusCode status;

    @NonNull
    protected HttpEntity body = EmptyHttpEntity.INSTANCE;

    protected final MutableHeaderMap headers = new ArrayHeaderMap();

    @Override
    public ResponseBuilder putHeader(@NonNull String key, @NonNull String value) {
        this.headers.put(validateKey(key), value);
        return this;
    }

    @Override
    public ResponseBuilder putHeader(@NonNull String key, @NonNull List<String> values) {
        this.headers.put(validateKey(key), values);
        return this;
    }

    @Override
    public ResponseBuilder putHeader(@NonNull Header header) {
        validateKey(header.key());
        this.headers.put(header);
        return this;
    }

    @Override
    public ResponseBuilder addHeader(@NonNull String key, @NonNull String value) {
        this.headers.add(validateKey(key), value);
        return this;
    }

    @Override
    public ResponseBuilder addHeader(@NonNull String key, @NonNull List<String> values) {
        this.headers.add(validateKey(key), values);
        return this;
    }

    @Override
    public ResponseBuilder addHeader(@NonNull Header header) {
        validateKey(header.key());
        this.headers.add(header);
        return this;
    }
}
