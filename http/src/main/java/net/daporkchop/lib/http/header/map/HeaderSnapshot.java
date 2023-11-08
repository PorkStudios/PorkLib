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

package net.daporkchop.lib.http.header.map;

import lombok.NonNull;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.SingletonHeaderImpl;
import net.daporkchop.lib.http.util.exception.HttpException;
import net.daporkchop.lib.http.util.exception.MalformedResponseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple implementation of an immutable {@link HeaderMap}.
 *
 * @author DaPorkchop_
 */
public final class HeaderSnapshot implements HeaderMap {
    protected final Header[]            value;
    protected final Map<String, Header> map;

    public HeaderSnapshot(@NonNull HeaderMap source) {
        this(source, true);
    }

    public HeaderSnapshot(@NonNull HeaderMap source, boolean map) {
        int size = source.size();
        this.value = new Header[size];
        for (int i = 0; i < size; i++) {
            //don't create new instance if it's already immutable
            this.value[i] = Header.immutable(source.get(i));
        }
        this.map = map
                ? Arrays.stream(this.value).collect(Collectors.toMap(header -> header.key().toLowerCase(), PFunctions.identity()))
                : null;
    }

    public HeaderSnapshot(@NonNull Stream<Header> source) throws HttpException {
        this(source, true);
    }

    public HeaderSnapshot(@NonNull Stream<Header> source, boolean map) throws HttpException {
        this.value = source.filter(Objects::nonNull).toArray(Header[]::new);

        //regardless of whether or not we're storing the map, use the map to assert all header keys are distinct
        Map<String, Header> tempMap = new HashMap<>(this.value.length);
        for (Header header : this.value) {
            String key = header.key().toLowerCase();
            if (tempMap.putIfAbsent(key, header) != null) {
                throw new MalformedResponseException(String.format("Duplicate header key: \"%s\" (to add: \"%s\", in map: \"%s\")", key, header.key(), tempMap.get(key).key()));
            }
        }
        this.map = map ? tempMap : null;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public Header get(int index) throws IndexOutOfBoundsException {
        return this.value[index];
    }

    @Override
    public Header get(@NonNull String key) {
        if (this.map == null) {
            for (Header header : this.value) {
                if (key.equalsIgnoreCase(header.key())) {
                    return header;
                }
            }
            return null;
        } else {
            return this.map.get(key.toLowerCase());
        }
    }
}
