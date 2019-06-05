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

package net.daporkchop.lib.network.protocol;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.session.ProtocolSession;
import net.daporkchop.lib.network.session.encode.BinaryEncoder;
import net.daporkchop.lib.network.session.handle.SessionHandler;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * A protocol is a way of moving session event logic outside of the session class itself, and can be used to implement
 * things such as a shared packet id registry.
 *
 * @author DaPorkchop_
 */
public interface Protocol<P extends Protocol<P, S>, S extends ProtocolSession<S, P>> extends SessionHandler<S>, BinaryEncoder<S> {
    @Override
    default void onOpened(@NonNull S session, boolean incoming) {
        session.logger().trace("Session opened: %s", session.remoteAddress());
    }

    @Override
    default void onClosed(@NonNull S session) {
        session.logger().trace("Session closed: %s", session.remoteAddress());
    }

    @Override
    default void onException(@NonNull S session, @NonNull Exception e)  {
        session.logger().alert(e);
        session.closeAsync();
    }

    @Override
    void encodeMessage(@NonNull S session, @NonNull Object msg, @NonNull DataOut out, @NonNull PacketMetadata metadata) throws IOException;

    @Override
    void onReceive(@NonNull S session, @NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException;
}
