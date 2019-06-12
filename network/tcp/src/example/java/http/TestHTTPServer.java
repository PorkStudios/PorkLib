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

package http;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.netty.LoopPool;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.tcp.TCPEngine;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author DaPorkchop_
 */
public class TestHTTPServer implements Logging {
    public static void main(String... args) {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting server...");

        LoopPool.DEFAULT_THREAD_COUNT = 1;
        PServer<HTTPServerSession> server = ServerBuilder.of(HTTPServerSession::new)
                .engine(TCPEngine.builder().enableSSLServerSelfSigned().<HTTPServerSession>framerFactory(HTTPFramer::new).build())
                .bind(8443)
                .build();

        try (Scanner scanner = new Scanner(System.in))  {
            scanner.nextLine();
        } finally {
            logger.success("Server closing...");
            server.closeAsync().addListener(v -> logger.success("Server closed."));
        }
    }

    static class HTTPServerSession extends HTTPSession<HTTPServerSession>    {
        @Override
        public void onReceive(@NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException {
            if (metadata.protocolId() == 0)  {
                this.send("HTTP/1.1 200 OK\r\n" +
                        "Transfer-Encoding: chunked\r\n\r\n");
                this.send("hello ", 1);
                this.send("world!", 1);
                this.sendFlush("", 1);
            }
        }
    }
}
