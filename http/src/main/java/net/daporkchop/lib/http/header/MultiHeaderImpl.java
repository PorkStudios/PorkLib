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

package net.daporkchop.lib.http.header;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Header} which has multiple values.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class MultiHeaderImpl implements Header {
    protected final String       key;
    protected final List<String> values;
    protected       int          hash;

    public MultiHeaderImpl(@NonNull String key, @NonNull List<String> values) {
        this(key, values, false);
    }

    public MultiHeaderImpl(@NonNull String key, @NonNull List<String> values, boolean skipCopy) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values list is empty!");
        } else {
            this.key = key;
            this.values = skipCopy ? values : new ArrayList<>(values);
        }
    }

    public MultiHeaderImpl(@NonNull Header source) {
        this(source.key(), source.values());
    }

    @Override
    public String value() {
        return this.singleton() ? this.values.get(0) : this.values.stream().collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.key, this.value());
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            hash = this.key.toLowerCase().hashCode();
            for (String value : this.values) {
                hash = hash * 31 + value.hashCode();
            }
            this.hash = hash;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Header) {
            Header header = (Header) obj;
            return this.key.equalsIgnoreCase(header.key()) && this.values.equals(header.values());
        } else {
            return false;
        }
    }
}
