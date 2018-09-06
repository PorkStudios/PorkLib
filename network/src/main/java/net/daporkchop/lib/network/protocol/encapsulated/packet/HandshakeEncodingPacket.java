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

package net.daporkchop.lib.network.protocol.encapsulated.packet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.PacketDirection;
import net.daporkchop.lib.network.protocol.PacketHandler;
import net.daporkchop.lib.network.protocol.PacketProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedConstants;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Response to {@link HandshakeClientInitPacket}, followed by {@link HandshakeStartEncryptionPacket}
 *
 * @author DaPorkchop_
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakeEncodingPacket implements EncapsulatedPacket, EncapsulatedConstants {
    @NonNull
    private EnumCompression compression;

    @NonNull
    private BlockCipherType cipherType;

    private BlockCipherMode cipherMode;

    private BlockCipherPadding cipherPadding;

    private ECDHKeyPair ecKey;

    @Override
    public void read(DataIn in, PacketProtocol protocol) throws IOException {
        this.compression = EnumCompression.valueOf(in.readUTF());
        this.cipherType = BlockCipherType.valueOf(in.readUTF());

        if (this.cipherType != BlockCipherType.NONE)    {
            this.cipherMode = BlockCipherMode.valueOf(in.readUTF());
            this.cipherPadding = BlockCipherPadding.valueOf(in.readUTF());
            this.ecKey = ECDHKeyPair.decodePublic(in.readBytesSimple());
        }
    }

    @Override
    public void write(DataOut out, PacketProtocol protocol) throws IOException {
        out.writeUTF(this.compression.name());
        out.writeUTF(this.cipherType.name());

        if (this.cipherType != null)    {
            out.writeUTF(this.cipherMode.name());
            out.writeUTF(this.cipherPadding.name());
            out.writeBytesSimple(this.ecKey.encodePublic());
        }
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENTBOUND;
    }

    @Override
    public byte getId() {
        return ID_HANDSHAKEENCODING;
    }

    public static class HandshakeEncodingHandler implements PacketHandler<HandshakeEncodingPacket, EncapsulatedSession> {
        @Override
        public void handle(HandshakeEncodingPacket packet, EncapsulatedSession session) {
            if (SessionData.CONNECTION_STATE.get(session.getSession()) != ConnectionState.HANDSHAKE)    {
                throw new IllegalStateException("Not currently in handshake mode!");
            }

            SessionData.COMPRESSION.set(session.getSession(), packet.getCompression());

            HandshakeStartEncryptionPacket startEncryption = new HandshakeStartEncryptionPacket();
            if (packet.cipherType != BlockCipherType.NONE) {
                ECDHKeyPair keyPair = AbstractEndpoint.getECKeyPairNow(SessionData.ECDH_CURVE_TYPE.get(session.getSession()));
                CompletableFuture<BlockCipherHelper> future = AbstractEndpoint.getCipherHelper(packet.ecKey.getPublicKey(), keyPair.getPrivateKey(), packet.cipherType, packet.cipherMode, packet.cipherPadding);
                SessionData.CIPHER_HELPER.set(session.getSession(), future);
                startEncryption.setEcKey(keyPair);
            }
            session.send(startEncryption);
        }
    }
}
