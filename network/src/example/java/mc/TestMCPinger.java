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

import io.netty.channel.ChannelOption;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.transport.tcp.TCPEngine;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author DaPorkchop_
 */
public class TestMCPinger implements Logging {
    protected static long startTime = -1L;
    protected static CompletableFuture<Long> endTime = new CompletableFuture<>();

    public static void main(String... args) {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting client...");

        PClient client = new ClientBuilder()
                .engine(TCPEngine.builder()
                        .clientOption(ChannelOption.TCP_NODELAY, true)
                        .framer(new MinecraftPacketFramer())
                        .build())
                .address(new InetSocketAddress("mc.pepsi.team", 25565))
                .protocol(new MinecraftPingProtocol())
                .build();

        logger.info("Pinging server...");
        client.write(out -> out
                //handshake
                .writeVarInt(0x00) //packet id
                .writeVarInt(-1) //protocol version
                .writeUTF("localhost")
                .writeUShort(25565) //port
                .writeVarInt(0x01) //next state
        ).write(out -> out
                //request
                .writeVarInt(0x00) //packet id
        ).write(out -> out
                //ping
                .writeVarInt(0x01)
                .writeLong(startTime = System.currentTimeMillis())
        ).flushBuffer();

        endTime.thenAccept(ping -> {
            logger.success("Ping: %dms", System.currentTimeMillis() - ping);
            client.closeAsync().addListener(v -> logger.success("Closed."));
        });
    }
}