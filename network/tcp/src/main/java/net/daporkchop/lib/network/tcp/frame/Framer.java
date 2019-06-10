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

package net.daporkchop.lib.network.tcp.frame;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.util.List;

/**
 * Packs binary messages into "frames",
 *
 * @author DaPorkchop_
 */
public interface Framer<S extends AbstractUserSession<S>> {
    /**
     * Called when data is received over the TCP channel. The data is not guaranteed to be a complete frame.
     * <p>
     * Note that any data not read from the buffer will be discarded.
     *
     * @param session  the session that the data was received on
     * @param msg      the data that was received. This buffer should not be released (unless explicitly retained elsewhere)
     * @param callback a callback function that each complete frame should be passed to individually for processing
     */
    void received(@NonNull S session, @NonNull ByteBuf msg, @NonNull UnpackCallback callback);

    /**
     * Called when data is about to be sent over the TCP channel. This method may be used to pack outbound messages into frames (such as prefixing them
     * with a length header).
     *
     * @param session  the session that the data will be sent on
     * @param msg      the data that will be sent. This buffer should not be released, and should probably also be added to the outbound frames list unless
     *                 there is a specific reason why not to send it
     * @param metadata additional metadata to be sent alongside the packet or otherwise used for it. If any metadata fields are set that cannot be used
     *                 by this framer, an {@link IllegalArgumentException} should be thrown
     * @param frames   a list of {@link ByteBuf}s that will be sent (sequentially) over the TCP channel
     */
    void sending(@NonNull S session, @NonNull ByteBuf msg, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames);

    /**
     * Called when the framer instance is initialized for a connecting session.
     *
     * @param session the session that is connecting
     */
    void init(@NonNull S session);

    /**
     * Called when the framer instance is released from a connected session.
     *
     * @param session the session that is disconnecting
     */
    void release(@NonNull S session);

    @FunctionalInterface
    interface UnpackCallback {
        void add(@NonNull ByteBuf buf, int channelId, int protocolId);

        default void add(@NonNull ByteBuf buf, int protocolId) {
            this.add(buf, 0, protocolId);
        }

        default void add(@NonNull ByteBuf buf) {
            this.add(buf, 0, 0);
        }
    }
}
