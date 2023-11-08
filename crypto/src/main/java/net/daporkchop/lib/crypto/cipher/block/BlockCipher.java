/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.crypto.cipher.block;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.key.CipherKey;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.function.Supplier;

/**
 * A {@link Cipher} used for block ciphers.
 * <p>
 * This implementation currently updates the IV with every message encrypted, I should probably make
 * that configurable
 * //TODO: the above
 *
 * @author DaPorkchop_
 */
public class BlockCipher implements Cipher {
    private static void doFinal(BufferedBlockCipher c, int tam, byte[] b) {
        try {
            c.doFinal(b, tam);
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
    }

    private final CipherType type;
    private final CipherMode mode;
    private final CipherPadding padding;
    private final ThreadLocal<SoftReference<BufferedBlockCipher>> tl;
    private final IVUpdater ivUpdater;
    private final Supplier<BufferedBlockCipher> supplier;
    private final CipherKey encrypt, decrypt;

    public BlockCipher(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull IVUpdater ivUpdater) {
        this(() -> {
            org.bouncycastle.crypto.BlockCipher cipher = type.create();
            cipher = mode.wrap(cipher);
            return new PaddedBufferedBlockCipher(cipher, padding.create());
        }, type, mode, padding, key, side, ivUpdater);
    }

    public BlockCipher(@NonNull Supplier<BufferedBlockCipher> supplier, @NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull IVUpdater ivUpdater) {
        this.type = type;
        this.mode = mode;
        this.padding = padding;

        this.ivUpdater = ivUpdater;
        this.supplier = supplier;
        this.tl = ThreadLocal.withInitial(() -> new SoftReference<>(this.supplier.get()));

        this.encrypt = new CipherKey(new SecretKeySpec(side.ivSetter.apply(key.getKey(), true), "aaa"), side.ivSetter.apply(key.getIV(), false));
        this.decrypt = new CipherKey(new SecretKeySpec(side.ivSetter.apply(key.getKey(), false), "aaa"), side.ivSetter.apply(key.getIV(), true));
    }

    @Override
    public byte[] encrypt(@NonNull byte[] plaintext) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.encrypt) {
            //update key
            this.ivUpdater.accept(this.encrypt.getIV());
            //cipher.init(true, this.mode.getParametersFromKey(this.encrypt));
            cipher.init(true, this.encrypt);
        }

        //actually do the encryption
        byte[] encrypted = new byte[cipher.getOutputSize(plaintext.length)];
        int tam = cipher.processBytes(plaintext, 0, plaintext.length, encrypted, 0);
        doFinal(cipher, tam, encrypted);
        return encrypted;
    }

    @Override
    public byte[] decrypt(@NonNull byte[] ciphertext) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.decrypt) {
            //update key
            this.ivUpdater.accept(this.decrypt.getIV());
            //cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
            cipher.init(false, this.decrypt);
        }

        byte[] decrypted = new byte[cipher.getOutputSize(ciphertext.length)];
        int tam = cipher.processBytes(ciphertext, 0, ciphertext.length, decrypted, 0);
        doFinal(cipher, tam, decrypted);
        return decrypted;
    }

    @Override
    public OutputStream encrypt(@NonNull OutputStream out) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.encrypt) {
            //update key
            this.ivUpdater.accept(this.encrypt.getIV());
            //cipher.init(true, this.mode.getParametersFromKey(this.encrypt));
            cipher.init(true, this.encrypt);
        }

        return new CipherOutputStream(out, cipher);
    }

    @Override
    public InputStream decrypt(@NonNull InputStream in) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.decrypt) {
            //update key
            this.ivUpdater.accept(this.decrypt.getIV());
            //cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
            cipher.init(false, this.decrypt);
        }

        return new CipherInputStream(in, cipher);
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
        return String.format("Cipher$BlockCipher(type=%s, mode=%s, padding=%s)", this.type.name, this.mode.name, this.padding.name);
    }
}
