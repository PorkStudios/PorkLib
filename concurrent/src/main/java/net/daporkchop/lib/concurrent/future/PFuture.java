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

package net.daporkchop.lib.concurrent.future;

import lombok.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public interface PFuture<R> extends BaseFuture<R> {
    /**
     * @return the value, or {@code null} if not yet complete
     */
    R get();

    /**
     * @return the value, or the given default value if not yet complete
     */
    default R getOrDefault(R def)   {
        return this.isComplete() ? this.get() : def;
    }

    /**
     * @return the value, or a value computed by the given supplier if not yet complete
     */
    default R getOrCompute(@NonNull Supplier<R> supplier)   {
        return this.isComplete() ? this.get() : supplier.get();
    }

    /**
     * Await completion of the task.
     * <p>
     * This method will block until the task has been completed.
     */
    R sync();

    /**
     * @see #sync()
     */
    R syncInterruptably() throws InterruptedException;

    /**
     * @param time the maximum number of milliseconds to wait for
     * @see #sync()
     */
    R sync(long time);

    /**
     * @param unit the time unit
     * @param time the maximum number of units to wait for
     * @see #sync()
     */
    default R sync(@NonNull TimeUnit unit, long time) {
        return this.sync(unit.toMillis(time));
    }

    /**
     * Completes the task, waking up all waiting threads.
     */
    void complete(@NonNull R value);
}
