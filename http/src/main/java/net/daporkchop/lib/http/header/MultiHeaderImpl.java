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
