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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.io.IOException;

/**
 * A {@link Protocol} that also encodes and decodes packets.
 *
 * @author DaPorkchop_
 */
public interface DataProtocol<S extends AbstractUserSession<S>> extends Protocol<S> {
    /**
     * @return this protocol's codec
     */
    Codec<S> codec();

    /**
     * @author DaPorkchop_
     */
    interface Codec<S extends AbstractUserSession<S>> {
        /**
         * Decodes a packet, reading no data more than required.
         *
         * @param in      a {@link DataIn} to read data from
         * @param session the session that the data was received on
         * @return a decoded packet
         */
        Object decode(@NonNull DataIn in, @NonNull S session) throws IOException;

        /**
         * Encodes a message.
         *
         * @param out     a {@link DataOut} to write data to
         * @param msg     the message that should be sent
         * @param session the session that the message will be sent on
         */
        void encode(@NonNull DataOut out, @NonNull Object msg, @NonNull S session) throws IOException;
    }
}
