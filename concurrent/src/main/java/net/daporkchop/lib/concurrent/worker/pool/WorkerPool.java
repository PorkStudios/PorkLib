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

package net.daporkchop.lib.concurrent.worker.pool;

import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.Worker;

/**
 * A pool of {@link Worker}s.
 *
 * @author DaPorkchop_
 */
public interface WorkerPool extends Worker {
    /**
     * Finds and returns a worker from this pool.
     * <p>
     * The exact behavior of this method is highly implementation-dependant.
     *
     * @return a worker from this pool
     */
    Worker next();

    /**
     * Stops this worker pool. This will cause the pool to stop accepting new tasks, execute all tasks currently
     * queued, and terminate once all tasks are complete.
     *
     * @return a promise that can be used to track the shutdown status of this pool
     */
    Promise stop();

    /**
     * Terminates this worker pool. This will cause the pool to execute any currently queued tasks to completion
     * and exit, leaving queued tasks completed with an exception.
     *
     * @return a promise that can be used to track the shutdown status of this pool
     */
    Promise terminate();

    @Override
    default WorkerPool pool() {
        return this;
    }
}
