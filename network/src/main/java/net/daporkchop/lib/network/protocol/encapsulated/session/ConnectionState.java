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

package net.daporkchop.lib.network.protocol.encapsulated.session;

import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.AbstractSession;
import org.apache.mina.core.session.IoSession;

/**
 * The connection state that a connection is currently in
 *
 * @author DaPorkchop_
 */
public enum ConnectionState {
    NONE(false),
    HANDSHAKE(false),
    AUTHENTICATE(true),
    RUN(true),
    DISCONNECTED(false);

    public final boolean encrypt;

    ConnectionState(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public static ConnectionState get(@NonNull IoSession session)   {
        return SessionData.CONNECTION_STATE.get(session);
    }

    public static ConnectionState get(@NonNull AbstractSession session) {
        return SessionData.CONNECTION_STATE.get(session.getSession());
    }

    public static void advance(@NonNull IoSession session)   {
        ConnectionState curr = get(session);
        if (curr == DISCONNECTED)   {
            throw new IllegalStateException("Already complete!");
        }
        SessionData.CONNECTION_STATE.set(session, values()[curr.ordinal() + 1]);
    }

    public static void advance(@NonNull AbstractSession session) {
        advance(session.getSession());
    }
}
