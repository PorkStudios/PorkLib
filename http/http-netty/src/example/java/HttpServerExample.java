/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
