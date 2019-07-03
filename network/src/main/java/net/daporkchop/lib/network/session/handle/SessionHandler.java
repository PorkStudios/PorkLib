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

package net.daporkchop.lib.network.session.handle;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * Handles session events.
 *
 * @author DaPorkchop_
 */
public interface SessionHandler<S extends AbstractUserSession<S>> {
    /**
     * Fired when a session is opened (connection is complete).
     *
     * @param session  the session
     * @param incoming whether or not the session was incoming (remote endpoint initiated connection to local
     *                 endpoint) or outgoing (local endpoint initiated connection to remote endpoint)
     */
    void onOpened(@NonNull S session, boolean incoming);

    /**
     * Fired when a session is closed (connection is onClosed).
     *
     * @param session the session
     */
    void onClosed(@NonNull S session);

    /**
     * Fired when an exception is caught at any point when processing something related to a specific session.
     *  @param session the session
     * @param e       the exception
     */
    void onException(@NonNull S session, @NonNull Exception e);

    /**
     * Handles incoming binary data.
     *
     * @param session  the session that the data was received on
     * @param in       a {@link DataIn} to read data from
     * @param metadata the metadata of the received data. While this parameter is guaranteed to be non-null, no
     *                 certainties are made about whether all fields are set (can be checked using the corresponding
     *                 methods in {@link PacketMetadata}), and keeping a reference to the instance outside of the
     *                 scope of this method should be considered unsafe.
     */
    void onReceive(@NonNull S session, @NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException;
}
