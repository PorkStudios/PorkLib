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

package mc;

import mc.packet.HandshakePacket;
import mc.packet.PingPacket;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.tcp.TCPEngine;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class TestMCPinger implements Logging {
    protected static final String HOST = "mc.pepsi.team";
    protected static final int PORT = 25565;

    public static void main(String... args) {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting client...");

        PClient<MCSession> client = ClientBuilder.of(new MinecraftPingProtocol())
                                                 .engine(TCPEngine.defaultInstance())
                                                 .address(new InetSocketAddress(HOST, PORT))
                                                 .build();

        logger.info("Pinging server...");
        client.send(new HandshakePacket(-1, HOST, PORT, 0x01))
              .write(out -> out.writeVarInt(0x00)) //currently there's no method for protocol states, so we have to hack this in due to conflicting packet IDs
              .sendFlush(new PingPacket(System.currentTimeMillis()));

        client.userSession().ping.addListener(ping -> {
            logger.success("Response: %s", client.userSession().response)
                  .success("Ping: %dms", ping);
            client.closeAsync().addListener(v -> logger.success("Closed."));
        });
    }
}
