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

package net.daporkchop.lib.concurrent;

import net.daporkchop.lib.concurrent.future.Promise;

/**
 * A type that can be closed at some point in the future (asynchronously).
 *
 * @author DaPorkchop_
 */
public interface CloseableFuture {
    /**
     * Closes this instance now, blocking until the close operation is complete.
     */
    default void closeNow() {
        this.closeAsync().syncUninterruptibly();
    }

    /**
     * Starts the close operation for this instance if it hasn't been started already.
     *
     * @return the {@link Promise} that will be notified when this instance is closed
     */
    Promise closeAsync();

    /**
     * Gets the {@link Promise} that will be notified when this instance is closed.
     * <p>
     * Invoking this method does not start the close operation.
     *
     * @return the {@link Promise} that will be notified when this instance is closed
     */
    Promise closePromise();
}
