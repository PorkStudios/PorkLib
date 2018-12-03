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
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherInput;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherOutput;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.hash.util.Digest;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Cipher {
    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key) {
        return createBlock(type, mode, padding, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        return createBlock(type, mode, padding, key, side, b -> {
            byte[] hash = Digest.SHA3_256.hash(b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
        });
    }

    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull Consumer<byte[]> ivUpdater) {
        return new BlockCipherImpl(type, mode, padding, key, side, ivUpdater);
    }

    static Cipher createStream(@NonNull StreamCipherType type, @NonNull CipherKey key) {
        return createStream(type, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createStream(@NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        return new StreamCipherImpl(type, key, side);
    }

    static Cipher createPseudoStream(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key)  {
        return createPseudoStream(type, mode, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createPseudoStream(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key, @NonNull CipherInitSide side)  {
        return new StreamCipherImpl(type, mode, key, side);
    }

    byte[] encrypt(@NonNull byte[] plaintext);

    byte[] decrypt(@NonNull byte[] ciphertext);

    OutputStream encrypt(@NonNull OutputStream outputStream);

    InputStream decrypt(@NonNull InputStream inputStream);
}

class BlockCipherImpl implements Cipher {
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
    private final Consumer<byte[]> ivUpdater;
    private final Supplier<BufferedBlockCipher> supplier;
    private final CipherKey encrypt, decrypt;

    public BlockCipherImpl(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull Consumer<byte[]> ivUpdater) {
        this(() -> {
            BlockCipher cipher = type.create();
            cipher = mode.wrap(cipher);
            return new PaddedBufferedBlockCipher(cipher, padding.create());
        }, type, mode, padding, key, side, ivUpdater);
    }

    BlockCipherImpl(@NonNull Supplier<BufferedBlockCipher> supplier, @NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull Consumer<byte[]> ivUpdater) {
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
    public byte[] encrypt(byte[] plaintext) {
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
    public byte[] decrypt(byte[] ciphertext) {
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
    public OutputStream encrypt(OutputStream outputStream) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.encrypt) {
            //update key
            this.ivUpdater.accept(this.encrypt.getIV());
            //cipher.init(true, this.mode.getParametersFromKey(this.encrypt));
            cipher.init(true, this.encrypt);
        }

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public InputStream decrypt(InputStream inputStream) {
        BufferedBlockCipher cipher = this.get();

        synchronized (this.decrypt) {
            //update key
            this.ivUpdater.accept(this.decrypt.getIV());
            //cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
            cipher.init(false, this.decrypt);
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
    private final StreamCipherType type;
    private final Lock outLock = new ReentrantLock();
    private final Lock inLock = new ReentrantLock();
    private final StreamCipher outCipher;
    private final StreamCipher inCipher;
    private String nameSuffix = "";

    public StreamCipherImpl(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this(() -> mode.streamify(type.create()), StreamCipherType.BLOCK_CIPHER, key, side);
        this.nameSuffix = String.format(", blockType=%s, blockMode=%s", type.name, mode.name);
    }

    public StreamCipherImpl(@NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side)   {
        this(type::create, type, key, side);
    }

    StreamCipherImpl(@NonNull Supplier<StreamCipher> cipherSupplier, @NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this.type = type;

        this.outCipher = cipherSupplier.get();
        this.inCipher = cipherSupplier.get();

        this.outCipher.init(true, new CipherKey(side.ivSetter.apply(key.getKey(), true), side.ivSetter.apply(key.getIV(), false)));
        this.inCipher.init(false, new CipherKey(side.ivSetter.apply(key.getKey(), false), side.ivSetter.apply(key.getIV(), true))); //TODO: figure out a way to use different IVs for encryption and decryption automagically
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        this.outLock.lock();
        try {
            byte[] b = new byte[plaintext.length];
            this.outCipher.processBytes(plaintext, 0, plaintext.length, b, 0);
            return b;
        } finally {
            this.outLock.unlock();
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) {
        this.inLock.lock();
        try {
            byte[] b = new byte[ciphertext.length];
            this.inCipher.processBytes(ciphertext, 0, ciphertext.length, b, 0);
            return b;
        } finally {
            this.inLock.unlock();
        }
    }

    @Override
    public OutputStream encrypt(OutputStream outputStream) {
        this.outLock.lock();
        return new StreamCipherOutput(this.outLock, this.outCipher, outputStream);
    }

    @Override
    public InputStream decrypt(InputStream inputStream) {
        this.inLock.lock();
        return new StreamCipherInput(this.inLock, this.inCipher, inputStream);
    }

    @Override
    public String toString() {
        return String.format("Cipher$StreamCipherImpl(type=%s%s)", this.type.name, this.nameSuffix);
    }
}
