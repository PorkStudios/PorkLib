/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.misc.TestRandomData;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.seekable.SeekableBlockCipher;
import net.daporkchop.lib.crypto.cipher.seekable.SeekableCipher;
import net.daporkchop.lib.crypto.cipher.seekable.SeekableStreamCipher;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.crypto.keygen.KeyGen;
import net.daporkchop.lib.crypto.keygen.KeyRandom;
import net.daporkchop.lib.logging.Logging;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EncryptionTest implements Logging {
    private static boolean isInvalid(@NonNull byte[] original, @NonNull byte[] decrypted) {
        if (decrypted.length < original.length) {
            return true;
        } else {
            for (int i = original.length - 1; i >= 0; i--) {
                if (original[i] != decrypted[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    @Test
    public void testBlockCipher() {
        Arrays.stream(CipherType.values()).parallel()
                .filter(type -> type != CipherType.NONE)
                .forEach(type -> {
                    CipherKey key = KeyGen.gen(type);

                    Arrays.stream(CipherMode.values()).parallel()
                            .forEach(mode -> Arrays.stream(CipherPadding.values()).parallel()
                                    .forEach(padding -> {
                                        try {
                                            Cipher cipher = Cipher.createBlock(type, mode, padding, key);
                                            for (byte[] b : TestRandomData.randomBytes) {
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
                                    }));

                    System.out.printf("Completed test on %s successfully\n", type.name);
                });
    }

    @Test
    public void testStreamCipher() {
        Arrays.stream(StreamCipherType.values()).parallel()
                .filter(type -> type != StreamCipherType.BLOCK_CIPHER)
                .forEach(type -> {
                    CipherKey key = KeyGen.gen(type);
                    Cipher cipher = Cipher.createStream(type, key);
                    for (byte[] b : TestRandomData.randomBytes) {
                        byte[] encrypted = cipher.encrypt(b);
                        byte[] decrypted = cipher.decrypt(encrypted);
                        if (!Arrays.equals(b, decrypted)) {
                            throw new AssertionError(String.format("Decrypted data isn't the same! Cipher: %s", type.name));
                        }
                    }
                    System.out.printf("Successful test of stream cipher %s\n", type.name);
                });
    }

    @Test
    public void testPseudoStreamCipher() {
        Arrays.stream(CipherType.values()).parallel()
                .filter(type -> type != CipherType.NONE)
                .forEach(type -> {
                    CipherKey key = KeyGen.gen(type);

                    Arrays.stream(CipherMode.streamableModes()).parallel()
                            .forEach(mode -> {
                                Cipher cipher = Cipher.createPseudoStream(type, mode, key);
                                try {
                                    for (byte[] b : TestRandomData.randomBytes) {
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
                            });

                    System.out.printf("Successful test of pseudo-stream (block) cipher based on %s\n", type.name);
                });
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
    public void testStreamCipherInputOutputStream() {
        Arrays.stream(StreamCipherType.values()).parallel()
                .filter(type -> type != StreamCipherType.BLOCK_CIPHER)
                .forEach((IOConsumer<StreamCipherType>) type -> {
                    CipherKey key1 = KeyGen.gen(type);
                    CipherKey key2 = KeyGen.gen(type);

                    Cipher cipher1 = Cipher.createStream(type, key1, CipherInitSide.SERVER);
                    Cipher cipher2 = Cipher.createStream(type, key1, CipherInitSide.CLIENT);
                    Cipher cipher3 = Cipher.createStream(type, key2, CipherInitSide.CLIENT);
                    Cipher cipher4 = Cipher.createStream(type, key1, CipherInitSide.SERVER);

                    this.runInputOutputStreamTests(cipher1, cipher2, cipher3, cipher4, new ByteArrayOutputStream(), cipher1.toString());

                    System.out.printf("Completed test on %s successfully\n", type.name);
                });
    }

    @Test
    public void testPseudoStreamCipherInputOutputStream() {
        Arrays.stream(CipherType.values()).parallel()
                .filter(type -> type != CipherType.NONE)
                .forEach(type -> {
                    CipherKey key1 = KeyGen.gen(type);
                    CipherKey key2 = KeyGen.gen(type);

                    Arrays.stream(CipherMode.streamableModes()).parallel()
                            .forEach((IOConsumer<CipherMode>) mode -> {
                                Cipher cipher1 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.SERVER);
                                Cipher cipher2 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.CLIENT);
                                Cipher cipher3 = Cipher.createPseudoStream(type, mode, key2, CipherInitSide.CLIENT);
                                Cipher cipher4 = Cipher.createPseudoStream(type, mode, key1, CipherInitSide.SERVER);

                                this.runInputOutputStreamTests(cipher1, cipher2, cipher3, cipher4, new ByteArrayOutputStream(), cipher1.toString());
                            });

                    System.out.printf("Completed test on %s successfully\n", type.name);
                });
    }

    private void runInputOutputStreamTests(@NonNull Cipher cipher1, @NonNull Cipher cipher2, @NonNull Cipher cipher3, @NonNull Cipher cipher4, @NonNull ByteArrayOutputStream baos, @NonNull String cipherName) throws IOException {
        for (byte[] b : TestRandomData.randomBytes) {
            baos.reset();
            {
                byte[] encrypted1;
                {
                    OutputStream os = cipher1.encrypt(baos);
                    os.write(b.length & 0xFF);
                    os.write((b.length >> 8) & 0xFF);
                    os.write(b);
                    os.close();
                    encrypted1 = baos.toByteArray();
                }
                byte[] decrypted;
                {
                    InputStream is = cipher2.decrypt(new ByteArrayInputStream(encrypted1));
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
                        InputStream is = cipher.decrypt(new ByteArrayInputStream(encrypted1));
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

    @Test
    public void testSeekableStream() throws IOException {
        logger.info("Testing seekable stream ciphers...");
        Arrays.stream(StreamCipherType.values())
                .filter(t -> t != StreamCipherType.BLOCK_CIPHER)
                .parallel()
                .forEach(type -> {
                    logger.info("  Testing %s...", type);
                    this.testSeekable(
                            seed -> KeyGen.gen(type, seed),
                            (key, side) -> new SeekableStreamCipher(type::create, key, side)
                    );
                });
    }

    @Test
    public void testSeekableBlock() throws IOException {
        logger.info("Testing seekable block ciphers...");
        Arrays.stream(CipherType.values())
                .filter(t -> t != CipherType.NONE)
                .parallel().forEach(type -> Arrays.stream(CipherPadding.values()).parallel()
                .forEach(padding -> {
                    logger.info("  Testing %s with %s padding...", type, padding);
                    this.testSeekable(
                            seed -> KeyGen.gen(type, seed),
                            (key, side) -> new SeekableBlockCipher(type, padding, key, side)
                    );
                }));
    }

    private void testSeekable(@NonNull Function<byte[], CipherKey> keyGenerator, @NonNull BiFunction<CipherKey, CipherInitSide, SeekableCipher> cipherSupplier) {
        byte[] seed = KeyRandom.getBytes(1024);
        CipherKey key1 = keyGenerator.apply(seed);
        CipherKey key2 = keyGenerator.apply(seed);
        SeekableCipher cipher1 = cipherSupplier.apply(key1, CipherInitSide.SERVER);
        SeekableCipher cipher2 = cipherSupplier.apply(key2, CipherInitSide.CLIENT);
        Arrays.stream(TestRandomData.randomBytes).forEachOrdered(b -> {
            long offset = ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE >>> 1L);
            byte[] encrypted = cipher1.encrypt(b, offset);
            byte[] decrypted = cipher2.decrypt(encrypted, offset);
            if (isInvalid(b, decrypted)) {
                throw new IllegalStateException("Decrypted data isn't the same!");
            }
        });
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Arrays.stream(TestRandomData.randomBytes).forEachOrdered(b -> {
            baos.reset();
            try {
                long offset = ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE >>> 1L);
                try (OutputStream out = cipher1.encrypt(baos, offset, b.length)) {
                    out.write(b);
                }
                byte[] encrypted = baos.toByteArray();
                byte[] decrypted;
                try (InputStream in = cipher2.decrypt(new ByteArrayInputStream(encrypted), offset, b.length)) {
                    decrypted = IOUtils.readFully(in, -1, false);
                }
                if (isInvalid(b, decrypted)) {
                    throw new IllegalStateException("Decrypted data isn't the same!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
