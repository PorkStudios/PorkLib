/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ThreadFactory;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PReferenceQueues {
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
     * Equivalent to calling {@link #getHandlingReferenceQueue(int)} with a {@code cost} of {@code 1}.
     */
    public static <T> ReferenceQueue<T> getHandlingReferenceQueue() {
        return getHandlingReferenceQueue(1);
    }

    /**
     * Gets a {@link ReferenceQueue} which automatically invokes {@link HandleableReference#handle()} on any {@link HandleableReference}s added to it.
     *
     * @param cost the estimated cost of invoking {@link HandleableReference#handle()} on the reference. This may be an arbitrary
     *             positive {@code int}, where higher values indicate a more expensive operation. It may be used to help load-balancing
     *             across multiple handler threads.
     * @return a {@link ReferenceQueue}
     */
    public static <T> ReferenceQueue<T> getHandlingReferenceQueue(int cost) {
        return PorkUtil.uncheckedCast(CURRENT_HANDLER.queue);
    }

    /**
     * @author DaPorkchop_
     */
    private final class Handler implements Runnable {
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
