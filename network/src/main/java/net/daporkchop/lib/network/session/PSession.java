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

package net.daporkchop.lib.network.session;

import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.util.CloseableFuture;

/**
 * A session represents a single connection between two endpoints.
 *
 * @author DaPorkchop_
 */
public interface PSession<Impl extends PSession<Impl>> extends CloseableFuture, Reliable<PSession<Impl>> {
    /**
     * Gets the local endpoint associated with this session.
     *
     * @return the local endpoint associated with this session
     */
    PEndpoint endpoint();

    /**
     * Gets an open channel on this session with a given id.
     *
     * @param id the id of the channel to get
     * @return a channel with the given id, or {@code null} if none was found
     */
    PChannel<Impl> channel(int id);

    /**
     * Opens a new channel on this session.
     *
     * @param reliability the fallback reliability of the new channel. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return the newly opened channel
     */
    PChannel<Impl> openChannel(Reliability reliability);

    /**
     * Opens a new channel on this session, using this session's fallback reliability level.
     *
     * @return the newly opened channel
     */
    default PChannel<Impl> openChannel() {
        return this.openChannel(this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * While it is not specified which channel will be used for this, it is guaranteed that all packets sent using
     * these methods will be sent on the same channel.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return this session
     */
    PSession<Impl> send(@NonNull Object packet, Reliability reliability);

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * While it is not specified which channel will be used for this, it is guaranteed that all packets sent using
     * these methods will be sent on the same channel.
     *
     * @param packet the packet to be sent
     * @return this session
     */
    default PSession<Impl> send(@NonNull Object packet) {
        return this.send(packet, this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channelId   the id of the channel that the packet will be sent on
     * @return this session
     */
    PSession<Impl> send(@NonNull Object packet, Reliability reliability, int channelId);

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     *
     * @param packet    the packet to be sent
     * @param channelId the id of the channel that the packet will be sent on
     * @return this session
     */
    default PSession<Impl> send(@NonNull Object packet, int channelId) {
        return this.send(packet, this.fallbackReliability(), channelId);
    }

    /**
     * Closes this session, blocking until it is closed.
     */
    @Override
    void close();
}
