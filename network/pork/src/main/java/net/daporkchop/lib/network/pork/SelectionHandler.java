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

package net.daporkchop.lib.network.pork;

import lombok.NonNull;

import java.nio.channels.SelectionKey;

/**
 * A handler fired when a key is selectable.
 * <p>
 * The attachment on the given {@link SelectionKey} will always be this handler instance.
 *
 * @author DaPorkchop_
 */
public interface SelectionHandler {
    /**
     * Handles an event.
     *
     * @param flags the flags (from {@link SelectionKey}) of the selection operation
     * @throws Exception if an exception occurs
     */
    void handle(int flags) throws Exception;

    /**
     * Handles an exception if one is caught while invoking {@link #handle(int)}.
     *
     * @param e the exception that was caught
     */
    default void handleException(@NonNull Exception e) {
        e.printStackTrace();
    }
}
