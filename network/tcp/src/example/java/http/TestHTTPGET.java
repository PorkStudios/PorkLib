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

import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.netty.LoopPool;
import net.daporkchop.lib.network.tcp.TCPEngine;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class TestHTTPGET implements Logging {
    protected static final String HOST = "maven.daporkchop.net";
    protected static final int PORT = 443;
    protected static final String URL = "/";

    public static void main(String... args)    {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting client...");

        LoopPool.DEFAULT_THREAD_COUNT = 1;
        PClient<HTTPSession> client = ClientBuilder.of(HTTPSession::new)
                .engine(TCPEngine.builder().enableSSLClient().framerFactory(HTTPFramer::new).build())
                .address(new InetSocketAddress(HOST, PORT))
                .build();

        logger.info("Sending request...");
        client.sendFlushNow("GET " + URL + " HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "User-Agent: PorkLib\r\n\r\n");
        logger.success("Request sent.");

        client.userSession().promise.addListener(f -> {
            if (f.isSuccess()) {
                logger.success((String) f.getNow());
            } else {
                logger.alert(f.cause());
            }
        });
    }
}
