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

package net.daporkchop.lib.network.util;

import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * Additional modifiers that may be applied to messages being sent (via {@link net.daporkchop.lib.network.session.PSession#send(Object, int, Reliability, Priority, int)})
 * and related methods).
 *
 * @author DaPorkchop_
 */
public interface SendFlags {
    /**
     * This flag indicates that the send method will block until the message has been written successfully to the network, an exception occurs during
     * encoding, or the session is closed. If this flag is set, the {@link io.netty.util.concurrent.Future} return value will probably be {@code null}
     * (although some transport engines may set it anyway).
     * <p>
     * If both {@link #SYNC} and {@link #ASYNC} are set, {@link #ASYNC} behavior will be used.
     * <p>
     * If neither {@link #SYNC} nor {@link #ASYNC} are set, the channel's fallback behavior will be used, as defined by the transport engine.
     */
    int SYNC = 1 << 0;

    /**
     * This flag indicates that the send method will not be blocking, and will return immediately as soon as the message is queued for sending. If this flag
     * is set, the {@link io.netty.util.concurrent.Future} return value is guaranteed not to be {@code null}.
     * <p>
     * If both {@link #SYNC} and {@link #ASYNC} are set, {@link #ASYNC} behavior will be used.
     * <p>
     * If neither {@link #SYNC} nor {@link #ASYNC} are set, the channel's fallback behavior will be used, as defined by the transport engine.
     */
    int ASYNC = 1 << 1;

    /**
     * This flag indicates that the send call will additionally cause the send buffer to be flushed, forcing all queued data to be written immediately.
     * <p>
     * This flag may be ignored, depending on the transport engine used.
     */
    int FLUSH = 1 << 2;
}
