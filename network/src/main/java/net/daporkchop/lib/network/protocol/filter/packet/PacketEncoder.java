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

package net.daporkchop.lib.network.protocol.filter.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.Packet;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.packet.WrappedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import net.daporkchop.lib.network.util.IoBufferOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.io.OutputStream;

/**
 * Encodes encapsulated packets into sendable data
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public class PacketEncoder implements ProtocolEncoder, CommonMethods {
    @NonNull
    private final AbstractEndpoint endpoint;

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        ConnectionState state = ConnectionState.get(session);
        EncapsulatedPacket encapsulated;
        if (message instanceof EncapsulatedPacket) {
            //send packet
            encapsulated = (EncapsulatedPacket) message;
        } else if (message instanceof Packet) {
            if (state == ConnectionState.RUN) {
                //wrap packet before sending
                encapsulated = new WrappedPacket((Packet) message);
            } else {
                throw new IllegalStateException("Cannot send a packet in state: " + state.name());
            }
        } else {
            throw new IllegalArgumentException("Invalid packet type: " + message);
        }

        //System.out.println("Encoding " + encapsulated.getClass().getCanonicalName());

        //write packet
        IoBuffer buf = IoBuffer.allocate(0).setAutoExpand(true);
        DataOut dataOut;
        {
            OutputStream stream = new IoBufferOutputStream(buf);

            if (ConnectionState.get(session).encrypt) {
                BlockCipherHelper helper = getHelper(session);
                if (helper != null) {
                    stream = helper.encryptionStream(stream);
                }
                //System.out.println("Encoding with " + SessionData.COMPRESSION.<EnumCompression>get(session).name());
                stream = SessionData.COMPRESSION.<EnumCompression>get(session).compressStream(stream);
            }

            dataOut = new DataOut(stream);
        }
        dataOut.writeByte(encapsulated.getId());
        encapsulated.write(dataOut, endpoint.getPacketProtocol());
        dataOut.flush();
        dataOut.close();
        buf.flip();
        out.write(buf);
    }

    @Override
    public void dispose(IoSession session) {
    }
}
