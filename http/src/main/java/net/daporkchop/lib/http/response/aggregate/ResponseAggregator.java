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

package net.daporkchop.lib.http.response.aggregate;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.Response;

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
    A init(@NonNull Response response, @NonNull Request<V> request) throws Exception;

    /**
     * Fired every time new data is received from the remote server.
     * <p>
     * The data should (although it is not required) be somehow processed into the temporary value.
     * <p>
     * Any data not read from the given {@link ByteBuf} instance will be silently discarded.
     *
     * @param temp    the temporary value
     * @param data    a {@link ByteBuf} containing the received data
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
    default void deinit(@NonNull A temp, @NonNull Request request) throws Exception {
    }
}
