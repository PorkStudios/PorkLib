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

import net.daporkchop.lib.crypto.cipher.*;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.keygen.KeyGen;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static net.daporkchop.lib.crypto.TestConstants.randomData;

public class EncryptionTest {
    @Test
    public void testBlockCipher() {
        for (CipherType type : CipherType.values()) {
            if (type == CipherType.NONE) {
                continue;
            }
            CipherKey key = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.values()) {
                for (CipherPadding padding : CipherPadding.values()) {
                    try {
                        Cipher cipher = Cipher.create(type, mode, padding, key);
                        for (byte[] b : randomData) {
                            byte[] encrypted = cipher.encrypt(b);
                            byte[] decrypted = cipher.decrypt(encrypted);
                            decrypted = Arrays.copyOf(decrypted, b.length); //remove padding //TODO: do this automagically somehow
                            if (!Arrays.equals(b, decrypted)) {
                                throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: (type=%s, mode=%s, padding= %s)", type.name, mode.name, padding.name));
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error occurred while testing cipher (type=%s, mode=%s, padding= %s)", type.name, mode.name, padding.name), e);
                    }
                }
            }

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }

    @Test
    public void testStreamCipher() {
        for (StreamCipherType type : StreamCipherType.values()) {
            try {
                CipherKey key = KeyGen.gen(type);
                Cipher cipher = Cipher.create(type, key);
                for (byte[] b : randomData) {
                    byte[] encrypted = cipher.encrypt(b);
                    byte[] decrypted = cipher.decrypt(encrypted);
                    if (!Arrays.equals(b, decrypted))   {
                        throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: %s", type.name));
                    }
                }
            } catch (Exception e)   {
                throw new RuntimeException(String.format("Error occurred while testing stream cipher (name=%s)", type.name), e);
            }
            System.out.printf("Successful test of stream cipher %s\n", type.name);
        }
    }

    @Test
    public void testBlockCipherInputOutputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (CipherType type : CipherType.values()) {
            if (type == CipherType.NONE) {
                continue;
            }
            CipherKey key1 = KeyGen.gen(type);
            CipherKey key2 = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.values()) {
                for (CipherPadding padding : CipherPadding.values()) {
                    Cipher cipher1 = Cipher.create(type, mode, padding, key1, CipherInitSide.SERVER);
                    Cipher cipher2 = Cipher.create(type, mode, padding, key1, CipherInitSide.CLIENT);
                    Cipher cipher3 = Cipher.create(type, mode, padding, key2, CipherInitSide.CLIENT);
                    Cipher cipher4 = Cipher.create(type, mode, padding, key1, CipherInitSide.SERVER);

                    for (byte[] b : randomData) {
                        baos.reset();
                        {
                            byte[] encrypted1;
                            {
                                OutputStream os = cipher1.encryptionStream(baos);
                                os.write(b.length & 0xFF);
                                os.write((b.length >> 8) & 0xFF);
                                os.write(b);
                                os.close();
                                encrypted1 = baos.toByteArray();
                            }
                            byte[] decrypted;
                            {
                                InputStream is = cipher2.decryptionStream(new ByteArrayInputStream(encrypted1));
                                decrypted = new byte[is.read() | (is.read() << 8)];
                                for (int i = 0; i < decrypted.length; i++)  {
                                    decrypted[i] = (byte) is.read();
                                }
                                is.close();
                                if (!Arrays.equals(b, decrypted))   {
                                    throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: (type=%s, mode=%s, padding= %s)", type.name, mode.name, padding.name));
                                }
                            }
                            int j = 0;
                            for (Cipher cipher : new Cipher[] {cipher3, cipher4}){
                                try {
                                    InputStream is = cipher.decryptionStream(new ByteArrayInputStream(encrypted1));
                                    decrypted = new byte[is.read() | (is.read() << 8)];
                                    for (int i = 0; i < decrypted.length; i++) {
                                        decrypted[i] = (byte) is.read();
                                    }
                                    is.close();
                                    if (!Arrays.equals(b, decrypted)) {
                                        throw new RuntimeException(String.format("Decrypted data isn't the same! Cipher: (type=%s, mode=%s, padding= %s)", type.name, mode.name, padding.name));
                                    }

                                    //if we've gotten this far, something is seriously wrong
                                    throw new AssertionError(String.format("Decryption using alt cipher completed successfully! Cipher: (type=%s, mode=%s, padding= %s)", type.name, mode.name, padding.name));
                                } catch (Exception e)   {
                                    //this should throw an exception, or something is wrong
                                } finally {
                                    j++;
                                }
                            }
                        }
                    }
                }
            }

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }
}
