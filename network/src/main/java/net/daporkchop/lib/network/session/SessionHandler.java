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
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.IOException;

/**
 * Handles events that are fired on a {@link PSession}.
 *
 * @author DaPorkchop_
 */
public abstract class SessionHandler<S extends AbstractUserSession<S>> {
    /**
     * Fired when a session is opened (connection is complete).
     *
     * @param session  the session
     * @param incoming whether or not the session was incoming (remote endpoint initiated connection to local
     *                 endpoint) or outgoing (local endpoint initiated connection to remote endpoint)
     */
    public void onOpened(@NonNull S session, boolean incoming) {
        session.onOpened(incoming);
    }

    /**
     * Fired when a session is closed (connection is onClosed).
     *
     * @param session the session
     */
    public void onClosed(@NonNull S session) {
        session.onClosed();
    }

    /**
     * Fired when an exception is caught at any point when processing something related to a specific session.
     *
     * @param session the session
     * @param t       the exception
     */
    public void onException(@NonNull S session, @NonNull Throwable t) {
        session.onException(t);
    }

    /**
     * Fired when a message is received.
     *
     * @param session the session
     * @param msg     the message that was received
     * @param channel the channel that the message was received on
     */
    public void onReceived(@NonNull S session, @NonNull Object msg, int channel) {
        try (DataIn in = session.transportEngine().attemptRead(msg)) {
            if (in == null) {
                this.handleMessage(session, msg, channel);
            } else {
                this.handleBinary(session, in, channel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void handleMessage(@NonNull S session, @NonNull Object msg, int channel) {
        session.onReceived(msg, channel);
    }

    protected void handleBinary(@NonNull S session, @NonNull DataIn in, int channel) throws IOException {
        session.onBinary(in, channel);
    }
}
