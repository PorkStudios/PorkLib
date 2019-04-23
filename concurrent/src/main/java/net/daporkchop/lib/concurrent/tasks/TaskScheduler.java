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

package net.daporkchop.lib.concurrent.tasks;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.ReturnableFuture;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A {@link TaskExecutor} that allows scheduling tasks for a future period in time.
 *
 * @author DaPorkchop_
 */
public interface TaskScheduler extends TaskExecutor {
    /**
     * Schedules a task to be executed at a future time.
     * <p>
     * The task is not guaranteed to be run at exactly the time given, for example if multiple tasks request execution times that overlap.
     *
     * @param task the task to be run
     * @param time the time at which the task will begin execution
     * @return an instance of {@link Future} that will be completed when the task has finished execution
     */
    Future schedule(@NonNull Runnable task, long time);

    /**
     * Schedules a task to be executed at a future time.
     * <p>
     * The task is not guaranteed to be run at exactly the time given, for example if multiple tasks request execution times that overlap.
     *
     * @param task the task to be run
     * @param time the time at which the task will begin execution
     * @param <T>  the return type from the task
     * @return an instance of {@link ReturnableFuture} that will be completed with the task's return value when the task has finished execution
     */
    <T> ReturnableFuture<T> schedule(@NonNull Supplier<T> task, long time);

    default Future scheduleIn(@NonNull Runnable task, long delay) {
        return this.schedule(task, System.currentTimeMillis() + delay);
    }

    default <T> ReturnableFuture<T> scheduleIn(@NonNull Supplier<T> task, long delay) {
        return this.schedule(task, System.currentTimeMillis() + delay);
    }

    default Future scheduleIn(@NonNull Runnable task, @NonNull TimeUnit unit, long delay) {
        return this.schedule(task, System.currentTimeMillis() + unit.toMillis(delay));
    }

    default <T> ReturnableFuture<T> scheduleIn(@NonNull Supplier<T> task, @NonNull TimeUnit unit, long delay) {
        return this.schedule(task, System.currentTimeMillis() + unit.toMillis(delay));
    }

    /**
     * Schedules a task that, starting at the given time, will be executed repeatedly after a certain delay.
     * <p>
     * For longer running tasks, it is possible that the task may take longer to execute than the given interval. In such a case, the task will
     * not be started again, but rather the next execution will not commence until the previous one has been completed.
     * <p>
     * The task has a {@code boolean} return value. The task will continue to be executed as long as the task returns {@code true}. If it returns
     * {@code false}, it will no longer be executed any more.
     *
     * @param task      the task that will be run
     * @param startTime the time at which the task will start executing
     * @param interval  the interval between executions of the task
     */
    void scheduleRepeating(@NonNull BooleanSupplier task, long startTime, long interval);

    /**
     * Schedules a task that, starting at the given time, will be executed repeatedly after a certain delay.
     * <p>
     * For longer running tasks, it is possible that the task may take longer to execute than the given interval. In such a case, the task will
     * not be started again, but rather the next execution will not commence until the previous one has been completed.
     *
     * @param task      the task that will be run
     * @param startTime the time at which the task will start executing
     * @param interval  the interval between executions of the task
     */
    default void scheduleRepeating(@NonNull Runnable task, long startTime, long interval)   {
        this.scheduleRepeating(() -> {
            task.run();
            return true;
        }, startTime, interval);
    }

    default void scheduleRepeating(@NonNull BooleanSupplier task, long interval)    {
        this.scheduleRepeating(task, 0L, interval);
    }

    default void scheduleRepeating(@NonNull BooleanSupplier task, @NonNull TimeUnit intervalUnit, long interval)  {
        this.scheduleRepeating(task, 0L, intervalUnit.toMillis(interval));
    }

    default void scheduleRepeating(@NonNull BooleanSupplier task, long delay, @NonNull TimeUnit intervalUnit, long interval)  {
        this.scheduleRepeating(task, System.currentTimeMillis() + delay, intervalUnit.toMillis(interval));
    }

    default void scheduleRepeating(@NonNull BooleanSupplier task, @NonNull TimeUnit delayUnit, long delay, @NonNull TimeUnit intervalUnit, long interval)  {
        this.scheduleRepeating(task, System.currentTimeMillis() + delayUnit.toMillis(delay), intervalUnit.toMillis(interval));
    }

    @Override
    default Future submit(@NonNull Runnable task) {
        return this.schedule(task, 0L);
    }

    @Override
    default <T> ReturnableFuture<T> submit(@NonNull Supplier<T> task) {
        return this.schedule(task, 0L);
    }
}
