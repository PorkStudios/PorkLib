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
import org.apache.mina.core.session.IoSession;

/**
 * The various types of session data that exist for each session
 *
 * @author DaPorkchop_
 */
public enum SessionData {
    ECDH_CURVE_TYPE,
    CIPHER_HELPER,
    COMPRESSION,
    PROTOCOL_SESSION,
    ENCAPSULATED_SESSION,
    CONNECTION_STATE,
    SESSION_RANDOM,
    PASSWORD_AUTH;

    /**
     * Gets a value from a session using this key
     *
     * @param session the session to get from
     * @param <T>     the type of the data to get
     * @return the value at the given key
     */
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull IoSession session) {
        return (T) session.getAttribute(this);
    }

    /**
     * Sets a value in a session at this key
     *
     * @param session the session to set
     * @param val     the value to set to
     */
    public void set(@NonNull IoSession session, Object val) {
        session.setAttribute(this, val);
    }
}
