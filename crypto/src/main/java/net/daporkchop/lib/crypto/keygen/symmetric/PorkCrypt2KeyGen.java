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

package net.daporkchop.lib.crypto.keygen.symmetric;

import net.daporkchop.lib.crypto.BouncyCastleInit;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.key.symmetric.impl.PorkCrypt2Key;
import net.daporkchop.lib.crypto.keygen.KeyRandom;
import net.daporkchop.lib.hash.helper.sha.Sha160Helper;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ThreadLocalRandom;

public class PorkCrypt2KeyGen {
    static {
        BouncyCastleInit.loadClass();
    }

    public static PorkCrypt2Key gen(byte[] data) {
        return gen(data, 16);
    }

    public static PorkCrypt2Key gen() {
        byte[] seed = new byte[BlockCipherType.PORKCRYPT2.blockSize];
        ThreadLocalRandom.current().nextBytes(seed);
        return gen(seed, 16);
    }

    /**
     * Generates a PorkCrypt2 key
     *
     * @param seed     the seed to use
     * @param strength the number of rounds to use for encryption/decryption
     * @return a PorkCrypt2 key with the specified strength
     */
    public static PorkCrypt2Key gen(byte[] seed, int strength) {
        return new PorkCrypt2Key(new SecretKeySpec(rand(seed, strength << 4), "aaa"), KeyRandom.getBytes(BlockCipherType.PORKCRYPT2.blockSize));
    }

    private static byte[] rand(byte[] seed, int n) {
        byte[] data = null;
        ByteArrayOutputStream ret = new ByteArrayOutputStream(n);
        while (ret.size() < n) {
            data = Sha160Helper.sha160(seed, data == null ? new byte[0] : data);
            ret.write(data, 0, Math.min(n - ret.size(), data.length));
        }
        return ret.toByteArray();
    }
}
