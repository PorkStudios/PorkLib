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

package net.daporkchop.lib.http.request.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.HttpMethod;

import java.io.IOException;
import java.util.Map;

/**
 * A simple implementation of {@link Query}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
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
    protected       String              encoded;

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
        if (encoded == null)    {
            this.encode(dst);
        } else {
            dst.append(encoded);
        }
    }

    protected void encode(@NonNull Appendable dst) throws IOException   {
        URLEncoding.encode(dst, this.path, true);
        int count = 0;
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            URLEncoding.encode(dst.append(count++ == 0 ? '?' : '&'), entry.getKey());
            String value = entry.getValue();
            if (!value.isEmpty()) {
                URLEncoding.encode(dst.append('='), value);
            }
        }
        if (this.fragment != null)  {
            URLEncoding.encode(dst.append('#'), this.fragment, true);
        }
    }
}
