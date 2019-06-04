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
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.session.pipeline.Pipeline;
import net.daporkchop.lib.network.tcp.netty.session.TCPSession;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class HTTPProtocol implements SimpleDataProtocol<HTTPSession>, SimpleHandlingProtocol<HTTPSession>, Logging {
    @Override
    public HTTPSession newSession() {
        return new HTTPSession();
    }

    @Override
    public void initPipeline(@NonNull Pipeline<HTTPSession> pipeline, @NonNull HTTPSession session) {
        pipeline.replace("tcp_framer", new HTTPPacketFramer());

        ((TCPSession<HTTPSession>) session.internalSession()).enableSSLClient(TestHTTPGet.HOST, 443);
    }

    @Override
    public Object decode(@NonNull HTTPSession session, @NonNull DataIn in, int channel) throws IOException {
        return new String(in.readAllAvailableBytes(), UTF8.utf8);
    }

    @Override
    public void encode(@NonNull DataOut out, @NonNull HTTPSession session, @NonNull Object msg, int channel) throws IOException {
        if (msg instanceof String)  {
            out.write(((String) msg).getBytes(UTF8.utf8));
        } else {
            throw new IllegalArgumentException(msg.getClass().getCanonicalName());
        }
    }

    @Override
    public void onReceived(@NonNull HTTPSession session, @NonNull Object msg, int channel) {
        if (msg instanceof String)  {
            if (session.headersComplete)    {
                session.body += msg;
            } else {
                session.headers += msg;
            }
        } else {
            throw new IllegalArgumentException(msg.getClass().getCanonicalName());
        }
    }

    @Override
    public void onBinary(@NonNull HTTPSession session, @NonNull DataIn in, int channel) throws IOException {
    }
}
