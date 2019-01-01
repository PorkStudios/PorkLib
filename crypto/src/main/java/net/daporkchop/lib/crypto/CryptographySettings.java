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

package net.daporkchop.lib.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.exchange.ECDHHelper;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.key.KeySerialization;
import net.daporkchop.lib.crypto.keygen.KeyGen;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.crypto.sig.ec.EllipticCurveKeyCache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
public class CryptographySettings implements Data {
    private EllipticCurveKeyPair keyPair;
    private CipherType cipherType;
    private CipherMode cipherMode;
    private CipherPadding cipherPadding;
    private StreamCipherType streamCipherType;

    public CryptographySettings(@NonNull EllipticCurveKeyPair keyPair, @NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding) {
        this.keyPair = keyPair;
        this.cipherType = type;
        this.cipherMode = mode;
        this.cipherPadding = padding;
    }

    public CryptographySettings(@NonNull EllipticCurveKeyPair keyPair, @NonNull CipherType type, @NonNull CipherMode mode) {
        this.keyPair = keyPair;
        this.cipherType = type;
        this.cipherMode = mode;
        this.streamCipherType = StreamCipherType.BLOCK_CIPHER;
    }

    public CryptographySettings(@NonNull EllipticCurveKeyPair keyPair, @NonNull StreamCipherType type) {
        this.keyPair = keyPair;
        this.streamCipherType = type;
    }

    public CryptographySettings(@NonNull CurveType curveType, @NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding) {
        this(EllipticCurveKeyCache.getKeyPair(curveType), type, mode, padding);
    }

    public CryptographySettings(@NonNull CurveType curveType, @NonNull CipherType type, @NonNull CipherMode mode) {
        this(EllipticCurveKeyCache.getKeyPair(curveType), type, mode);
    }

    public CryptographySettings(@NonNull CurveType curveType, @NonNull StreamCipherType type) {
        this(EllipticCurveKeyCache.getKeyPair(curveType), type);
    }

    public CryptographySettings(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding) {
        this(CurveType.brainpoolp256r1, type, mode, padding);
    }

    public CryptographySettings(@NonNull CipherType type, @NonNull CipherMode mode) {
        this(CurveType.brainpoolp256r1, type, mode);
    }

    public CryptographySettings(@NonNull StreamCipherType type) {
        this(CurveType.brainpoolp256r1, type);
    }

    public CryptographySettings(@NonNull EllipticCurveKeyPair keyPair, @NonNull CryptographySettings cryptographySettings) {
        this.keyPair = keyPair;

        this.cipherType = cryptographySettings.cipherType;
        this.cipherMode = cryptographySettings.cipherMode;
        this.cipherPadding = cryptographySettings.cipherPadding;
        this.streamCipherType = cryptographySettings.streamCipherType;
    }

    public CryptographySettings(@NonNull DataIn in) throws IOException {
        this.read(in);
    }

    /**
     * @see #random(Random)
     */
    public static CryptographySettings random() {
        return random(ThreadLocalRandom.current());
    }

    /**
     * Creates a new instance of {@link CryptographySettings} with randomly chosen settings.
     * <p>
     * This doesn't really have many practical applications aside from unit tests.
     *
     * @param random an instance of {@link Random} for choosing random numbers
     * @return an instance of {@link CryptographySettings} with randomly chosen settings
     */
    public static CryptographySettings random(@NonNull Random random) {
        CurveType curveType = CurveType.values()[random.nextInt(CurveType.values().length)];
        switch (random.nextInt(3)) {
            case 0: //block cipher
                return new CryptographySettings(
                        curveType,
                        CipherType.values()[random.nextInt(CipherType.values().length)],
                        CipherMode.values()[random.nextInt(CipherMode.values().length)],
                        CipherPadding.values()[random.nextInt(CipherPadding.values().length)]
                );
            case 1: //stream cipher
                return new CryptographySettings(
                        curveType,
                        StreamCipherType.values()[random.nextInt(StreamCipherType.values().length)]
                );
            case 2: //pseudo-stream cipher
                return new CryptographySettings(
                        curveType,
                        CipherType.values()[random.nextInt(CipherType.values().length)],
                        CipherMode.streamableModes()[random.nextInt(CipherMode.streamableModes().length)]
                );
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void read(DataIn in) throws IOException {
        this.keyPair = in.readBoolean() ? KeySerialization.decodeEC(in, true, false) : null;
        if (this.keyPair != null) {
            this.cipherType = in.readEnum(CipherType::valueOf);
            this.cipherMode = in.readEnum(CipherMode::valueOf);
            this.cipherPadding = in.readEnum(CipherPadding::valueOf);
            this.streamCipherType = in.readEnum(StreamCipherType::valueOf);
        }
    }

    @Override
    public void write(DataOut out) throws IOException {
        if (this.keyPair == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            KeySerialization.encodeEC(out, this.keyPair, true, false);
            out.writeEnum(this.cipherType);
            out.writeEnum(this.cipherMode);
            out.writeEnum(this.cipherPadding);
            out.writeEnum(this.streamCipherType);
        }
    }

    public Cipher getCipher(@NonNull EllipticCurveKeyPair localPair, @NonNull CipherInitSide side) {
        if (this.keyPair == null) {
            return null;
        }
        byte[] commonSecret = ECDHHelper.generateCommonSecret(localPair.getPrivateKey(), this.keyPair.getPublicKey());
        if (this.streamCipherType == null) {
            return Cipher.createBlock(this.cipherType, this.cipherMode, this.cipherPadding, KeyGen.gen(this.cipherType, commonSecret), side);
        } else if (this.streamCipherType == StreamCipherType.BLOCK_CIPHER) {
            return Cipher.createPseudoStream(this.cipherType, this.cipherMode, KeyGen.gen(this.cipherType, commonSecret), side);
        } else {
            return Cipher.createStream(this.streamCipherType, KeyGen.gen(this.streamCipherType, commonSecret), side);
        }
    }
}
