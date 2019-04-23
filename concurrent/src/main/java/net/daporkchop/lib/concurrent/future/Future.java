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

/**
 * @author DaPorkchop_
 */
public interface Future {
    /**
     * Blocks the invoking thread until this {@link Future} is completed.
     * <p>
     * If this {@link Future} was completed with an exception, the exception will be thrown (unsafely).
     */
    default void sync() {
        try {
            this.syncInterruptably();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Blocks the invoking thread until this {@link Future} is completed.
     * <p>
     * If this {@link Future} was completed with an exception, the exception will be thrown (unsafely).
     *
     * @throws InterruptedException if the thread is interrupted
     */
    void syncInterruptably() throws InterruptedException;

    /**
     * Checks whether or not this {@link Future} has been completed.
     * <p>
     * This method will not block the invoking thread.
     *
     * @return whether or not this {@link Future} has been complete
     */
    boolean isCompleted();

    /**
     * Completes this {@link Future}.
     * <p>
     * Any waiting threads will be notified, and this instance will be marked as completed.
     * <p>
     * If this {@link Future} is already completed, the results are undefined.
     */
    void complete();

    /**
     * Completes this {@link Future} with an exception.
     * <p>
     * Any waiting threads will have this exception thrown, and this instance will be marked as completed.
     * <p>
     * If this {@link Future} is already completed, the results are undefined.
     *
     * @param e the exception
     */
    void completeExceptionally(@NonNull Exception e);
}
