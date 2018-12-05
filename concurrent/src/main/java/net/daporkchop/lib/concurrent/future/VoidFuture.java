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

package net.daporkchop.lib.concurrent.future;

import lombok.Getter;

import java.util.concurrent.ExecutionException;

/**
 * Similar to {@link java.util.concurrent.CompletableFuture}, however without a return
 * value.
 *
 * @author DaPorkchop_
 */
@Getter
public class VoidFuture implements PorkFuture {
    private volatile boolean complete = false;
    private volatile Throwable exception = null;

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        synchronized (this) {
            while (!this.complete) {
                this.wait(50L); //don't do a full sleep, for some reason threads don't always seem to be notified here
            }
        }
        if (this.exception != null) {
            throw new ExecutionException(this.exception);
        }
        return null;
    }

    @Override
    public Object get(long millis) throws InterruptedException, ExecutionException {
        synchronized (this) {
            this.wait(millis);
        }
        if (this.complete && this.exception != null) {
            throw new ExecutionException(this.exception);
        }
        return null;
    }

    @Override
    public void complete(Object value) {
        this.complete();
    }

    @Override
    public void complete() {
        if (this.complete) {
            throw new IllegalStateException("already complete!");
        }
        synchronized (this) {
            this.complete = true;
            this.notifyAll();
        }
    }

    @Override
    public void completeExceptionally(Throwable exception) {
        this.exception = exception;
        this.complete();
    }
}
