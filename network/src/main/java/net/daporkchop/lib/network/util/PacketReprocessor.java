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

package net.daporkchop.lib.network.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.ec.EllipticCurveKeyCache;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.protocol.pork.PorkConnection;
import net.daporkchop.lib.network.protocol.pork.packet.HandshakeCompletePacket;
import net.daporkchop.lib.network.protocol.pork.packet.HandshakeInitPacket;
import net.daporkchop.lib.network.protocol.pork.packet.HandshakeResponsePacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Getter
public class PacketReprocessor {
    @NonNull
    private final PorkConnection connection;
    private CompressionHelper compression = Compression.NONE;
    private CryptographySettings cryptographySettings;
    private Cipher cipher;

    public PacketReprocessor(@NonNull PorkConnection connection)    {
        this.connection = connection;
        this.cryptographySettings = connection.getEndpoint() instanceof PorkServer ? ((PorkServer) connection.getEndpoint()).getCryptographySettings() : null;
    }

    public HandshakeResponsePacket initClient(@NonNull HandshakeInitPacket packet) {
        this.cryptographySettings = packet.cryptographySettings;
        this.compression = packet.compression;

        EllipticCurveKeyPair localPair = this.cryptographySettings.getKeyPair() == null ? null : EllipticCurveKeyCache.getKeyPair(this.cryptographySettings.getKeyPair().getCurveType());
        if (localPair != null) {
            this.cipher = this.cryptographySettings.getCipher(localPair, CipherInitSide.CLIENT);
        }
        return new HandshakeResponsePacket(
                new CryptographySettings(localPair, this.cryptographySettings), //placeholder, we just need this as a container for the key pair
                this.connection.getEndpoint().getPacketRegistry().getProtocols().stream().map(Version::new).collect(Collectors.toList())
        );
    }

    public HandshakeCompletePacket initServer(@NonNull HandshakeResponsePacket packet) {
        if (packet.cryptographySettings.getKeyPair() != null) {
            this.cipher = packet.cryptographySettings.getCipher(this.cryptographySettings.getKeyPair(), CipherInitSide.SERVER);
        }
        return new HandshakeCompletePacket();
    }

    public OutputStream wrap(@NonNull OutputStream out) throws IOException {
        if (this.connection.getState().shouldCompress) {
            out = this.compression.deflate(out);
        }
        if (this.cipher != null && this.connection.getState().shouldEncrypt) {
            out = this.cipher.encryptionStream(out);
        }
        return out;
    }

    public InputStream wrap(@NonNull InputStream in) throws IOException {
        if (this.connection.getState().shouldCompress)  {
            in = this.compression.inflate(in);
        }
        if (this.cipher != null && this.connection.getState().shouldEncrypt)    {
            in = this.cipher.decryptionStream(in);
        }
        return in;
    }
}
