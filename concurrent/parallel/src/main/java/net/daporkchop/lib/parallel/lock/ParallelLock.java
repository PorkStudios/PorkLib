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

package net.daporkchop.lib.parallel.lock;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.concurrent.future.VoidFuture;
import net.daporkchop.lib.concurrent.lock.AnyThreadLock;
import net.daporkchop.lib.concurrent.lock.SimplifiedLock;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.parallel.Parallelified;
import net.daporkchop.lib.parallel.future.ParallelFuture;
import net.daporkchop.lib.parallel.future.ParallelVoidFuture;
import net.daporkchop.lib.parallel.protocol.ParallelConnection;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author DaPorkchop_
 */
public interface ParallelLock extends Parallelified {
    void lock(@NonNull ParallelLockRequest request);

    void unlock(@NonNull ParallelLockRequest request);

    @RequiredArgsConstructor
    @Getter
    class Server implements ParallelLock {
        private final int id;
        private volatile boolean locked = false;
        private final Queue<ParallelLockRequest> waitingTasks = new ConcurrentLinkedQueue<>();

        @Override
        public boolean isServer() {
            return true;
        }

        @Override
        public boolean isClient() {
            return false;
        }
    }

    class ParallelLockRequest extends ParallelVoidFuture implements Data, Packet {
        public int id;

        protected ParallelLockRequest() {
        }

        public ParallelLockRequest(int id, long taskId, ParallelConnection connection) {
            super(taskId, connection);
            this.id = id;
        }

        public ParallelLockRequest(@NonNull ParallelVoidFuture future, int id)    {
            this(id, future.getTaskId(), future.getConnection());
        }

        @Override
        public void read(@NonNull DataIn in) throws IOException {
            this.taskId = in.readLong();
        }

        @Override
        public void write(@NonNull DataOut out) throws IOException {
            out.writeLong(this.taskId);
        }

        public static class ParallelLockCodec implements Codec<ParallelLockRequest, ParallelConnection> {
            @Override
            public void handle(@NonNull ParallelLockRequest packet, @NonNull Channel channel, @NonNull ParallelConnection connection) {
                ParallelLock lock = connection.getProtocol().get(packet.id);
            }

            @Override
            public ParallelLockRequest createInstance() {
                return new ParallelLockRequest();
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    class Client implements SimplifiedLock, ParallelLock, Parallelified.Client  {
        private final int id;
        @NonNull
        private final ParallelConnection connection;

        @Override
        public void lock() {
            synchronized (this) {
                ParallelVoidFuture future = this.connection.allocateVoid();
                connection.getChannel().send(new ParallelLockRequest(future, this.id));
                future.getUnchecked();
            }
        }

        @Override
        public void unlock() {

        }

        @Override
        public void lock(@NonNull ParallelLockRequest request) {

        }

        @Override
        public void unlock(@NonNull ParallelLockRequest request) {

        }
    }
}
