/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.concurrent.future;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFutureTask;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PScheduledFuture;

import java.util.Queue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.*;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * Default base implementation of {@link PFuture}.
 *
 * @author DaPorkchop_
 */
public class DefaultPScheduledFuture<V> extends DefaultPFuture<V> implements PScheduledFuture<V>, Runnable {
    protected static final Class<?> SCHEDULED_FUTURE_TASK = PorkUtil.classForName("io.netty.util.concurrent.ScheduledFutureTask");

    protected static final AtomicLong nextTaskId = pork_getStaticField(SCHEDULED_FUTURE_TASK, "nextTaskId").getObject();
    protected static final long       START_TIME = pork_getStaticField(SCHEDULED_FUTURE_TASK, "START_TIME").getLong();

    protected static final long ID_OFFSET            = pork_getOffset(SCHEDULED_FUTURE_TASK, "id");
    protected static final long DEADLINENANOS_OFFSET = pork_getOffset(SCHEDULED_FUTURE_TASK, "deadlineNanos");

    public static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    public static long deadlineNanos(long delay) {
        long deadlineNanos = nanoTime() + delay;
        // Guard against overflow
        return deadlineNanos < 0 ? Long.MAX_VALUE : deadlineNanos;
    }

    protected final long id = nextTaskId.getAndIncrement();
    protected long deadlineNanos;
    // 0 - no repeat, >0 - repeat at fixed rate, <0 - repeat with fixed delay
    private final   long periodNanos;

    public DefaultPScheduledFuture(@NonNull EventExecutor executor, long delayNanos, long periodNanos) {
        super(executor);

        this.deadlineNanos = deadlineNanos(delayNanos);
        this.periodNanos = periodNanos;
    }

    public DefaultPScheduledFuture(@NonNull EventExecutor executor, long delayNanos) {
        super(executor);

        this.deadlineNanos = deadlineNanos(delayNanos);
        this.periodNanos = 0L;
    }

    public long delayNanos() {
        return max(this.deadlineNanos - nanoTime(), 0L);
    }

    public long delayNanos(long currentTimeNanos) {
        return max(this.deadlineNanos - (currentTimeNanos - START_TIME), 0L);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }

        long thatDeadlineNanos;
        long thatId;
        if (o instanceof DefaultPScheduledFuture) {
            DefaultPScheduledFuture other = (DefaultPScheduledFuture) o;
            thatId = other.id;
            thatDeadlineNanos = other.deadlineNanos;
        } else if (o.getClass() == SCHEDULED_FUTURE_TASK) {
            thatId = getLong(o, ID_OFFSET);
            thatDeadlineNanos = getLong(o, DEADLINENANOS_OFFSET);
        } else {
            throw new IllegalArgumentException(PorkUtil.className(o));
        }

        long d = this.deadlineNanos - thatDeadlineNanos;
        if (d < 0L) {
            return -1;
        } else if (d > 0L) {
            return 1;
        } else if (this.id < thatId) {
            return -1;
        } else if (this.id == thatId) {
            throw new Error();
        } else {
            return 1;
        }
    }

    @Override
    public void run() {
        try {
            if (this.periodNanos == 0) {
                if (this.setUncancellable()) {
                    this.run0();
                }
            } else {
                // check if is done as it may was cancelled
                if (!this.isCancelled()) {
                    this.runPeriod0();
                    if (!this.executor().isShutdown()) {
                        long p = this.periodNanos;
                        if (p > 0) {
                            this.deadlineNanos += p;
                        } else {
                            this.deadlineNanos = nanoTime() - p;
                        }
                        if (!this.isCancelled()) {
                            // scheduledTaskQueue can never be null as we lazy init it before submit the task!
                            Queue<ScheduledFutureTask<?>> scheduledTaskQueue =
                                    ((AbstractScheduledEventExecutor) executor()).scheduledTaskQueue;
                            scheduledTaskQueue.add(this);
                        }
                    }
                }
            }
        } catch (Throwable cause) {
            this.setFailure(cause);
        }
    }

    protected void run0()    {
        throw new UnsupportedOperationException();
    }

    protected void runPeriod0()    {
        throw new UnsupportedOperationException();
    }
}
