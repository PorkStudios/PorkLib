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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.util.URLEncoding;

import java.io.IOException;
import java.util.Map;

/**
 * A simple implementation of {@link Query}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"hash"})
@Getter
@Accessors(fluent = true)
public final class QueryImpl implements Query {
    @NonNull
    protected final HttpMethod          method;
    @NonNull
    protected final String              path;
    protected final String              fragment;
    @NonNull
    protected final Map<String, String> params;

    @Accessors(fluent = false)
    protected String encoded;

    @Getter(AccessLevel.NONE)
    protected transient int hash;

    @Override
    public CharSequence encoded() {
        return this.encoded;
    }

    private String getEncoded() {
        return this.encoded(true).toString();
    }

    @Override
    public CharSequence encoded(boolean computeIfAbsent) {
        String encoded = this.encoded;
        if (encoded == null && computeIfAbsent) {
            StringBuilder builder = new StringBuilder();
            try {
                this.encode(builder);
            } catch (IOException e) {
                //can't happen
            }
            this.encoded = encoded = builder.toString();
        }
        return encoded;
    }

    @Override
    public void appendEncoded(@NonNull Appendable dst) throws IOException {
        String encoded = this.encoded;
        if (encoded == null) {
            this.encode(dst);
        } else {
            dst.append(encoded);
        }
    }

    protected void encode(@NonNull Appendable dst) throws IOException {
        URLEncoding.encode(dst, this.path, true);
        int count = 0;
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            URLEncoding.encode(dst.append(count++ == 0 ? '?' : '&'), entry.getKey());
            String value = entry.getValue();
            if (!value.isEmpty()) {
                URLEncoding.encode(dst.append('='), value);
            }
        }
        if (this.fragment != null) {
            URLEncoding.encode(dst.append('#'), this.fragment, true);
        }
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            hash = this.method.hashCode();
            hash = hash * 31 + this.path.hashCode();
            hash = hash * 31 + this.path.hashCode();
            hash = hash * 31 + (this.fragment == null ? 0 : this.fragment.hashCode());
            for (Map.Entry<String, String> entry : this.params.entrySet())    {
                hash = (hash * 31 + entry.getKey().hashCode()) * 31 + entry.getValue().hashCode();
            }
            if (hash == 0) {
                hash = 1;
            }
            this.hash = hash;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof Query)    {
            if (obj == UnsetQuery.INSTANCE) {
                return false;
            }
            Query other = (Query) obj;
            return this.method == other.method()
                    && this.path.equals(other.path())
                    && (this.fragment == null ? other.fragment() == null : this.fragment.equals(other.fragment()))
                    && this.params.equals(other.params());
        }
        return super.equals(obj);
    }
}
