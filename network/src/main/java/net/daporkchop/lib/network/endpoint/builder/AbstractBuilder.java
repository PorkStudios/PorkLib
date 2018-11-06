/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.endpoint.builder;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.Transport;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.PacketProtocol;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractBuilder<C extends UserConnection, E extends Endpoint<C>> {
    private static final AtomicInteger DEFAULT_EXECUTOR_THREAD_COUNTER = new AtomicInteger(0);
    //private static final Executor DEFAULT_EXECUTOR = Executors.newCachedThreadPool(runnable -> new Thread(runnable, String.format("PorkLib network executor #%d", DEFAULT_EXECUTOR_THREAD_COUNTER.getAndIncrement())));
    private static final Executor DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            0, TimeUnit.SECONDS, //2, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            runnable -> new Thread(runnable, String.format("PorkLib network executor #%d", DEFAULT_EXECUTOR_THREAD_COUNTER.getAndIncrement()))
    );

    @NonNull
    private PacketProtocol<C> protocol;

    @NonNull
    private InetSocketAddress address;

    @NonNull
    private Transport transport = Transport.TCP;

    @NonNull
    private Executor executor = DEFAULT_EXECUTOR;

    public E build()    {
        if (this.protocol == null)  {
            throw new IllegalStateException("protocol must be set!");
        } else if (this.address == null)    {
            throw new IllegalStateException("address must be set!");
        }

        return this.doBuild();
    }

    abstract E doBuild();
}
