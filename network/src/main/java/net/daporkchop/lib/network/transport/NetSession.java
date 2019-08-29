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

package net.daporkchop.lib.network.transport;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.OldDataIn;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.PSession;
import net.daporkchop.lib.network.session.encode.SelfMessageEncoder;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.session.handle.SelfSessionHandler;
import net.daporkchop.lib.network.util.PacketMetadata;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

/**
 * An internally-used representation of a session, used as an extra layer of abstraction to allow for creation of
 * custom transport protocols rather than forcing everyone to use Netty.
 *
 * @author DaPorkchop_
 */
public interface NetSession<S extends AbstractUserSession<S>> extends PSession<NetSession<S>, S>, SelfSessionHandler, SelfMessageEncoder {
    long ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET = PUnsafe.pork_getOffset(AbstractUserSession.class, "internalSession");

    /**
     * @return this session's user session instance
     */
    S userSession();

    /**
     * @return whether or not this connection is an incoming connection
     */
    boolean incoming();

    @Override
    default void onOpened(boolean incoming) {
        this.userSession().onOpened(incoming);
    }

    @Override
    default void onClosed() {
        this.userSession().onClosed();
    }

    @Override
    default void onException(@NonNull Exception e) {
        this.userSession().onException(e);
    }

    @Override
    default void onReceive(@NonNull OldDataIn in, @NonNull PacketMetadata metadata) throws IOException {
        this.userSession().onReceive(in, metadata);
    }

    @Override
    default void encodeMessage(@NonNull Object msg, @NonNull PacketMetadata metadata, @NonNull SendCallback callback) {
        this.userSession().encodeMessage(msg, metadata, callback);
    }
}
