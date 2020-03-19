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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.message.Message;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.server.ResponseBuilder;
import net.daporkchop.lib.http.server.handle.ServerHandler;
import net.daporkchop.lib.http.util.StatusCodes;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class ExampleServerHandler implements ServerHandler {
    @Override
    public int maxBodySize() {
        return 1 << 20;
    }

    @Override
    public void handleQuery(@NonNull Query query) throws Exception {
        logger.info("%s", query);
    }

    @Override
    public void handleHeaders(@NonNull Query query, @NonNull HeaderMap headers) throws Exception {
        //headers.forEach((key, value) -> logger.info("  %s: %s", key, body));
    }

    @Override
    public void handle(@NonNull Query query, @NonNull Message message, @NonNull ResponseBuilder response) throws Exception {
        if (!query.method().hasRequestBody() && message.body() != null)    {
            throw new IllegalStateException("May not send a body for " + query.method());
        }

        if (message.body() != null) {
            if (message.body() instanceof ByteBuf)  {
                logger.info("Body: %s", ((ByteBuf) message.body()).toString(StandardCharsets.UTF_8));
            } else {
                logger.info("Body: %s", message.body());
            }
        }

        response.status(StatusCodes.OK)
                //.body("name jeff lol")
                //.bodyTextUTF8("name jeff lol")
                .body(StandardContentType.TEXT_PLAIN, new File("/home/daporkchop/Desktop/betterdiscord.css"))
                .addHeader("my-name", "jeff");

        //throw new GenericHttpException(StatusCodes.Im_A_Teapot);
    }
}
