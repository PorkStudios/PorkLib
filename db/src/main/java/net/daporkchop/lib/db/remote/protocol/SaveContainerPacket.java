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

package net.daporkchop.lib.db.remote.protocol;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.db.local.LocalContainer;
import net.daporkchop.lib.db.local.LocalDB;
import net.daporkchop.lib.db.remote.RemoteDBConnection;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.packet.Codec;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
public class SaveContainerPacket extends ContainerPacket {
    public SaveContainerPacket(String name, long actionId, boolean response) {
        super(name, actionId, response);
    }

    public static class SaveContainerCodec implements Codec<SaveContainerPacket, RemoteDBConnection>, Logging {
        @Override
        public void handle(@NonNull SaveContainerPacket packet, @NonNull Channel channel, @NonNull RemoteDBConnection connection) {
            if (packet.response)    {
            } else {
                LocalContainer container = connection.<LocalDB>getDb().get(packet.name);
                if (container == null)  {
                    throw this.exception("Unknown container: ${0}", packet.name);
                }
                try {
                    container.save();
                } catch (IOException e) {
                    throw this.exception("Unable to save container: ${0}", e, packet.name);
                }
                channel.send(new SaveContainerPacket(packet.name, packet.actionId, true));
            }
        }

        @Override
        public SaveContainerPacket createInstance() {
            return new SaveContainerPacket();
        }
    }
}
