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

import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.tcp.TCPEngine;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class TestHTTPGet implements Logging {
    protected static String data = "";

    public static void main(String... args) {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG).info("Starting client...");

        PClient<HTTPSession> client = ClientBuilder.of(new HTTPProtocol())
                .engine(TCPEngine.of(new HTTPPacketFramer()))
                .address(new InetSocketAddress("gist.githubusercontent.com", 80))
                .build();

        client.send("GET /DaMatrix/8b7ff92fcc7e49c0f511a8ed207d8e92/raw/teampepsi-server-players.json HTTP/1.1\r\n" +
                        "Host: gist.githubusercontent.com\r\n" +
                        "User-Agent: PorkLib\r\n\r\n")
                .flushBuffer();

        logger.info("Headers:\n%s", client.userSession().headers.get());
        PorkUtil.sleep(200L); //TODO: event handlers so we can wait for the connection to be closed
        logger.info("Body:\n%s", client.userSession().body);
        client.closeAsync();
    }
}
