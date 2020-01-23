/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.impl.netty.server.NettyHttpServer;
import net.daporkchop.lib.http.impl.netty.util.NettyHttpUtil;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.server.HttpServer;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.GenericHttpException;
import net.daporkchop.lib.http.util.exception.HttpException;
import net.daporkchop.lib.logging.LogAmount;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class HttpServerExample {
    public static void main(String... args) throws HttpException {
        logger.enableANSI().setLogAmount(LogAmount.DEBUG);

        HttpServer server = new NettyHttpServer();

        server.handler(new ExampleServerHandler());

        server.bind(new InetSocketAddress(8080)).syncUninterruptibly();

        try (Scanner scanner = new Scanner(System.in))  {
            scanner.nextLine();
        }

        server.close().syncUninterruptibly();
    }
}