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
import lombok.Setter;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.ec.EllipticCurveKeyCache;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.ChannelImplementation;
import net.daporkchop.lib.network.pork.packet.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author DaPorkchop_
 */
@Getter
public class PacketReprocessor implements Logging {
    @NonNull
    private final ChannelImplementation channel;
    @Setter
    private CompressionHelper compression;
    @Setter
    private CryptographySettings cryptographySettings;
    private Cipher cipher;

    public PacketReprocessor(@NonNull ChannelImplementation channel) {
        this.channel = channel;
        //TODO: check if the CHANNEL is a server, not the endpoint. will be needed for p2p where the endpoint is a client and a server
        this.cryptographySettings = null;
        this.compression = Compression.NONE; //TODO: support compression
    }

    public EncryptionStartedPacket init(@NonNull StartEncryptionPacket packet) {
        if (this.cryptographySettings != null)  {
            throw new IllegalStateException("cryptography already initialized!");
        } else if (packet.channelId != this.channel.getId())    {
            throw new IllegalStateException("invalid channel id!");
        }
        this.cryptographySettings = packet.cryptographySettings;

        EllipticCurveKeyPair localPair = this.cryptographySettings.getKeyPair() == null ? null : EllipticCurveKeyCache.getKeyPair(this.cryptographySettings.getKeyPair().getCurveType());
        if (localPair != null) {
            this.cipher = this.cryptographySettings.getCipher(localPair, CipherInitSide.CLIENT);
        }
        return new EncryptionStartedPacket(
                localPair == null ? new CryptographySettings() : new CryptographySettings(localPair, this.cryptographySettings), //placeholder, we just need this as a container for the key pair
                packet.channelId
        );
    }

    public void init(@NonNull EncryptionStartedPacket packet) {
        if (packet.cryptographySettings.getKeyPair() != null) {
            this.cipher = packet.cryptographySettings.getCipher(this.cryptographySettings.getKeyPair(), CipherInitSide.SERVER);
        }
    }

    public OutputStream wrap(@NonNull OutputStream out, boolean allowEncryption) throws IOException {
        logger.debug(
                "[${2}] Wrapping output. (${0} with ${1})",
                this.compression,
                allowEncryption ? this.cipher : null,
                this.channel.getEndpoint().getName()
        );
        if (allowEncryption && this.cipher != null && this.channel.isEncryptionReady()) {
            out = this.cipher.encrypt(out);
        }
        return this.compression.deflate(out);
    }

    public InputStream wrap(@NonNull InputStream in, boolean allowEncryption) throws IOException {
        logger.debug(
                "[${2}] Wrapping input.  (${0} with ${1})",
                this.compression,
                allowEncryption ? this.cipher : null,
                this.channel.getEndpoint().getName()
        );
        if (allowEncryption && this.cipher != null && this.channel.isEncryptionReady()) {
            in = this.cipher.decrypt(in);
        }
        return this.compression.inflate(in);
    }
}
