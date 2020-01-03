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

package net.daporkchop.lib.common.misc.threadfactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.BitSet;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that applies a name to all created threads, formatted with an ID which "collapses": if a thread exits, the thread's ID will
 * be freed and become usable by other threads that are created later.
 * <p>
 * Basically just an aesthetic thing, but shouldn't cause any issues since it's only minor overhead and only occurs once on thread creation.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class CollapsingIncrementingNamedThreadFactory extends AbstractThreadFactory {
    @Getter
    protected final String format;
    protected final BitSet usedIds = new BitSet();

    public CollapsingIncrementingNamedThreadFactory(@NonNull String format, ClassLoader contextClassLoader, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, int priority, boolean daemon) {
        super(contextClassLoader, uncaughtExceptionHandler, priority, daemon);

        this.format = format;
    }

    @Override
    protected Thread createThread(Runnable r, String name) {
        return new ThreadWrapper(r);
    }

    @Override
    protected String getName(Runnable r) {
        return null; //not actually used
    }

    private final class ThreadWrapper extends Thread {
        protected int id;

        public ThreadWrapper(Runnable target) {
            super(target);
        }

        @Override
        public void run() {
            synchronized (CollapsingIncrementingNamedThreadFactory.this.usedIds)    {
                this.id = CollapsingIncrementingNamedThreadFactory.this.usedIds.nextClearBit(0);
                CollapsingIncrementingNamedThreadFactory.this.usedIds.set(this.id);
            }

            try {
                this.setName(String.format(CollapsingIncrementingNamedThreadFactory.this.format, this.id));

                super.run();
            } finally {
                CollapsingIncrementingNamedThreadFactory.this.usedIds.clear(this.id);
            }
        }
    }
}
