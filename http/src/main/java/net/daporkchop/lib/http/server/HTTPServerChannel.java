/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.http.RequestType;
import net.daporkchop.lib.http.parameter.ParameterRegistry;
import net.daporkchop.lib.http.parameter.Parameters;
import net.daporkchop.lib.logging.Logging;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
class HTTPServerChannel extends NioSocketChannel implements Logging {
    @NonNull
    private final HTTPServer server;

    @NonNull
    private RequestType type;
    @NonNull
    private String path;
    private Parameters parameters;

    public HTTPServerChannel(Channel parent, SocketChannel socket, @NonNull HTTPServer server) {
        super(parent, socket);
        this.server = server;
    }

    public void handle(@NonNull ByteBuf request) {
        String[] lines = request.toString(UTF8.utf8).split("\r\n");
        String s = lines[0];
        int i1 = s.indexOf(' ');
        int i2 = s.lastIndexOf(' ');
        String s1 = s.substring(0, i1);
        try {
            this.type = RequestType.valueOf(s1);
        } catch (IllegalArgumentException e) {
            throw this.exception("Unknown request type: ${0}", s1);
        }
        this.path = s.substring(i1 + 1, i2 + 1);
        this.parameters = new Parameters(
                Arrays.stream(lines, 1, lines.length).filter(s2 -> !s2.isEmpty()).collect(Collectors.toList()),
                ParameterRegistry.def()
        );
    }
}
