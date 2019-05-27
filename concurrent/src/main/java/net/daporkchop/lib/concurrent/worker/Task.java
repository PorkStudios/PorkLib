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

package net.daporkchop.lib.concurrent.worker;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.concurrent.future.Completable;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public abstract class Task<C extends Completable<C>, F> implements Runnable {
    @Override
    public abstract void run();

    public abstract void cancel();

    @RequiredArgsConstructor
    @Getter
    private static abstract class AbstractTask<C extends Completable<C>, F> extends Task {
        @NonNull
        protected final C completable;
        @NonNull
        protected final F func;

        @Override
        public final void run() {
            try {
                this.doRun();
            } catch (Exception e)   {
                this.completable.completeError(e);
            }
        }

        @Override
        public void cancel() {
            this.completable.cancel();
        }

        protected abstract void doRun() throws Exception;
    }

    public static class Run extends AbstractTask<Promise, Runnable> {
        public Run(Promise completable, Runnable func) {
            super(completable, func);
        }

        @Override
        protected void doRun() throws Exception {
            this.func.run();
            this.completable.completeSuccessfully();
        }
    }

    public static class RunParam<P> extends AbstractTask<Promise, Consumer<P>>  {
        protected final P param;

        public RunParam(Promise completable, Consumer<P> func, P param) {
            super(completable, func);

            this.param = param;
        }

        @Override
        protected void doRun() throws Exception {
            this.func.accept(this.param);
            this.completable.completeSuccessfully();
        }
    }

    public static class Compute<R> extends AbstractTask<Future<R>, Supplier<R>> {
        public Compute(Future<R> completable, Supplier<R> func) {
            super(completable, func);
        }

        @Override
        protected void doRun() throws Exception {
            this.completable.completeSuccessfully(this.func.get());
        }
    }

    public static class ComputeParam<P, R> extends AbstractTask<Future<R>, Function<P, R>>  {
        protected final P param;

        public ComputeParam(Future<R> completable, Function<P, R> func, P param) {
            super(completable, func);

            this.param = param;
        }

        @Override
        protected void doRun() throws Exception {
            this.completable.completeSuccessfully(this.func.apply(this.param));
        }
    }
}
