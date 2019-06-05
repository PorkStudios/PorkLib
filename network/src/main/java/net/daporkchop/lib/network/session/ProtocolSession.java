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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.reference.InstancePool;
import net.daporkchop.lib.common.util.GenericMatcher;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * A {@link AbstractUserSession} that uses a {@link Protocol}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class ProtocolSession<S extends ProtocolSession<S, P>, P extends Protocol<P, S>> extends AbstractUserSession<S> {
    @NonNull
    protected final P protocol;

    public ProtocolSession()    {
        this.protocol = InstancePool.getInstance(GenericMatcher.uncheckedFind(ProtocolSession.class, this.getClass(), "P"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onOpened(boolean incoming) {
        this.protocol.onOpened((S) this, incoming);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onClosed() {
        this.protocol.onClosed((S) this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onException(@NonNull Exception e) {
        this.protocol.onException((S) this, e);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(@NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException {
        this.protocol.onReceive((S) this, in, metadata);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encodeMessage(@NonNull Object msg, @NonNull PacketMetadata metadata, @NonNull SendCallback callback) {
        this.protocol.encodeMessage((S) this, msg, metadata, callback);
    }
}
