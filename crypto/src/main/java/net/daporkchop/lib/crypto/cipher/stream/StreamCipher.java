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

package net.daporkchop.lib.crypto.cipher.stream;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.key.CipherKey;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * A {@link Cipher} used for stream ciphers
 *
 * @author DaPorkchop_
 */
public class StreamCipher implements Cipher {
    private final StreamCipherType type;
    private final Lock outLock = new ReentrantLock();
    private final Lock inLock = new ReentrantLock();
    private final org.bouncycastle.crypto.StreamCipher outCipher;
    private final org.bouncycastle.crypto.StreamCipher inCipher;
    private String nameSuffix = "";

    public StreamCipher(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this(() -> mode.streamify(type.create()), StreamCipherType.BLOCK_CIPHER, key, side);
        this.nameSuffix = String.format(", blockType=%s, blockMode=%s", type.name, mode.name);
    }

    public StreamCipher(@NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this(type::create, type, key, side);
    }

    public StreamCipher(@NonNull Supplier<org.bouncycastle.crypto.StreamCipher> cipherSupplier, @NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side) {
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
    public OutputStream encrypt(OutputStream out) {
        this.outLock.lock();
        return new StreamCipherOutput(this.outLock, this.outCipher, out);
    }

    @Override
    public InputStream decrypt(InputStream in) {
        this.inLock.lock();
        return new StreamCipherInput(this.inLock, this.inCipher, in);
    }

    @Override
    public String toString() {
        return String.format("Cipher$StreamCipher(type=%s%s)", this.type.name, this.nameSuffix);
    }
}
