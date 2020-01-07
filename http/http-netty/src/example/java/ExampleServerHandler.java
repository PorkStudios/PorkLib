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

import lombok.NonNull;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.server.ResponseBuilder;
import net.daporkchop.lib.http.server.handle.ServerHandler;
import net.daporkchop.lib.http.util.StatusCodes;

import java.io.File;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class ExampleServerHandler implements ServerHandler {
    @Override
    public void handleQuery(@NonNull Query query) throws Exception {
        logger.info("%s", query);
    }

    @Override
    public void handleHeaders(@NonNull Query query, @NonNull HeaderMap headers, @NonNull ResponseBuilder response) throws Exception {
        //headers.forEach((key, value) -> logger.info("  %s: %s", key, value));

        response.status(StatusCodes.OK)
                //.body("name jeff lol");
                //.bodyTextUTF8("name jeff lol");
                .body(StandardContentType.TEXT_PLAIN, new File("/home/daporkchop/Desktop/betterdiscord.css"));

        //throw new GenericHttpException(StatusCodes.Im_A_Teapot);
    }
}
