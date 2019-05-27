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
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;
import net.daporkchop.lib.concurrent.util.exception.NotCompleteException;
import net.daporkchop.lib.concurrent.worker.Worker;

import java.util.function.Consumer;

/**
 * Shared methods implemented by both {@link Promise} and {@link Future}.
 *
 * @author DaPorkchop_
 */
public interface Completable<I extends Completable<I>> {
    /**
     * @return whether or not this {@link Promise} or {@link Future} completed successfully
     */
    boolean isSuccess();

    /**
     * @return whether or not this {@link Promise} or {@link Future} was cancelled
     */
    boolean isCancelled();

    /**
     * @return whether or not this {@link Promise} or {@link Future} completed with an error
     */
    default boolean isError() {
        return this.getError() != null;
    }

    /**
     * @return whether or not this {@link Promise} or {@link Future} is complete
     */
    default boolean isComplete() {
        return this.isSuccess() || this.isError() || this.isCancelled();
    }

    /**
     * Ensures that this {@link Promise} or {@link Future} is complete.
     *
     * @throws NotCompleteException if this {@link Promise} or {@link Future} is not complete
     */
    default void assertComplete() throws NotCompleteException {
        if (!this.isComplete()) {
            throw new NotCompleteException();
        }
    }

    /**
     * @return the exception that caused this {@link Promise} or {@link Future} to be completed with an error, or {@code null} if this wasn't completed with an error
     */
    Exception getError();

    /**
     * @return the {@link Worker} that owns this {@link Promise} or {@link Future}, or {@code null} if this is not owned by a worker
     */
    Worker getWorker();

    //
    //
    // sync methods
    //
    //

    /**
     * Waits for this {@link Promise} or {@link Future} to be completed, blocking the invoking thread.
     * <p>
     * This method will ignore thread interrupts.
     *
     * @return this {@link Promise} or {@link Future}
     */
    I sync();

    /**
     * Waits for this {@link Promise} or {@link Future} to be completed, blocking the invoking thread.
     *
     * @return this {@link Promise} or {@link Future}
     * @throws InterruptedException if the thread is interrupted
     */
    I syncInterruptably() throws InterruptedException;

    //
    //
    // Completion methods
    //
    //

    /**
     * Marks this {@link Promise} or {@link Future} as being successfully completed.
     * <p>
     * This will cause all attached listeners to be executed, and wake up any waiting threads.
     *
     * @param error the error
     */
    void completeError(@NonNull Exception error) throws AlreadyCompleteException;

    /**
     * Attempts to mark this {@link Promise} or {@link Future} as being successfully completed, doing nothing if it is already
     * complete.
     *
     * @return whether this could be completed successfully
     * @see #completeError(Exception)
     */
    default boolean tryCompleteError(@NonNull Exception error) {
        try {
            this.completeError(error);
            return true;
        } catch (AlreadyCompleteException e) {
            return false;
        }
    }

    /**
     * Marks this {@link Promise} or {@link Future} as being cancelled.
     * <p>
     * This will cause all attached listeners to be executed, and wake up any waiting threads.
     */
    void cancel() throws AlreadyCompleteException;

    /**
     * Attempts to mark this {@link Promise} or {@link Future} as being being cancelled, doing nothing if it is already complete.
     * <p>
     * This will cause all attached listeners to be executed, and wake up any waiting threads.
     *
     * @return whether this could be completed successfully
     * @see #cancel()
     */
    default boolean tryCancel() {
        try {
            this.cancel();
            return true;
        } catch (AlreadyCompleteException e) {
            return false;
        }
    }

    //
    //
    // Listener methods
    //
    //

    /**
     * Adds a listener that will be run when {@link Promise} or {@link Future} is completed.
     * @param callback the function to run
     * @return this {@link Promise} or {@link Future}
     */
    I addListener(@NonNull Consumer<I> callback);

    /**
     * Adds a listener that will be run when this {@link Promise} or {@link Future} is completed.
     *
     * @param callback the function to run
     */
    default I addListener(@NonNull Runnable callback)   {
        return this.addListener(i -> callback.run());
    }

    /**
     * Adds a listener that will be run when this {@link Promise} or {@link Future} is completed successfully.
     *
     * @param callback the function to run
     */
    default I addSuccessListener(@NonNull Runnable callback)    {
        return this.addListener(i -> {
            if (i.isSuccess())  {
                callback.run();
            }
        });
    }

    /**
     * Adds a listener that will be run when this {@link Promise} or {@link Future} is completed with an error.
     *
     * @param callback the function to run
     */
    default I addErrorListener(@NonNull Runnable callback)  {
        return this.addListener(i -> {
            if (i.isError())    {
                callback.run();
            }
        });
    }

    /**
     * Adds a listener that will be run when this {@link Promise} or {@link Future} is completed with an error.
     *
     * @param callback the function to run
     */
    default I addErrorListener(@NonNull Consumer<Exception> callback)   {
        return this.addListener(i -> {
            if (i.isError())    {
                callback.accept(i.getError());
            }
        });
    }

    /**
     * Adds a listener that will be run when this {@link Promise} or {@link Future} is cancelled.
     *
     * @param callback the function to run
     */
    default I addCancelListener(@NonNull Runnable callback)    {
        return this.addListener(i -> {
            if (i.isCancelled())  {
                callback.run();
            }
        });
    }
}
