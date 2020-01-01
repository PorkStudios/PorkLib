/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.network.session.encode.SelfMessageEncoder;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.session.handle.SelfSessionHandler;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractUserSession<S extends AbstractUserSession<S>> implements BaseUserSession<S, S>, SelfSessionHandler, SelfMessageEncoder {
    private final NetSession<S> internalSession = null;

    @Override
    public void onOpened(boolean incoming) {
    }

    @Override
    public void onClosed() {
    }

    @Override
    public void onException(@NonNull Exception e) {
        logger.alert(new RuntimeException(e));
        this.closeAsync();
    }

    @Override
    public abstract void onReceive(@NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException;

    @Override
    public abstract void encodeMessage(@NonNull Object msg, @NonNull PacketMetadata metadata, @NonNull SendCallback callback);
}
