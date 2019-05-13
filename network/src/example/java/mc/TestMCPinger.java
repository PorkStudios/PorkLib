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

import io.netty.buffer.PooledByteBufAllocator;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.transport.tcp.TCPEngine;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class TestMCPinger implements Logging {
    public static void main(String... args) {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting client...");

        PClient client = new ClientBuilder()
                .engine(new TCPEngine(new MinecraftPacketFramer()))
                .address(new InetSocketAddress("mc.pepsi.team", 25565))
                .protocol(new MinecraftPingProtocol())
                .build();

        logger.info("Pinging server...");
        client.send(PooledByteBufAllocator.DEFAULT.ioBuffer()
                                                  .writeByte(0x00) //packet id
                                                  .writeByte(0x00) //protocol version
                                                  .writeByte(0x09) //server address length
                                                  .writeBytes("localhost".getBytes()) //server address
                                                  .writeShort(25565) //port
                                                  .writeByte(0x01) //next state
        ).send(PooledByteBufAllocator.DEFAULT.ioBuffer()
                                             .writeByte(0x00) //packet id
        ).sendFuture(PooledByteBufAllocator.DEFAULT.ioBuffer()
                                                   .writeByte(0x01) //packet id
                                                   .writeLong(System.currentTimeMillis()) //value
        ).addListener(v -> logger.debug("Ping sent."));
    }
}
