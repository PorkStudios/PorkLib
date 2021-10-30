/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.reference;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.threadfactory.PThreadFactories;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PReferenceHandler {
    /*
     * Implementation note:
     *
     * This currently uses only one thread. At some point this may be rewritten (without breaking API compatibility) to use multiple handler threads.
     *
     * The reference handler thread is currently unable to shut down automatically. This will be changed in the future.
     */

    private final ThreadFactory THREAD_FACTORY = PThreadFactories.builder()
            .daemon().formatId().collapsingId()
            .name("PorkLib Reference Handler Thread #%d")
            .build();

    private final Handler CURRENT_HANDLER = new Handler();

    static {
        //start reference handler thread
        THREAD_FACTORY.newThread(CURRENT_HANDLER).start();
    }

    /**
     * Equivalent to calling {@link #createReference(Object, BiFunction, int)} with a {@code cost} of {@code 1}.
     */
    public <S, R extends Reference<S> & HandleableReference> R createReference(@NonNull S referent, @NonNull BiFunction<S, ReferenceQueue<? super S>, R> factory) {
        return createReference(referent, factory, 1);
    }

    /**
     * Creates a new {@link Reference} which is also a {@link HandleableReference}.
     * <p>
     * The reference will be {@link HandleableReference#handle()}ed as defined by {@link HandleableReference}.
     *
     * @param referent the referent
     * @param factory  a {@link BiFunction} which will create the reference
     * @param cost     the estimated cost of invoking {@link HandleableReference#handle()} on the reference. This may be an arbitrary
     *                 positive {@code int}, where higher values indicate a more expensive operation. It may be used to help load-balancing
     *                 across multiple handler threads.
     * @param <S>      the referent type
     * @param <R>      the reference type
     * @return the created reference
     */
    public <S, R extends Reference<S> & HandleableReference> R createReference(@NonNull S referent, @NonNull BiFunction<S, ReferenceQueue<? super S>, R> factory, int cost) {
        return factory.apply(referent, uncheckedCast(CURRENT_HANDLER.queue));
    }

    /**
     * @author DaPorkchop_
     */
    private class Handler implements Runnable {
        private final ReferenceQueue<?> queue = new ReferenceQueue<>();

        /**
         * @deprecated internal API, do not touch!
         */
        @Override
        @Deprecated
        public void run() {
            while (this.doWork()) {
                //loop
            }
        }

        private boolean doWork() {
            try {
                Reference<?> reference = this.queue.remove();

                if (reference != null) { //a reference could be polled from the queue before the timeout was reached, handle it
                    this.handleReference(reference);
                }
            } catch (InterruptedException e) {
                this.handleThrowable(new Error(Thread.currentThread() + " was interrupted!", e));
            }

            return true;
        }

        private void handleReference(@NonNull Reference<?> reference) {
            try {
                ((HandleableReference) reference).handle();
            } catch (Throwable x) {
                this.handleThrowable(new Error("Reference handler terminated exceptionally", x));
            }
        }

        private void handleThrowable(@NonNull Throwable t) {
            Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, t);
        }
    }
}
