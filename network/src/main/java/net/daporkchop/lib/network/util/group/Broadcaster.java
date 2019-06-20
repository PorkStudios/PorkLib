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

package net.daporkchop.lib.network.util.group;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.SendFlags;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.util.reliability.Reliable;

/**
 * A type that can send messages to multiple sessions at once.
 *
 * @author DaPorkchop_
 */
public interface Broadcaster<Impl extends Broadcaster<Impl>> extends Reliable<Impl> {
    // normal send

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, 0);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message) {
        return this.broadcast(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + channel

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, 0);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, int channel) {
        return this.broadcast(message, channel, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + reliability

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, 0);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, @NonNull Reliability reliability) {
        return this.broadcast(message, 0, reliability, Priority.NORMAL, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + priority

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, 0);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, @NonNull Priority priority) {
        return this.broadcast(message, 0, this.fallbackReliability(), priority, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + channel + reliability

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, 0);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using the given reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, int channel, @NonNull Reliability reliability) {
        return this.broadcast(message, channel, reliability, Priority.NORMAL, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + channel + priority

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, 0);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on the given channel, using this session's fallback reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, int channel, @NonNull Priority priority) {
        return this.broadcast(message, channel, this.fallbackReliability(), priority, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    // send + reliability + priority

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and no additional
     * flags.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcast(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, 0);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlush(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastNow(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastAsync(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushNow(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using the given reliability level, the given priority, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #broadcast(Object, int, Reliability, Priority, int)
     */
    default Promise broadcastFlushAsync(@NonNull Object message, @NonNull Reliability reliability, @NonNull Priority priority) {
        return this.broadcast(message, 0, reliability, priority, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint.
     *
     * @param message     the message to be sent
     * @param channel     the id of the channel to send the message on
     * @param reliability the reliability that the message is to be sent with. Some transport engines may ignore this
     * @param priority    the priority that the message is to be sent with. Some transport engines may ignore this
     * @param flags       additional flags that the message is to be sent with. Valid are any fields from {@link net.daporkchop.lib.network.util.SendFlags}, and
     *                    flags may also be ORed together
     * @return a {@link Promise} that will be notified once the message has been sent. Depending on the flags that are set (or if none are set), this may return {@code null}
     */
    Promise broadcast(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags);

    /**
     * Flushes this channel's send buffer, if present.
     * <p>
     * This method will not block, however if this transport engine uses some form of send buffer it will immediately begin to broadcast all queued data
     * to the remote endpoint.
     */
    void flushBuffer();
}
