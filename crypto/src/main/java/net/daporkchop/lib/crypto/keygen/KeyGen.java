/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.crypto.keygen;

import lombok.NonNull;
import net.daporkchop.lib.crypto.BouncyCastleInit;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.hash.util.Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;

import static org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.EC;

public class KeyGen {
    static {
        BouncyCastleInit.loadClass();
    }

    /**
     * Generate a pseudorandom EC key pair using a given seed
     *
     * @param seed  The seed to use for random generation
     * @param curve The type of curve to generate
     * @return An instance of SecretKey for use by EC key exchange methods
     */
    public static EllipticCurveKeyPair gen(byte[] seed, CurveType curve) {
        EC ec = new EC();
        SecureRandom random = new SecureRandom(seed);
        try {
            ec.initialize(curve.spec, random);
            KeyPair pair = ec.genKeyPair();
            return new EllipticCurveKeyPair(curve, (BCECPrivateKey) pair.getPrivate(), (BCECPublicKey) pair.getPublic());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generate a random EC key pair, using 1024 bytes of random data as a seed
     *
     * @param curve The type of curve to generate
     * @return An instance of SecretKey for use by EC key exchange methods
     */
    public static EllipticCurveKeyPair gen(CurveType curve) {
        return gen(KeyRandom.getBytes(1024), curve);
    }

    /**
     * Generates a pseudorandom block cipher key using a given seed
     *
     * @param type the block cipher algorithm to generate for
     * @param seed the seed to use for random generation
     * @return a random CipherKey
     */
    public static CipherKey gen(@NonNull CipherType type, byte[] seed) {
        byte[] key = new byte[type.keySize];
        byte[] iv = new byte[type.ivSize];
        int i = 0;
        do {
            System.arraycopy(seed, 0, key, i, Math.min(seed.length, key.length - i));
            i += seed.length;
            seed = Digest.SHA3_256.hash(key, seed).getHash();
        } while (i < key.length);
        i = 0;
        do {
            System.arraycopy(seed, 0, iv, i, Math.min(seed.length, iv.length - i));
            i += seed.length;
            seed = Digest.SHA3_256.hash(iv, seed).getHash();
        } while (i < iv.length);
        return new CipherKey(new SecretKeySpec(key, "aaa"), iv);
    }

    /**
     * Generates a random block cipher key
     *
     * @param type the block cipher algorithm to generate for
     * @return a random CipherKey
     */
    public static CipherKey gen(@NonNull CipherType type) {
        return gen(type, KeyRandom.getBytes(1024));
    }

    public static CipherKey gen(@NonNull StreamCipherType type, byte[] seed) {
        byte[] key = new byte[type.keySize];
        byte[] iv = new byte[type.ivSize];
        int i = 0;
        do {
            System.arraycopy(seed, 0, key, i, Math.min(seed.length, key.length - i));
            i += seed.length;
            seed = Digest.SHA3_256.hash(key, seed).getHash();
        } while (i < key.length);
        i = 0;
        do {
            System.arraycopy(seed, 0, iv, i, Math.min(seed.length, iv.length - i));
            i += seed.length;
            seed = Digest.SHA3_256.hash(iv, seed).getHash();
        } while (i < iv.length);
        return new CipherKey(new SecretKeySpec(key, "aaa"), iv);
    }

    public static CipherKey gen(@NonNull StreamCipherType type) {
        return gen(type, KeyRandom.getBytes(1024));
    }
}
