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

import io.netty.util.Recycler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
public final class ChanneledPacket<P> {
    protected static final Recycler<ChanneledPacket<Object>> RECYCLER = new Recycler<ChanneledPacket<Object>>() {
        @Override
        @SuppressWarnings("unchecked")
        protected ChanneledPacket newObject(Handle<ChanneledPacket<Object>> handle) {
            return new ChanneledPacket<>(handle);
        }
    };

    public static <P> ChanneledPacket<P> getInstance(@NonNull P packet, int channel)  {
        return RECYCLER.get().packet(packet).channel(channel);
    }

    @NonNull
    protected final Recycler.Handle<ChanneledPacket<P>> handle;

    protected P packet;
    protected int channel;

    @SuppressWarnings("unchecked")
    public <NEW_P> ChanneledPacket<NEW_P> packet(@NonNull NEW_P packet)    {
        ((ChanneledPacket<NEW_P>) this).packet = packet;
        return (ChanneledPacket<NEW_P>) this;
    }

    public void release()   {
        this.packet = null;
        this.channel = -1;
        this.handle.recycle(this);
    }
}