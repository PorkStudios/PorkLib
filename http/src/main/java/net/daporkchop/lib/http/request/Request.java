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

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.http.response.ResponseBody;
import net.daporkchop.lib.http.response.ResponseHeaders;

/**
 * An HTTP request.
 *
 * @param <V> the type of the return value of the request
 * @author DaPorkchop_
 */
public interface Request<V> {
    /**
     * This future is completed once the remote server has responded with a status code and headers, or this request fails before the headers are received.
     *
     * @return a {@link Future} that will be notified when headers have been received
     */
    Future<ResponseHeaders<V>> headersFuture();

    /**
     * Adds a listener to {@link #headersFuture()}.
     * <p>
     * Simply a convenience method, since {@link Future#addListener(GenericFutureListener)} has some annoying generic parameters that make it a pain
     * to use.
     *
     * @param listener the listener to add
     * @return this {@link Request} instance
     */
    default Request<V> addHeadersListener(@NonNull GenericFutureListener<Future<ResponseHeaders<V>>> listener) {
        this.headersFuture().addListener(listener);
        return this;
    }

    /**
     * Waits for {@link #headersFuture()} to be completed.
     * <p>
     * This will block until the server responds with a status code+headers, or this request fails before the headers are received.
     *
     * @return this {@link Request} instance
     * @see #headersFuture()
     * @see #syncHeadersInterruptably()
     */
    default Request<V> syncHeaders() {
        this.headersFuture().syncUninterruptibly();
        return this;
    }

    /**
     * Waits for {@link #headersFuture()} to be completed.
     * <p>
     * This will block until the server responds with a status code+headers, or this request fails before the headers are received.
     *
     * @return this {@link Request} instance
     * @throws InterruptedException if the current thread is interrupted
     * @see #headersFuture()
     * @see #syncHeaders()
     */
    default Request<V> syncHeadersInterruptably() throws InterruptedException {
        this.headersFuture().sync();
        return this;
    }

    /**
     * Gets the {@link ResponseHeaders} from {@link #headersFuture()}, blocking if needed.
     * <p>
     * This will block until the server responds with a status code+headers, or this request fails before the headers are received.
     *
     * @return the status code+headers that the server responded with
     * @see #headersFuture()
     * @see #syncHeaders()
     * @see #syncHeadersInterruptablyAndGet()
     */
    default ResponseHeaders<V> syncHeadersAndGet() {
        return this.headersFuture().syncUninterruptibly().getNow();
    }

    /**
     * Gets the {@link ResponseHeaders} from {@link #headersFuture()}, blocking if needed.
     * <p>
     * This will block until the server responds with a status code+headers, or this request fails before the headers are received.
     *
     * @return the status code+headers that the server responded with
     * @throws InterruptedException if the current thread is interrupted
     * @see #headersFuture()
     * @see #syncHeadersInterruptably()
     * @see #syncHeadersAndGet()
     */
    default ResponseHeaders<V> syncHeadersInterruptablyAndGet() throws InterruptedException {
        return this.headersFuture().sync().getNow();
    }

    /**
     * This future is completed once this request has been completed with the final, aggregated value, or this request fails before the body is received.
     * <p>
     * Note that it is possible for {@link #headersFuture()} to have succeeded, but this future to have failed (e.g. in case of network failure before
     * the entire body could be read).
     *
     * @return a {@link Future} that will be notified when the request is complete
     */
    Future<ResponseBody<V>> bodyFuture();

    /**
     * Adds a listener to {@link #bodyFuture()}.
     * <p>
     * Simply a convenience method, since {@link Future#addListener(GenericFutureListener)} has some annoying generic parameters that make it a pain
     * to use.
     *
     * @param listener the listener to add
     * @return this {@link Request} instance
     */
    default Request<V> addBodyListener(@NonNull GenericFutureListener<Future<ResponseBody<V>>> listener) {
        this.bodyFuture().addListener(listener);
        return this;
    }

    /**
     * Waits for {@link #bodyFuture()} to be completed.
     * <p>
     * This will block until the server responds with a complete body, or this request fails.
     *
     * @return this {@link Request} instance
     * @see #bodyFuture()
     * @see #syncBodyInterruptably()
     */
    default Request<V> syncBody() {
        this.bodyFuture().syncUninterruptibly();
        return this;
    }

    /**
     * Waits for {@link #bodyFuture()} to be completed.
     * <p>
     * This will block until the server responds with a complete body, or this request fails.
     *
     * @return this {@link Request} instance
     * @throws InterruptedException if the current thread is interrupted
     * @see #bodyFuture()
     * @see #syncBody()
     */
    default Request<V> syncBodyInterruptably() throws InterruptedException {
        this.bodyFuture().sync();
        return this;
    }

    /**
     * Gets the {@link ResponseBody} from {@link #bodyFuture()}, blocking if needed.
     * <p>
     * This will block until the server responds with a complete body, or this request fails.
     *
     * @return the status code+headers that the server responded with
     * @see #bodyFuture()
     * @see #syncBody()
     * @see #syncBodyInterruptablyAndGet()
     */
    default ResponseBody<V> syncBodyAndGet() {
        return this.bodyFuture().syncUninterruptibly().getNow();
    }

    /**
     * Gets the {@link ResponseBody} from {@link #bodyFuture()}, blocking if needed.
     * <p>
     * This will block until the server responds with a complete body, or this request fails.
     *
     * @return the status code+headers that the server responded with
     * @throws InterruptedException if the current thread is interrupted
     * @see #bodyFuture()
     * @see #syncBodyInterruptably()
     * @see #syncBodyAndGet()
     */
    default ResponseBody<V> syncBodyInterruptablyAndGet() throws InterruptedException {
        return this.bodyFuture().sync().getNow();
    }

    /**
     * Attempts to close the HTTP request.
     * <p>
     * If the request has already been completed, this method does nothing.
     *
     * @return the same {@link Future} instance as {@link #bodyFuture()}
     */
    Future<ResponseBody<V>> close();
}
