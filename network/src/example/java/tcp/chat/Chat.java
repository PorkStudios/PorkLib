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

package tcp.chat;

import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.tcp.TCPEngine;
import tcp.chat.packet.ChatPacket;
import tcp.chat.packet.LoginPacket;
import tcp.chat.packet.MessagePacket;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author DaPorkchop_
 */
public class Chat implements Logging {
    protected static final int PORT = 29873;

    public static void main(String... args) {
        logger.enableANSI().redirectStdOut().setLogAmount(LogAmount.DEBUG);

        PServer<ChatSession> server = ServerBuilder.of(new ChatProtocol())
                                                   .engine(TCPEngine.defaultInstance())
                                                   .bind(new InetSocketAddress(PORT))
                                                   .build();

        PClient<ChatSession> client = ClientBuilder.of(new ChatProtocol())
                                                   .engine(TCPEngine.defaultInstance())
                                                   .address(new InetSocketAddress("localhost", PORT))
                                                   .build();

        try (Scanner scanner = new Scanner(System.in))  {
            String line;
            while (!"/dc".equals(line= scanner.nextLine().trim()))  {
                if (client.userSession().name() == null)    {
                    client.sendFlush(new LoginPacket(client.userSession().name = line));
                } else {
                    client.sendFlush(new ChatPacket(line));
                }
            }
        }

        client.closeAsync().addListener(v -> server.closeAsync());
    }
}
