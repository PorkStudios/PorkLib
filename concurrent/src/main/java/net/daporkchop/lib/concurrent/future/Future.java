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
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
public interface Future<V> extends Completable<Future<V>> {
    /**
     * Gets the value that this {@link Future} was completed with, blocking until completion if not yet complete.
     * <p>
     * Throws exceptions unsafely if it is completed with an error.
     *
     * @return the return value
     */
    default V get() {
        try {
            return this.getExceptionally();
        } catch (Exception e) {
            PUnsafe.throwException(e);
            return null;
        }
    }

    /**
     * Gets the value that this {@link Future} was completed with, blocking until completion if not yet complete.
     *
     * @return the return value
     * @throws Exception if this {@link Future} is completed with an error
     */
    V getExceptionally() throws Exception;

    /**
     * Gets the value that this {@link Future} was completed with, returning {@code null} if not yet complete.
     * <p>
     * Throws exceptions unsafely if it is completed with an error.
     *
     * @return the return value
     */
    default V getNow() {
        try {
            return this.getNowExceptionally();
        } catch (Exception e) {
            PUnsafe.throwException(e);
            return null;
        }
    }

    /**
     * Gets the value that this {@link Future} was completed with, returning {@code null} if not yet complete.
     *
     * @return the return value
     * @throws Exception if this {@link Future} is completed with an error
     */
    V getNowExceptionally() throws Exception;

    /**
     * Marks this {@link Promise} as being successfully completed.
     * <p>
     * This will cause all attached listeners to be executed, and wake up any waiting threads.
     *
     * @param value the value
     * @throws AlreadyCompleteException if this {@link Promise} is already complete
     */
    void completeSuccessfully(@NonNull V value) throws AlreadyCompleteException;

    /**
     * Attempts to mark this {@link Promise} as being successfully completed, doing nothing if it is already
     * complete.
     *
     * @return whether this could be completed successfully
     * @see #completeSuccessfully(Object)
     */
    default boolean tryCompleteSuccessfully(@NonNull V value) {
        try {
            this.completeSuccessfully(value);
            return true;
        } catch (AlreadyCompleteException e) {
            return false;
        }
    }
}
