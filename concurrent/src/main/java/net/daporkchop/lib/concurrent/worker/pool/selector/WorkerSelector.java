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

package net.daporkchop.lib.concurrent.worker.pool.selector;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.concurrent.worker.WorkerPool;

/**
 * Chooses a worker from a pool.
 *
 * @author DaPorkchop_
 * @see net.daporkchop.lib.concurrent.worker.pool.selector.RandomSelector
 * @see net.daporkchop.lib.concurrent.worker.pool.selector.RoundRobinSelector
 */
@FunctionalInterface
public interface WorkerSelector {
    /**
     * Chooses a worker from a pool.
     *
     * @param pool    the {@link WorkerPool} to which the workers belong
     * @param workers all the workers in the pool
     * @return the worker to use. Must be from the pool parameter, otherwise results are undefined
     */
    Worker select(@NonNull WorkerPool pool, @NonNull Worker[] workers);
}
