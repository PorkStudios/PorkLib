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

package net.daporkchop.lib.crypto.keygen;

import lombok.NonNull;
import net.daporkchop.lib.crypto.BouncyCastleInit;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.hash.helper.sha.Sha256Helper;
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
            seed = Sha256Helper.sha256(key, seed);
        } while (i < key.length);
        i = 0;
        do {
            System.arraycopy(seed, 0, iv, i, Math.min(seed.length, iv.length - i));
            i += seed.length;
            seed = Sha256Helper.sha256(iv, seed);
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
            seed = Sha256Helper.sha256(key, seed);
        } while (i < key.length);
        i = 0;
        do {
            System.arraycopy(seed, 0, iv, i, Math.min(seed.length, iv.length - i));
            i += seed.length;
            seed = Sha256Helper.sha256(iv, seed);
        } while (i < iv.length);
        return new CipherKey(new SecretKeySpec(key, "aaa"), iv);
    }

    public static CipherKey gen(@NonNull StreamCipherType type) {
        return gen(type, KeyRandom.getBytes(1024));
    }
}
