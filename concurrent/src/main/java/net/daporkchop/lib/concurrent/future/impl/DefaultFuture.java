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

package net.daporkchop.lib.concurrent.future.impl;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.unsafe.PUnsafe;
import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 * @author DaPorkchop_
 */
public class DefaultFuture<V> extends DefaultCompletable<Future<V>> implements Future<V> {
    protected static final long VALUE_OFFSET = PUnsafe.pork_getOffset(DefaultFuture.class, "value");
    protected static final long CANCELLED_OFFSET = PUnsafe.pork_getOffset(DefaultFuture.class, "cancelled");

    protected volatile V value = null;
    protected volatile int cancelled = 0;

    public DefaultFuture(Worker worker) {
        super(worker);
    }

    @Override
    public boolean isSuccess() {
        return this.value != null;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled != 0;
    }

    @Override
    public V getExceptionally() throws Exception {
        this.sync();
        if (this.isError()) {
            throw this.error;
        }
        return this.value;
    }

    @Override
    public V getNowExceptionally() throws Exception {
        if (this.isComplete())  {
            if (this.isError()) {
                throw this.error;
            }
            return this.value;
        } else {
            return null;
        }
    }

    @Override
    public void completeSuccessfully(@NonNull V value) throws AlreadyCompleteException {
        synchronized (this.mutex)   {
            if (this.isError() || this.isCancelled() || !PUnsafe.compareAndSwapObject(this, VALUE_OFFSET, null, value))   {
                throw new AlreadyCompleteException();
            }
            this.mutex.notifyAll();
        }
        this.fireListeners();
    }

    @Override
    public void cancel() throws AlreadyCompleteException {
        synchronized (this.mutex)   {
            if (this.isError() || this.isSuccess() || !PUnsafe.compareAndSwapInt(this, CANCELLED_OFFSET, 0, 1))   {
                throw new AlreadyCompleteException();
            }
            this.mutex.notifyAll();
        }
        this.fireListeners();
    }
}
