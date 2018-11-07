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

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.keygen.KeyGen;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class EncryptionTest {
    @Test
    public void testBlockCipher() {
        byte[][] randomData = this.generateRandomBytes();
        for (CipherType type : CipherType.values()) {
            if (type == CipherType.NONE) {
                continue;
            }
            CipherKey key = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.values()) {
                for (CipherPadding padding : CipherPadding.values()) {
                    try {
                        Cipher cipher = Cipher.createBlock(type, mode, padding, key);
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
        byte[][] randomData = this.generateRandomBytes();
        for (StreamCipherType type : StreamCipherType.values()) {
            if (type == StreamCipherType.BLOCK_CIPHER) {
                continue;
            }
            try {
                CipherKey key = KeyGen.gen(type);
                Cipher cipher = Cipher.createStream(type, key);
                for (byte[] b : randomData) {
                    byte[] encrypted = cipher.encrypt(b);
                    byte[] decrypted = cipher.decrypt(encrypted);
                    if (!Arrays.equals(b, decrypted)) {
                        throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: %s", type.name));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error occurred while testing stream cipher (name=%s)", type.name), e);
            }
            System.out.printf("Successful test of stream cipher %s\n", type.name);
        }
    }

    @Test
    public void testPseudoStreamCipher() {
        byte[][] randomData = this.generateRandomBytes();
        for (CipherType type : CipherType.values()) {
            if (type == CipherType.NONE) {
                continue;
            }
            CipherKey key = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.streamableModes()) {
                Cipher cipher = Cipher.createPseudoStream(type, mode, key);
                try {
                    for (byte[] b : randomData) {
                        byte[] encrypted = cipher.encrypt(b);
                        byte[] decrypted = cipher.decrypt(encrypted);
                        decrypted = Arrays.copyOf(decrypted, b.length); //remove padding //TODO: do this automagically somehow
                        if (!Arrays.equals(b, decrypted)) {
                            throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: %s", type.name));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error occurred while testing pseudo-stream (block) cipher (%s)", cipher.toString()), e);
                }
            }
            System.out.printf("Successful test of pseudo-stream (block) cipher based on %s\n", type.name);
        }
    }

    //@Test
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
                    Cipher cipher1 = Cipher.createBlock(type, mode, padding, key1, CipherInitSide.SERVER);
                    Cipher cipher2 = Cipher.createBlock(type, mode, padding, key1, CipherInitSide.CLIENT);
                    Cipher cipher3 = Cipher.createBlock(type, mode, padding, key2, CipherInitSide.CLIENT);
                    Cipher cipher4 = Cipher.createBlock(type, mode, padding, key1, CipherInitSide.SERVER);

                    this.runInputOutputStreamTests(cipher1, cipher2, cipher3, cipher4, baos, cipher1.toString());
                }
            }

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }

    @Test
    public void testStreamCipherInputOutputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (StreamCipherType type : StreamCipherType.values()) {
            if (type == StreamCipherType.BLOCK_CIPHER) {
                continue;
            }
            CipherKey key1 = KeyGen.gen(type);
            CipherKey key2 = KeyGen.gen(type);

            Cipher cipher1 = Cipher.createStream(type, key1, CipherInitSide.SERVER);
            Cipher cipher2 = Cipher.createStream(type, key1, CipherInitSide.CLIENT);
            Cipher cipher3 = Cipher.createStream(type, key2, CipherInitSide.CLIENT);
            Cipher cipher4 = Cipher.createStream(type, key1, CipherInitSide.SERVER);

            this.runInputOutputStreamTests(cipher1, cipher2, cipher3, cipher4, baos, cipher1.toString());

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }

    @Test
    public void testPseudoStreamCipherInputOutputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (CipherType type : CipherType.values()) {
            if (type == CipherType.NONE) {
                continue;
            }
            CipherKey key1 = KeyGen.gen(type);
            CipherKey key2 = KeyGen.gen(type);

            for (CipherMode mode : CipherMode.streamableModes()) {
                Cipher cipher1 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.SERVER);
                Cipher cipher2 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.CLIENT);
                Cipher cipher3 = Cipher.createPseudoStream(type, mode, key2, CipherInitSide.CLIENT);
                Cipher cipher4 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.SERVER);

                this.runInputOutputStreamTests(cipher1, cipher2, cipher3, cipher4, baos, cipher1.toString());
            }

            System.out.printf("Completed test on %s successfully\n", type.name);
        }
    }

    private void runInputOutputStreamTests(@NonNull Cipher cipher1, @NonNull Cipher cipher2, @NonNull Cipher cipher3, @NonNull Cipher cipher4, @NonNull ByteArrayOutputStream baos, @NonNull String cipherName) throws IOException {
        for (byte[] b : this.generateRandomBytes()) {
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
                    for (int i = 0; i < decrypted.length; i++) {
                        decrypted[i] = (byte) is.read();
                    }
                    is.close();
                    if (!Arrays.equals(b, decrypted)) {
                        throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: %s", cipherName));
                    }
                }
                for (Cipher cipher : new Cipher[]{cipher3, cipher4}) {
                    try {
                        InputStream is = cipher.decryptionStream(new ByteArrayInputStream(encrypted1));
                        decrypted = new byte[is.read() | (is.read() << 8)];
                        for (int i = 0; i < decrypted.length; i++) {
                            decrypted[i] = (byte) is.read();
                        }
                        is.close();
                        if (!Arrays.equals(b, decrypted)) {
                            throw new RuntimeException(String.format("Decrypted data isn't the same! Cipher: %s", cipherName));
                        }

                        //if we've gotten this far, something is seriously wrong
                        throw new AssertionError(String.format("Decryption using alt cipher completed successfully! Cipher: %s", cipherName));
                    } catch (Exception e) {
                        //this should throw an exception, or something is wrong
                    }
                }
            }
        }
    }

    public static byte[][] generateRandomBytes()  {
        byte[][] randomData = new byte[32][];
        for (int i = randomData.length - 1; i >= 0; i--)  {
            byte[] b = new byte[ThreadLocalRandom.current().nextInt(1024, 8192)];
            ThreadLocalRandom.current().nextBytes(b);
            randomData[i] = b;
        }
        return randomData;
    }
}
