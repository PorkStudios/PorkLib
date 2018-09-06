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
import net.daporkchop.lib.crypto.key.symmetric.impl.RC2Key;
import net.daporkchop.lib.crypto.keygen.KeyRandom;

import javax.crypto.spec.SecretKeySpec;
import java.util.concurrent.ThreadLocalRandom;

public class RC2KeyGen {
    static {
        BouncyCastleInit.loadClass();
    }

    public static RC2Key gen(byte[] data) {
        byte[] b = new byte[BlockCipherType.RC2.blockSize];
        int i = 0;
        do {
            System.arraycopy(data, 0, b, i, Math.min(data.length, b.length - i));
            i += data.length;
        } while (i < data.length);
        data = b;
        return new RC2Key(new SecretKeySpec(data, "aaa"), KeyRandom.getBytes(BlockCipherType.RC2.blockSize));
    }

    public static RC2Key gen() {
        byte[] key = new byte[BlockCipherType.RC2.blockSize];
        ThreadLocalRandom.current().nextBytes(key);
        return gen(key);
    }
}
