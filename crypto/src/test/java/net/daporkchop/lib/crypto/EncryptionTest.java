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

package net.daporkchop.lib.crypto;

import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherMode;
import net.daporkchop.lib.crypto.cipher.CipherPadding;
import net.daporkchop.lib.crypto.cipher.CipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.keygen.KeyGen;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class EncryptionTest {
    @Test
    public void testBlockCipher()   {
        byte[][] dataSets = new byte[32][];
        for (int i = dataSets.length - 1; i >= 0; i--)  {
            byte[] b = new byte[ThreadLocalRandom.current().nextInt(1024, 8192)];
            ThreadLocalRandom.current().nextBytes(b);
            dataSets[i] = b;
        }

        for (CipherType type : CipherType.values()) {
            CipherKey key = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.values()) {
                for (CipherPadding padding : CipherPadding.values())    {
                    Cipher cipher = Cipher.create(type, mode, padding, key);
                    for (byte[] b : dataSets)   {
                        byte[] encrypted = cipher.encrypt(b);
                        byte[] decrypted = cipher.decrypt(encrypted);
                        decrypted = Arrays.copyOf(decrypted, b.length); //remove padding
                        //TODO: do this automagically somehow
                        if (!Arrays.equals(b, decrypted))   {
                            throw new IllegalStateException(String.format("Decrypted data isn't the same! Cipher: %s", cipher));
                        }
                    }
                }
            }

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }
}
