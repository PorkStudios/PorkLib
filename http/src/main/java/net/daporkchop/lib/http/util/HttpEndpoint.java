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

package net.daporkchop.lib.http.util;

import io.netty.util.concurrent.Future;
import net.daporkchop.lib.http.HttpEngine;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.server.HttpServer;

/**
 * Shared methods between {@link HttpClient} and {@link HttpServer}.
 *
 * @author DaPorkchop_
 * @see HttpClient
 * @see HttpServer
 */
public interface HttpEndpoint {
    /**
     * @return the {@link HttpEngine} that created this {@link HttpEndpoint}
     */
    HttpEngine engine();

    /**
     * Closes this {@link HttpEndpoint} instance.
     * <p>
     * This will release any resources allocated specifically by this endpoint (however, any resources allocated
     * by the parent {@link HttpEngine} will not be released unless specifically stated otherwise by the
     * implementation).
     */
    Future<Void> close();
}
