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

import net.daporkchop.lib.crypto.cipher.impl.asymmetric.RSACipherHelper;
import net.daporkchop.lib.crypto.key.asymmetric.RSAKeyPair;
import net.daporkchop.lib.crypto.key.serializer.impl.RSAKeySerializer;
import net.daporkchop.lib.crypto.keygen.asymmetric.RSAKeyGen;
import net.daporkchop.lib.crypto.sig.HashTypes;
import net.daporkchop.lib.crypto.sig.asymmetric.impl.RSASignatureHelper;
import net.daporkchop.lib.crypto.test.TestMain;
import org.junit.Test;

import java.util.Arrays;

public class RSATest {
    @Test
    public void testCipher() {
        RSAKeyPair pair = RSAKeyGen.gen(4096);
        byte[] encoded = RSAKeySerializer.INSTANCE.serialize(pair);
        pair = RSAKeySerializer.INSTANCE.<RSAKeyPair>deserialize(encoded);

        CIPHER:
        {
            RSACipherHelper helper = new RSACipherHelper();
            for (byte[] b : TestMain.RANDOM_DATA) {
                byte[] encrypted = helper.encrypt(b, pair);
                byte[] decrypted = helper.decrypt(encrypted, pair);
                if (!Arrays.equals(decrypted, b)) {
                    throw new IllegalStateException("Decrypted data didn't match!");
                }
            }
            System.out.println("Successful test of RSA cipher!");
        }
    }

    @Test
    public void testSig() {
        RSAKeyPair pair = RSAKeyGen.gen(4096);
        byte[] encoded = RSAKeySerializer.INSTANCE.serialize(pair);
        pair = RSAKeySerializer.INSTANCE.<RSAKeyPair>deserialize(encoded);

        SIG:
        {
            for (HashTypes hash : HashTypes.values()) {
                RSASignatureHelper helper = new RSASignatureHelper(hash);

                for (byte[] b : TestMain.RANDOM_DATA) {
                    byte[] sig = helper.sign(b, pair);
                    if (!helper.verify(sig, b, pair)) {
                        throw new IllegalStateException("Invalid signature!");
                    }
                }
                System.out.println("Signed using " + hash.name());
            }
            System.out.println("Successful test of RSA signature!");
        }
    }
}
