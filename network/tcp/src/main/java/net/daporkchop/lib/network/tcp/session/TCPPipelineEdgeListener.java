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

package net.daporkchop.lib.network.tcp.session;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.pipeline.PipelineEdgeListener;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TCPPipelineEdgeListener<S extends AbstractUserSession<S>> extends PipelineEdgeListener<S> {
    @NonNull
    protected final TCPNetSession<S> session;

    @Override
    public void fireSending(@NonNull S session, @NonNull Object msg, Reliability reliability, int channel) {
        try {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) msg;
                switch (buf.nioBufferCount()) {
                    case 0:
                        break;
                    case 1:
                        this.session.channel.write(buf.nioBuffer()); //TODO: figure out how to deal with incomplete writes
                        break;
                    default:
                        this.session.channel.write(buf.nioBuffers());
                        break;
                }
            } else if (msg instanceof ByteBuffer) {
                this.session.channel.write((ByteBuffer) msg);
            } else {
                throw new IllegalArgumentException(String.format("TCP cannot send message type: \"%s\"!", PorkUtil.getClassName(msg)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
