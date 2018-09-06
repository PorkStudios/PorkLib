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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import net.daporkchop.lib.network.util.IoBufferInputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.InputStream;

/**
 * Decodes received data into encapsulated packets
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public class PacketDecoder implements ProtocolDecoder, CommonMethods {
    @NonNull
    private final AbstractEndpoint endpoint;

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        DataIn dataIn;
        {
            InputStream stream = new IoBufferInputStream(in);

            if (ConnectionState.get(session).encrypt) {
                BlockCipherHelper helper = getHelper(session);
                if (helper != null) {
                    stream = helper.decryptionStream(stream);
                }
                //System.out.println("Decoding with " + SessionData.COMPRESSION.<EnumCompression>get(session).name());
                stream = SessionData.COMPRESSION.<EnumCompression>get(session).inflateStream(stream);
            }

            dataIn = new DataIn(stream);
        }
        byte id = dataIn.readByte();
        EncapsulatedPacket packet = endpoint.getEncapsulatedProtocol().newPacket(id);

        if (packet == null) {
            throw new IllegalStateException("Received invalid packet ID: " + id);
        }

        //System.out.println("Decoding " + packet.getClass().getCanonicalName());

        packet.read(dataIn, endpoint.getPacketProtocol());

        while (dataIn.read() != -1) {
            //dataIn.skip(dataIn.available());
        }
        dataIn.close();
        out.write(packet);
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) {
    }

    @Override
    public void dispose(IoSession session) {
    }
}
