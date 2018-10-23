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

package net.daporkchop.lib.crypto.cipher;

import lombok.NonNull;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.hash.helper.sha.Sha256Helper;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Cipher {
    static Cipher create(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key) {
        return create(type, mode, padding, key, b -> {
            byte[] hash = Sha256Helper.sha256(b);
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
        });
    }

    static Cipher create(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull Consumer<byte[]> ivUpdater) {
        return new BlockCipherImpl(type, mode, padding, key, ivUpdater);
    }

    static Cipher create(@NonNull StreamCipherType type, @NonNull CipherKey key)    {
        return new StreamCipherImpl(type, key);
    }

    byte[] encrypt(@NonNull byte[] plaintext);

    byte[] decrypt(@NonNull byte[] ciphertext);

    OutputStream encryptionStream(@NonNull OutputStream outputStream);

    InputStream decryptionStream(@NonNull InputStream inputStream);
}

class BlockCipherImpl implements Cipher {
    private final CipherType type;
    private final CipherMode mode;
    private final CipherPadding padding;

    private final ThreadLocal<SoftReference<BufferedBlockCipher>> tl;
    private final CipherKey key;
    private final Consumer<byte[]> ivUpdater;
    private final Supplier<BufferedBlockCipher> supplier;
    private final CipherKey encrypt, decrypt;

    public BlockCipherImpl(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull Consumer<byte[]> ivUpdater) {
        this.type = type;
        this.mode = mode;
        this.padding = padding;

        this.ivUpdater = ivUpdater;
        this.key = key;
        this.supplier = () -> {
            BlockCipher cipher = type.create();
            cipher = mode.wrap(cipher);
            return new PaddedBufferedBlockCipher(cipher, padding.create());
        };
        this.tl = ThreadLocal.withInitial(() -> new SoftReference<>(this.supplier.get()));

        this.encrypt = new CipherKey(new SecretKeySpec(key.getKey(), "aaa"), key.getIV());
        this.decrypt = new CipherKey(new SecretKeySpec(key.getKey(), "aaa"), key.getIV());
    }

    private static void doFinal(BufferedBlockCipher c, int tam, byte[] b) {
        try {
            c.doFinal(b, tam);
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.encrypt) {
            //update key
            this.ivUpdater.accept(this.encrypt.getIV());
            cipher.init(true, this.mode.getParametersFromKey(this.encrypt));
        }

        //actually do the encryption
        byte[] encrypted = new byte[cipher.getOutputSize(plaintext.length)];
        int tam = cipher.processBytes(plaintext, 0, plaintext.length, encrypted, 0);
        doFinal(cipher, tam, encrypted);
        return encrypted;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.decrypt) {
            //update key
            this.ivUpdater.accept(this.decrypt.getIV());
            cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
        }

        byte[] decrypted = new byte[cipher.getOutputSize(ciphertext.length)];
        int tam = cipher.processBytes(ciphertext, 0, ciphertext.length, decrypted, 0);
        doFinal(cipher, tam, decrypted);
        return decrypted;
    }

    @Override
    public OutputStream encryptionStream(OutputStream outputStream) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.encrypt) {
            //update key
            this.ivUpdater.accept(this.encrypt.getIV());
            cipher.init(true, this.mode.getParametersFromKey(this.encrypt));
        }

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public InputStream decryptionStream(InputStream inputStream) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.decrypt) {
            //update key
            this.ivUpdater.accept(this.decrypt.getIV());
            cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
        }

        return new CipherInputStream(inputStream, cipher);
    }

    private BufferedBlockCipher get() {
        BufferedBlockCipher cipher;
        if ((cipher = this.tl.get().get()) == null) {
            cipher = this.supplier.get();
            this.tl.set(new SoftReference<>(cipher));
        }
        return cipher;
    }

    @Override
    public String toString() {
        return String.format("Cipher$BlockCipherImpl(type=%s, mode=%s, padding=%s)", this.type.name, this.mode.name, this.padding.name);
    }
}

class StreamCipherImpl implements Cipher {
    //TODO
    private final StreamCipherType type;

    private final StreamCipher outCipher;
    private final StreamCipher inCipher;

    public StreamCipherImpl(@NonNull StreamCipherType type, @NonNull CipherKey key) {
        this.type = type;

        this.outCipher = type.create();
        this.inCipher = type.create();

        this.outCipher.init(true, new CipherKey(key.getKey(), key.getIV()));
        this.inCipher.init(false, new CipherKey(key.getKey(), key.getIV()));
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        synchronized (this.outCipher) {
            return new byte[0];
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        synchronized (this.inCipher) {
            return new byte[0];
        }
    }

    @Override
    public OutputStream encryptionStream(OutputStream outputStream) {
        synchronized (this.outCipher) {
            return null;
        }
    }

    @Override
    public InputStream decryptionStream(InputStream inputStream) {
        synchronized (this.inCipher) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("Cipher$StreamCipherImpl(type=%s)", this.type.name);
    }
}
