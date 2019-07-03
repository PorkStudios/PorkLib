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

package net.daporkchop.lib.network.netty.util.future;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.util.DefaultListenable;
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class NettyPromiseWrapper implements Promise {
    @NonNull
    protected final io.netty.util.concurrent.Promise<Void> delegate;

    @Override
    public void completeSuccessfully() throws AlreadyCompleteException {
        if (!this.delegate.trySuccess(null))    {
            throw new AlreadyCompleteException();
        }
    }

    @Override
    public boolean isSuccess() {
        return this.delegate.isSuccess();
    }

    @Override
    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }

    @Override
    public Exception getError() {
        return (Exception) this.delegate.cause();
    }

    @Override
    public Promise sync() throws InterruptedException {
        this.delegate.sync();
        return this;
    }

    @Override
    public Promise syncUninterruptibly() {
        this.delegate.syncUninterruptibly();
        return this;
    }

    @Override
    public void completeError(@NonNull Exception error) throws AlreadyCompleteException {
        if (!this.delegate.tryFailure(error))   {
            throw new AlreadyCompleteException();
        }
    }

    @Override
    public void cancel() throws AlreadyCompleteException {
        this.delegate.cancel(true);
    }

    @Override
    public Promise addListener(@NonNull Consumer<Promise> callback) {
        this.delegate.addListener(f -> callback.accept(this));
        return this;
    }
}
