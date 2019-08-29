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

package net.daporkchop.lib.network.session.encode;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.OldDataOut;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * Encodes messages into a network-ready binary form.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface BinaryEncoder<S extends AbstractUserSession<S>> extends MessageEncoder<S> {
    @Override
    default void encodeMessage(@NonNull S session, @NonNull Object msg, @NonNull PacketMetadata metadata, @NonNull SendCallback callback) {
        try (BinaryOut out = BinaryOut.get(metadata, callback, null)) {
            this.encodeMessage(session, msg, out, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes a message into binary.
     *
     * @param session  the session
     * @param msg      the message to encode
     * @param out      a {@link OldDataOut} to write data to. This will buffer all data written to it, buffered data will only
     *                 be sent after {@link OldDataOut#flush()} or {@link OldDataOut#close()} is called or this method returns
     * @param metadata packet metadata
     * @throws IOException if an IO exception occurs you dummy
     */
    void encodeMessage(@NonNull S session, @NonNull Object msg, @NonNull OldDataOut out, @NonNull PacketMetadata metadata) throws IOException;
}
