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
