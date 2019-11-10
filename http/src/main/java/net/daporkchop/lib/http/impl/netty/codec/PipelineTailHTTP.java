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

package net.daporkchop.lib.http.impl.netty.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import net.daporkchop.lib.http.util.ConnectionState;
import net.daporkchop.lib.http.util.exception.GenericHTTPException;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * The last member of every HTTP connection pipeline.
 *
 * @author DaPorkchop_
 */
public abstract class PipelineTailHTTP extends ChannelDuplexHandler {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg.getClass() == ConnectionState.class)    {
            ConnectionState state = (ConnectionState) msg;
            Attribute<ConnectionState> attr = ctx.channel().attr(KEY_STATE);
            if (state.ordinal() <= attr.get().ordinal())    {
                //don't allow going back a state
                throw GenericHTTPException.Internal_Server_Error;
            } else {
                attr.set(state);
            }
        } else {
            ctx.write(msg, promise);
        }
    }
}
