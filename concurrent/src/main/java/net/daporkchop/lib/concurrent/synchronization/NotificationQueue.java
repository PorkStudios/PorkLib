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

package net.daporkchop.lib.concurrent.synchronization;

import lombok.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * This is somewhat hard to explain. Think of this as a way of using {@link Object#wait()} and {@link Object#notify()} without having to place
 * calls to said methods inside of a queue.
 * <p>
 * I think this is also called a semaphore or something like that? idk
 * never taken a computer science class or whatever lol
 *
 * @author DaPorkchop_
 */
public interface NotificationQueue {
    /**
     * Waits until this thread is signalled or the given number of nanoseconds elapse, whichever happens first.
     *
     * @param maxWaitNanos the maximum number of nanoseconds to wait. If negative, then this will wait forever until signalled.
     */
    void awaitNanos(long maxWaitNanos);

    /**
     * Waits until this thread is signalled or the given number of milliseconds elapse, whichever happens first.
     *
     * @param maxWaitMillis the maximum number of milliseconds to wait.
     */
    default void await(long maxWaitMillis) {
        this.awaitNanos(TimeUnit.MILLISECONDS.toNanos(maxWaitMillis));
    }

    /**
     * Waits until this thread is signalled or the given amount of time elapses, whichever happens first.
     *
     * @param unit    the unit that the time is given in
     * @param maxWait the maximum amount of time units to wait for
     */
    default void await(@NonNull TimeUnit unit, long maxWait) {
        this.awaitNanos(unit.toNanos(maxWait));
    }

    /**
     * Waits until this thread is signalled.
     */
    default void await() {
        this.awaitNanos(-1L);
    }

    /**
     * Signals any thread that is currently waiting. If none are waiting, this method does nothing.
     */
    void signal();

    /**
     * Signals every thread that is currently waiting. If none are waiting, this method does nothing.
     */
    void signalAll();
}
