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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface BaseFuture<R> {
    /**
     * Adds a listener.
     *
     * @param callback a function to run when this future is completed
     */
    void addListener(@NonNull BiConsumer<R, Throwable> callback);

    /**
     * Adds a listener.
     *
     * @param callback a function to run when this future is completed
     */
    default void addListener(@NonNull Consumer<R> callback) {
        this.addListener((r, t) -> callback.accept(r));
    }

    /**
     * Adds a listener.
     *
     * @param callback a function to run when this future is completed
     */
    default void addListener(@NonNull Runnable callback) {
        this.addListener((r, t) -> callback.run());
    }

    /**
     * @return whether or not the task has been completed
     */
    boolean isComplete();

    /**
     * @param t the exception that will be thrown
     */
    void completeExceptionally(@NonNull Throwable t);

    /**
     * @param t the exception that will be thrown
     */
    void tryCompleteExceptionally(@NonNull Throwable t);
}
