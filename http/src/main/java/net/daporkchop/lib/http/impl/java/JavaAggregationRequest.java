/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.impl.java;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Actual implementation of {@link net.daporkchop.lib.http.request.Request} for {@link JavaHttpClient} which implements
 * the the usage of a {@link net.daporkchop.lib.http.response.aggregate.ResponseAggregator}.
 *
 * @author DaPorkchop_
 */
public final class JavaAggregationRequest<V> extends JavaRequest<V, JavaAggregationRequest<V>> {
    public JavaAggregationRequest(@NonNull JavaHttpClient client, @NonNull JavaRequestBuilder<V, JavaAggregationRequest<V>> builder) throws IOException {
        super(client, builder);
    }

    @Override
    protected void implRecvBody(@NonNull InputStream bodyIn) throws IOException {
        //TODO: implement this
    }
}
