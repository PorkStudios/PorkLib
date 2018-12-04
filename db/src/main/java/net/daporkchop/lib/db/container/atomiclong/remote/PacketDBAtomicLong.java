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

package net.daporkchop.lib.db.container.atomiclong.remote;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.container.atomiclong.LocalDBAtomicLong;
import net.daporkchop.lib.db.local.LocalDB;
import net.daporkchop.lib.db.remote.RemoteDBConnection;
import net.daporkchop.lib.db.remote.protocol.ContainerPacket;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.packet.Codec;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
public class PacketDBAtomicLong extends ContainerPacket {
    public Action action;

    public long value;

    public PacketDBAtomicLong(String name, long actionId, boolean response, @NonNull Action action) {
        super(name, actionId, response);
        this.action = action;
    }

    public PacketDBAtomicLong(String name, long actionId, boolean response, @NonNull Action action, long value) {
        super(name, actionId, response);
        this.action = action;
        this.value = value;
    }

    @Override
    public void read(@NonNull DataIn in) throws IOException {
        super.read(in);
        this.response = in.readBoolean();
        this.action = in.readEnum(Action::valueOf);
        if ((this.response && this.action.valueResponse) || (!this.response && this.action.valueRequest)) {
            this.value = in.readLong();
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        super.write(out);
        out.writeBoolean(this.response);
        out.writeEnum(this.action);
        if ((this.response && this.action.valueResponse) || (!this.response && this.action.valueRequest)) {
            out.writeLong(this.value);
        }
    }

    @RequiredArgsConstructor
    public enum Action {
        ADD_AND_GET(true, true),
        GET_AND_ADD(true, true),
        INCREMENT_AND_GET(false, true),
        GET_AND_INCREMENT(false, true),
        DECREMENT_AND_GET(false, true),
        GET_AND_DECREMENT(false, true),
        GET(false, true),
        SET(true, false),
        GET_AND_SET(true, true);

        public final boolean valueRequest;
        public final boolean valueResponse;
    }

    public static class DBAtomicLongCodec implements Codec<PacketDBAtomicLong, RemoteDBConnection> {
        @Override
        @SuppressWarnings("unchecked")
        public void handle(@NonNull PacketDBAtomicLong packet, @NonNull Channel channel, @NonNull RemoteDBConnection connection) {
            if (packet.response) {
            } else {
                LocalDBAtomicLong dbAtomicLong = connection.<LocalDB>getDb().get(packet.name);
                switch (packet.action) {
                    case GET: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.get()));
                    }
                    return;
                    case SET: {
                        dbAtomicLong.set(packet.value);
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action));
                    }
                    return;
                    case GET_AND_SET: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.getAndSet(packet.value)));
                    }
                    return;
                    case ADD_AND_GET: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.addAndGet(packet.value)));
                    }
                    return;
                    case GET_AND_ADD: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.getAndAdd(packet.value)));
                    }
                    return;
                    case INCREMENT_AND_GET: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.incrementAndGet()));
                    }
                    return;
                    case DECREMENT_AND_GET: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.decrementAndGet()));
                    }
                    return;
                    case GET_AND_INCREMENT: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.getAndIncrement()));
                    }
                    return;
                    case GET_AND_DECREMENT: {
                        channel.send(new PacketDBAtomicLong(packet.name, packet.actionId, true, packet.action, dbAtomicLong.getAndDecrement()));
                    }
                    return;
                }
            }
        }

        @Override
        public PacketDBAtomicLong createInstance() {
            return new PacketDBAtomicLong();
        }
    }
}
