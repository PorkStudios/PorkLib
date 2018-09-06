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

package net.daporkchop.lib.crypto.test.impl.ec.impl;

import net.daporkchop.lib.crypto.exchange.ECDHHelper;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.crypto.keygen.ec.ECDHKeyGen;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import org.junit.Test;

import java.util.Arrays;

public class ECDHTest {
    @Test
    public void test() {
        for (CurveType curve : CurveType.values()) {
            ECDHKeyPair pair1 = ECDHKeyGen.gen(curve);
            ECDHKeyPair pair2 = ECDHKeyGen.gen(curve);

            pair1 = ECDHKeyPair.fromFullEncoding(pair1.encodeFull());
            pair2 = ECDHKeyPair.fromFullEncoding(pair2.encodeFull());

            byte[] key1 = ECDHHelper.generateCommonSecret(
                    ECDHKeyPair.decodePrivate(pair1.encodePrivate()).getPrivateKey(),
                    ECDHKeyPair.decodePublic(pair2.encodePublic()).getPublicKey());
            byte[] key2 = ECDHHelper.generateCommonSecret(
                    ECDHKeyPair.decodePrivate(pair2.encodePrivate()).getPrivateKey(),
                    ECDHKeyPair.decodePublic(pair1.encodePublic()).getPublicKey());

            if (!Arrays.equals(key1, key2)) throw new IllegalStateException("Generated keys aren't equal!");
            System.out.println("ECDH key exchange successful for curve " + curve.name);
        }
    }
}
