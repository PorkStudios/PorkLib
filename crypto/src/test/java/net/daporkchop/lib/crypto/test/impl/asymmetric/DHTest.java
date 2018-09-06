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

package net.daporkchop.lib.crypto.test.impl.asymmetric;

import net.daporkchop.lib.crypto.exchange.DHHelper;
import net.daporkchop.lib.crypto.key.asymmetric.DHKeyPair;
import net.daporkchop.lib.crypto.keygen.asymmetric.DHKeyGen;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
public class DHTest {
    @Test
    public void test() {
        DHKeyPair pair1 = DHKeyGen.gen(512);
        pair1 = DHKeyPair.fromFullEncoding(pair1.encodeFull());
        DHKeyPair pair2 = DHKeyGen.gen(512);
        pair2 = DHKeyPair.fromFullEncoding(pair2.encodeFull());

        byte[] key1 = DHHelper.generateCommonSecret(pair1.getPrivateKey(), pair2.getPublicKey());
        byte[] key2 = DHHelper.generateCommonSecret(pair2.getPrivateKey(), pair1.getPublicKey());

        if (!Arrays.equals(key1, key2)) throw new IllegalStateException("Keys didn't match!");
        System.out.println("DH key exchange ");
    }
}
