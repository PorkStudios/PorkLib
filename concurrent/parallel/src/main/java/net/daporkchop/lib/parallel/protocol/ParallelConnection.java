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

package net.daporkchop.lib.parallel.protocol;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.parallel.Parallelified;
import net.daporkchop.lib.parallel.future.ParallelFuture;
import net.daporkchop.lib.parallel.future.ParallelVoidFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class ParallelConnection extends UserConnection {
    @NonNull
    @Getter
    private final ParallelProtocol protocol;
    private final AtomicLong idCounter = new AtomicLong(0L);
    private final Map<Long, ParallelFuture> waitingTasks = new ConcurrentHashMap<>(); //TODO: finish primitive
    @Getter
    private Channel channel;
    @Getter
    private boolean server;

    @Override
    public void onConnect() {
        this.server = this.getEndpoint() instanceof Server;
        this.channel = this.openChannel(Reliability.RELIABLE_ORDERED);
    }

    public long allocateNewTask() {
        return this.idCounter.incrementAndGet();
    }

    public ParallelVoidFuture allocateVoid() {
        return this.allocateVoid(this.allocateNewTask());
    }

    public ParallelVoidFuture allocateVoid(long taskId) {
        ParallelVoidFuture future = new ParallelVoidFuture(taskId, this);
        this.waitingTasks.put(taskId, future);
        return future;
    }

    @SuppressWarnings("unchecked")
    public boolean complete(long id, Object obj) {
        ParallelFuture future = this.waitingTasks.get(id);
        if (future == null) {
            return false;
        } else {
            future.complete(obj);
            return true;
        }
    }
}
