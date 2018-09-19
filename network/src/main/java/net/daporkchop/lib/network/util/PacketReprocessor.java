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
import lombok.Setter;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.exchange.ECDHHelper;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.conn.ConnectionState;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeCompletePacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeInitPacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeResponsePacket;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PacketReprocessor {
    @NonNull
    private final Endpoint endpoint;

    private CryptographySettings cryptographySettings;

    private EnumCompression compression = EnumCompression.NONE;

    private BlockCipherHelper cipherHelper;

    public PacketReprocessor(@NonNull Endpoint endpoint, @NonNull CryptographySettings cryptographySettings, @NonNull EnumCompression compression) {
        this.endpoint = endpoint;
        this.cryptographySettings = cryptographySettings;
        this.compression = compression;
    }

    public HandshakeResponsePacket initClient(@NonNull HandshakeInitPacket packet) {
        if (this.cryptographySettings != null || this.compression != EnumCompression.NONE || this.cipherHelper != null) {
            throw new IllegalStateException("Handshake is already complete!");
        }

        this.cryptographySettings = packet.cryptographySettings;
        this.compression = packet.compression;

        HandshakeResponsePacket response = new HandshakeResponsePacket();
        if (packet.cryptographySettings.doesEncrypt()) {
            ECDHKeyPair localPair = this.initCrypto();
            response.cryptographySettings = new CryptographySettings(
                    this.cryptographySettings.getCurveType(),
                    localPair,
                    this.cryptographySettings.getCipherType(),
                    this.cryptographySettings.getCipherMode(),
                    this.cryptographySettings.getCipherPadding()
            );
        } else {
            response.cryptographySettings = new CryptographySettings();
        }
        response.encapsulatedVersion = EncapsulatedPacket.ENCAPSULATED_VERSION;
        response.protocolName = this.endpoint.getProtocol().getName();
        response.protocolVersion = this.endpoint.getProtocol().getVersion();
        return response;
    }

    public HandshakeCompletePacket initServer(@NonNull HandshakeResponsePacket responsePacket) {
        if (this.cipherHelper != null) {
            throw new IllegalStateException("Handshake is already complete!");
        }

        if (this.cryptographySettings.doesEncrypt()) {
            this.cryptographySettings.setKey(responsePacket.cryptographySettings.getKey());
            this.initCrypto();
        }

        return new HandshakeCompletePacket();
    }

    private ECDHKeyPair initCrypto() {
        if (this.cryptographySettings.doesEncrypt()) {
            ECDHKeyPair localPair = ECDHKeyPair.getKey(this.cryptographySettings.getCurveType());
            PrivateKey privateKey = localPair.getPrivateKey();
            PublicKey publicKey = this.cryptographySettings.getKey().getPublicKey();

            AbstractSymmetricKey key = ECDHHelper.generateKey(
                    privateKey,
                    publicKey,
                    this.cryptographySettings.getCipherType()
            );
            this.cipherHelper = this.cryptographySettings.getCipherType().createHelper(
                    this.cryptographySettings.getCipherMode(),
                    this.cryptographySettings.getCipherPadding(),
                    key
            );
            return localPair;
        }

        return null;
    }

    public OutputStream encrypt(@NonNull OutputStream o, @NonNull ConnectionState state) {
        if (state.encrypt && this.cipherHelper != null) {
            o = this.cipherHelper.encryptionStream(o);
        }
        if (state.compress) {
            return this.compression.compressStream(o);
        } else {
            return o;
        }
    }

    public InputStream decrypt(@NonNull InputStream i, @NonNull ConnectionState state) {
        if (state.encrypt && this.cipherHelper != null) {
            i = this.cipherHelper.decryptionStream(i);
        }
        if (state.compress) {
            return this.compression.inflateStream(i);
        } else {
            return i;
        }
    }
}
