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

package net.daporkchop.lib.crypto.keygen.asymmetric;

import net.daporkchop.lib.crypto.key.asymmetric.DHKeyPair;
import net.daporkchop.lib.crypto.keygen.KeyRandom;
import org.bouncycastle.jcajce.provider.asymmetric.dh.KeyPairGeneratorSpi;

import java.security.KeyPair;
import java.security.SecureRandom;

public class DHKeyGen {
    /**
     * Generates a new random Diffie-Hellman key pair, using the given key size
     *
     * @param keySize The size of the key to generate (in bytes)
     * @param seed    The seed to use for random key generation
     * @return An instance of DHKeyPair for use with Diffie-Hellman key exchange methods
     */
    public static DHKeyPair gen(int keySize, byte[] seed) {
        SecureRandom random = new SecureRandom(seed);
        KeyPairGeneratorSpi generator = new KeyPairGeneratorSpi();
        generator.initialize(keySize, random);
        KeyPair pair = generator.generateKeyPair();
        return new DHKeyPair(pair.getPrivate(), pair.getPublic());
    }

    /**
     * Generates a new random Diffie-Hellman key pair, using the given key size and 1024 random bytes as a seed
     *
     * @param keySize The size of the key to generate (in bytes)
     * @return An instance of RSAKeyPair for use with Diffie-Hellman key exchange methods
     */
    public static DHKeyPair gen(int keySize) {
        return gen(keySize, KeyRandom.getBytes(1024));
    }
}
