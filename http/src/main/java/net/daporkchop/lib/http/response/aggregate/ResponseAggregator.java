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

package net.daporkchop.lib.http.response.aggregate;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.ResponseHeaders;

/**
 * Aggregates data received from an HTTP response into a final object.
 *
 * @param <A> a type that will store the data temporarily (used internally for aggregation)
 * @param <V> the result type
 * @author DaPorkchop_
 */
public interface ResponseAggregator<A, V> {
    /**
     * Prepares a new temporary value for aggregating results into.
     *
     * @param request  the request that is being processed
     * @param response the server's initial response to the outgoing request
     * @return a new temporary value
     * @throws Exception if an exception occurs while creating a new temporary value
     */
    A init(@NonNull ResponseHeaders response, @NonNull Request<V> request) throws Exception;

    /**
     * Fired every time new data is received from the remote server.
     * <p>
     * The data should (although it is not required) be somehow processed into the temporary value.
     * <p>
     * Any data not read from the given {@link ByteBuf} instance will be silently discarded.
     *
     * @param temp    the temporary value
     * @param data    a {@link ByteBuf} containing the received data. Even if retained manually, this value is not safe to keep past the termination of this
     *                method. If the data must be stored for later use, write it to some intermediate location or make a clone of the buffer.
     * @param request the request that the data was received on
     * @return the temporary value. May be different from the one passed to this method, in which case it will be updated for all subsequent aggregator function calls
     * @throws Exception if an exception occurs while accepting the data
     */
    A add(@NonNull A temp, @NonNull ByteBuf data, @NonNull Request<V> request) throws Exception;

    /**
     * Called after the request has been successfully completed and no more data is available to be read.
     * <p>
     * This converts the data from the internal storage type to the final type.
     * <p>
     * It is allowed to return the temporary value, if no conversion is necessary.
     *
     * @param temp    the temporary value
     * @param request the request that is being finalized
     * @return the final value
     * @throws Exception if an exception occurs while finalizing the value
     */
    V doFinal(@NonNull A temp, @NonNull Request<V> request) throws Exception;

    /**
     * De-initializes a temporary value after the request is complete.
     * <p>
     * Unlike {@link #doFinal(Object, Request)}, this method will also be called in the event of a failure, and is
     * intended to be used to release any resources allocated by the temporary value that are no longer needed.
     * <p>
     * If the request is successful, this method will always be called after {@link #doFinal(Object, Request)}.
     *
     * @param temp    the temporary value
     * @param request the request that has been completed
     * @throws Exception if an exception occurs while de-initializing the temporary value
     */
    default void deinit(@NonNull A temp, @NonNull Request<V> request) throws Exception {
    }
}
