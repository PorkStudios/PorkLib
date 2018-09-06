/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.endpoint;

import lombok.Data;
import lombok.NonNull;
import net.daporkchop.lib.network.protocol.Packet;
import net.daporkchop.lib.network.protocol.encapsulated.packet.DisconnectPacket;
import org.apache.mina.core.session.IoSession;

/**
 * Common methods for all sessions
 *
 * @author DaPorkchop_
 */
@Data
public abstract class AbstractSession {
    /**
     * The underlying instance of {@link IoSession} that this session is using to communicate on
     */
    @NonNull
    private final IoSession session;

    /**
     * Whether or not this session is on a server
     */
    private final boolean server;

    /**
     * Send a packet to this session.
     * <p>
     * IMPORTANT! This method should not be invoked until the handshake has been completed!
     *
     * @param packet the packet to send
     */
    public void send(@NonNull Packet packet) {
        if (this.session.isConnected()) {
            this.session.write(packet);
        }
    }

    /**
     * Closes this session without a reason message
     */
    public void close() {
        this.close("", false);
    }

    /**
     * Closes this session with a reason
     *
     * @param reason the reason for disconnection
     */
    public void close(@NonNull String reason, boolean blocking) {
        if (this.session.isConnected()) {
            DisconnectPacket packet = new DisconnectPacket(reason);
            this.session.write(packet);
            if (blocking) {
                this.session.closeOnFlush().awaitUninterruptibly();
            } else {
                this.session.closeOnFlush();
            }
        }
    }

    /**
     * A session implementation that does nothing
     */
    public static class NoopSession extends AbstractSession {
        public NoopSession(IoSession session, boolean server) {
            super(session, server);
        }
    }
}
