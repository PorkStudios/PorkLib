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

package net.daporkchop.lib.http.request;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.response.aggregate.ToByteArrayAggregator;
import net.daporkchop.lib.http.response.aggregate.ToByteBufAggregator;
import net.daporkchop.lib.http.response.aggregate.ToStringAggregator;

import java.nio.charset.StandardCharsets;

/**
 * Used for configuring an HTTP request prior to sending.
 * <p>
 * Assume that all implementations will cause undefined behavior when used concurrently.
 *
 * @author DaPorkchop_
 */
public interface RequestBuilder<V> {
    /**
     * Configures this {@link RequestBuilder} to send the request to the given URL.
     *
     * @param url the URL that the request should be sent to
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> url(@NonNull String url);

    /**
     * Configures this {@link RequestBuilder} to use the given {@link ResponseAggregator}.
     *
     * @param aggregator the new {@link ResponseAggregator} to use
     * @param <V_NEW>    the new return value type
     * @return this {@link RequestBuilder} instance
     */
    <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull ResponseAggregator<?, V_NEW> aggregator);

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@link String}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<String> aggregateToString() {
        return this.aggregator(new ToStringAggregator(StandardCharsets.UTF_8));
    }

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@code byte[]}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<byte[]> aggregateToByteArray() {
        return this.aggregator(new ToByteArrayAggregator());
    }

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@link ByteBuf}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<ByteBuf> aggregateToByteBuf() {
        return this.aggregator(new ToByteBufAggregator());
    }

    /**
     * Configures this {@link RequestBuilder} to follow redirects silently.
     *
     * @param silentlyFollowRedirects whether or not this request will follow redirects silently
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> silentlyFollowRedirects(boolean silentlyFollowRedirects);

    /**
     * Initiates the HTTP request using the configured settings.
     * <p>
     * Once this method has been called, this {@link RequestBuilder} instance should be discarded and any attempts
     * to use it will result in undefined behavior.
     *
     * @return an {@link Request} constructed using the configured settings
     */
    Request<V> send();
}
