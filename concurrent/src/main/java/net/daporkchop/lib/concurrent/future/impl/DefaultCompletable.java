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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.concurrent.future.Completable;
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class DefaultCompletable<I extends Completable<I>> implements Completable<I> {
    protected static final long LISTENERS_OFFSET = PUnsafe.pork_getOffset(DefaultCompletable.class, "listeners");
    protected static final long ERROR_OFFSET = PUnsafe.pork_getOffset(DefaultCompletable.class, "error");

    @NonNull
    protected final Worker worker;
    protected final Object mutex = new Object[0]; //TODO: pool these
    protected volatile Object listeners;
    protected volatile Exception error = null;

    @Override
    public Exception getError() {
        return this.error;
    }

    @Override
    public Worker getWorker() {
        return worker;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I sync() {
        while (!this.isComplete()) {
            try {
                synchronized (this.mutex) {
                    this.mutex.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (I) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I syncInterruptably() throws InterruptedException {
        while (!this.isComplete()) {
            synchronized (this.mutex) {
                this.mutex.wait();
            }
        }
        return (I) this;
    }

    @Override
    public void completeError(@NonNull Exception error) throws AlreadyCompleteException {
        synchronized (this.mutex) {
            if (this.isSuccess() || this.isCancelled() || !PUnsafe.compareAndSwapObject(this, ERROR_OFFSET, null, error)) {
                throw new AlreadyCompleteException();
            }
            this.mutex.notifyAll();
        }
        this.fireListeners();
    }

    @Override
    @SuppressWarnings("unchecked")
    public I addListener(@NonNull Consumer<I> callback) {
        Object prev, next;
        do {
            if ((prev = this.listeners) == null) {
                if (this.isComplete()) {
                    this.worker.submit((I) this, callback);
                    return (I) this;
                } else {
                    //first listener
                    next = callback;
                }
            } else if (prev instanceof List) {
                ((List) prev).add(callback);
                next = prev;
            } else {
                next = new LinkedList<>(); //TODO: pool these
                ((List) next).add(callback);
            }
        } while (!PUnsafe.compareAndSwapObject(this, LISTENERS_OFFSET, prev, next));
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    protected void fireListeners() {
        Object obj = PUnsafe.pork_swapIfNonNull(this, LISTENERS_OFFSET, null);
        if (obj != null) {
            if (obj instanceof List) {
                for (Consumer<I> listener : (List<Consumer<I>>) obj) {
                    this.worker.submit((I) this, listener);
                }
            } else if (obj instanceof Consumer) {
                this.worker.submit((I) this, (Consumer<I>) obj);
            }
        }
    }
}
