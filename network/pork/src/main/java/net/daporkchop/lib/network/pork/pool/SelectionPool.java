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

package net.daporkchop.lib.network.pork.pool;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.CloseableFuture;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.pork.SelectionHandler;

import java.nio.channels.SelectableChannel;

/**
 * A pool of workers that work in parallel accepting data from {@link java.nio.channels.Selector}s.
 *
 * @author DaPorkchop_
 */
public interface SelectionPool extends CloseableFuture {
    /**
     * Chooses a worker from this pool and registers the given channel and handler to that worker-
     *
     * @param channel     the channel
     * @param interestOps the operations (from {@link java.nio.channels.SelectionKey}) that should be listened for
     * @param handler     the handler that will handle events on the channel
     */
    void register(@NonNull SelectableChannel channel, int interestOps, @NonNull SelectionHandler handler);

    @Override
    Promise closeAsync();

    @Override
    Promise closePromise();
}
