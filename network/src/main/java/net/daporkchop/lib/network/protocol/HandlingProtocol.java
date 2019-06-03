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

package net.daporkchop.lib.network.protocol;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.io.IOException;

/**
 * A {@link Protocol} that can handle messages when they reach the end of the pipeline.
 * <p>
 * Protocols which inherit from this will prevent the methods {@link AbstractUserSession#onReceived(Object, int)}
 * and {@link AbstractUserSession#onBinary(DataIn, int)} from firing.
 *
 * @author DaPorkchop_
 */
public interface HandlingProtocol<S extends AbstractUserSession<S>> extends Protocol<S> {
    /**
     * @return this protocol's message handler
     */
    Handler<S> handler();

    interface Handler<S> {
        /**
         * Fired if a message reaches the end of the pipeline.
         *
         * @param session the session that the message was received on
         * @param msg     the message that was received
         * @param channel the channel that the message was received on
         */
        void onReceived(@NonNull S session, @NonNull Object msg, int channel);

        /**
         * Fired if raw binary data reaches the end of the pipeline.
         * <p>
         * Whether or not a certain message qualifies as binary or not depends on the transport engine.
         *
         * @param session the session that the data was received on
         * @param in      a {@link DataIn} to read data from
         * @param channel the channel that the data was received on
         */
        void onBinary(@NonNull S session, @NonNull DataIn in, int channel) throws IOException;
    }
}
