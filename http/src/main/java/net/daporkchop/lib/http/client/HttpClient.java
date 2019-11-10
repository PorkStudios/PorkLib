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

package net.daporkchop.lib.http.client;

import lombok.NonNull;
import net.daporkchop.lib.http.client.builder.AsyncRequestBuilder;
import net.daporkchop.lib.http.client.builder.BlockingRequestBuilder;
import net.daporkchop.lib.http.util.Constants;
import net.daporkchop.lib.http.util.HttpEndpoint;

/**
 * A container around an instance of an HTTP client.
 * <p>
 * Re-using an HTTP client instance has many advantages, mainly since it allows resources (threads, selectors) to
 * be shared between requests, and also enables protocol extensions that benefit from persistent connections (HTTP/1.1
 * connection reuse or HTTP/2.0).
 *
 * @author DaPorkchop_
 */
//TODO: make this useful
public interface HttpClient extends HttpEndpoint {
    /**
     * Creates a new {@link AsyncRequestBuilder} for issuing asynchronous HTTP requests from this {@link HttpClient}.
     *
     * @return a new {@link AsyncRequestBuilder}
     */
    AsyncRequestBuilder prepareAsync();

    /**
     * Creates a new {@link BlockingRequestBuilder} for issuing blocking HTTP requests from this {@link HttpClient}.
     *
     * @return a new {@link BlockingRequestBuilder}
     */
    BlockingRequestBuilder prepareBlocking();

    /**
     * Creates a new {@link BlockingRequestBuilder} for issuing blocking HTTP requests from this {@link HttpClient}.
     *
     * @param url the URL to initialize the builder with
     * @return a new {@link BlockingRequestBuilder}
     */
    default BlockingRequestBuilder prepareBlocking(@NonNull String url) {
        return this.prepareBlocking().configure(url);
    }
}
