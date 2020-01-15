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

package net.daporkchop.lib.ai;

import lombok.NonNull;

/**
 * A type that can train an AI.
 *
 * @author DaPorkchop_
 */
public interface Evaluator<A extends AI> {
    /**
     * Initializes the trainer in preparation for training.
     * <p>
     * This method is only called once for the beginning of training.
     * <p>
     * This may be used to e.g. load training data from disk.
     *
     * @param totalWorkers the total number of workers that will be used
     * @throws Exception if an exception occurs while initializing
     */
    default void init(int totalWorkers) throws Exception {
    }

    /**
     * Initializes a single worker in preparation for training.
     * <p>
     * This method is only called once per worker, and will always be called from the same thread that the worker
     * will later be training on.
     *
     * @param worker       the ordinal of the current worker
     * @param totalWorkers the total number of workers
     * @throws Exception if an exception occurs while initializing
     */
    default void initWorker(int worker, int totalWorkers) throws Exception {
    }

    /**
     * Evaluates a single specimen.
     * <p>
     * This method should evaluate the fitness of the given specimen by feeding it a large number of test
     * data samples and seeing what the results are, and return a {@code double} value that represents the fitness
     * of the specimen. The network's fitness is not on any sort of scale, higher values simply indicate that the
     * specimen performed better (although the fitness value should be directly proportional to the specimen's
     * performance, for example it could be simply the number of tests that were passed successfully). However, the
     * fitness must be at least {@code 0.0d} and may not be {@link Double#NaN}.
     *
     * @param specimen the specimen to evaluate
     * @return the specimen's skill
     */
    double evaluate(@NonNull A specimen);

    /**
     * De-initializes a worker after training is complete.
     * <p>
     * This method is only called once per worker, and will always be called from the same thread that the worker
     * will later be training on.
     *
     * @param worker       the ordinal of the current worker
     * @param totalWorkers the total number of workers
     * @throws Exception if an exception occurs while de-initializing
     */
    default void deinitWorker(int worker, int totalWorkers) throws Exception {
    }

    /**
     * De-initializes the trainer after training is complete.
     *
     * @param totalWorkers the total number of workers
     * @throws Exception if an exception occurs while initializing
     */
    default void deinit(int totalWorkers) throws Exception {
    }
}
