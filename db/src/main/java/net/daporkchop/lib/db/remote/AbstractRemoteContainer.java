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

package net.daporkchop.lib.db.remote;

import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.container.AbstractContainer;
import net.daporkchop.lib.db.remote.protocol.SaveContainerPacket;
import net.daporkchop.lib.primitive.map.LongObjectMap;
import net.daporkchop.lib.primitive.map.PorkMaps;
import net.daporkchop.lib.primitive.map.hashmap.LongObjectHashMap;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractRemoteContainer<V, B extends Container.Builder<V, ? extends AbstractRemoteContainer<V, B>, RemoteDB>> extends AbstractContainer<V, B, RemoteDB> implements RemoteContainer<V, B, RemoteDB> {
    public final AtomicLong taskIdCounter = new AtomicLong(0L);
    public LongObjectMap<CompletableFuture> executionWaiters = PorkMaps.synchronize(new LongObjectHashMap<>());

    public AbstractRemoteContainer(B builder) throws IOException {
        super(builder);
    }

    @Override
    public void save() throws IOException {
        long id = this.taskIdCounter.getAndIncrement();
        this.db.getNetClient().send(new SaveContainerPacket(this.name, id, false));
        this.waitForTask(id);
    }

    protected void waitForTask(long id) {
        CompletableFuture future = new CompletableFuture();
        this.executionWaiters.put(id, future);
        try {
            future.get();
        } catch (ExecutionException
                | InterruptedException e)   {
            throw this.exception(e);
        }
    }

    public static abstract class Builder<V, C extends AbstractRemoteContainer<V, ? extends Builder<V, C>>> extends Container.Builder<V, C, RemoteDB> {
        protected Builder(RemoteDB db, String name) {
            super(db, name);
        }

        @Override
        public C buildIfPresent() throws IOException {
            //TODO: check if is present on remote server
            return null;
        }
    }
}
