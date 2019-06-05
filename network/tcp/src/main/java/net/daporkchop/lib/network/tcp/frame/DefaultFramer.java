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

package net.daporkchop.lib.network.tcp.frame;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * A default {@link Framer} implementation that uses a 4-byte value for the length field, channel id and protocol id.
 *
 * @author DaPorkchop_
 */
public class DefaultFramer<S extends AbstractUserSession<S>> extends LengthPrefixedFramer<S> {
    @Override
    protected int lengthFieldLength() {
        return 4;
    }

    @Override
    protected void writeLengthField(@NonNull ByteBuf buf, int length) {
        buf.writeInt(length);
    }

    @Override
    protected int readLengthField(@NonNull ByteBuf buf) {
        return buf.readInt();
    }

    @Override
    protected int channelIdLength() {
        return 4;
    }

    @Override
    protected void writeChannelId(@NonNull ByteBuf buf, int channelId) {
        buf.writeInt(channelId);
    }

    @Override
    protected int readChannelId(@NonNull ByteBuf buf) {
        return buf.readInt();
    }

    @Override
    protected int protocolIdLength() {
        return 4;
    }

    @Override
    protected void writeProtocolId(@NonNull ByteBuf buf, int protocolId) {
        buf.writeInt(protocolId);
    }

    @Override
    protected int readProtocolId(@NonNull ByteBuf buf) {
        return buf.readInt();
    }
}
