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

package net.daporkchop.lib.crypto.keygen.ec;

import net.daporkchop.lib.crypto.BouncyCastleInit;
import net.daporkchop.lib.crypto.key.ec.impl.ECDSAKeyPair;
import net.daporkchop.lib.crypto.keygen.KeyRandom;
import net.daporkchop.lib.crypto.sig.ec.CurveType;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;

import static org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.ECDSA;

public class ECDSAKeyGen {
    static {
        BouncyCastleInit.loadClass();
    }

    /**
     * Generate a random ECDSA key pair using a given seed
     *
     * @param seed  The seed to use for random generation
     * @param curve The type of curve to generate
     * @return An instance of SecretKey for use by ECDSA signing/verification methods
     */
    public static ECDSAKeyPair gen(byte[] seed, CurveType curve) {
        ECDSA ecdsa = new ECDSA();
        SecureRandom random = new SecureRandom(seed);
        try {
            ecdsa.initialize(curve.spec, random);
            KeyPair pair = ecdsa.genKeyPair();
            return new ECDSAKeyPair(pair.getPrivate(), pair.getPublic());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generate a random ECDSA key pair, using 1024 bytes of random data as a seed
     *
     * @param curve The type of curve to generate
     * @return An instance of SecretKey for use by ECDSA signing/verification methods
     */
    public static ECDSAKeyPair gen(CurveType curve) {
        return gen(KeyRandom.getBytes(1024), curve);
    }
}
