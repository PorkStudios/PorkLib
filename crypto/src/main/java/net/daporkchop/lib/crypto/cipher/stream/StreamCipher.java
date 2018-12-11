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
        return String.format("Cipher$StreamCipher(type=%s%s)", this.type.name, this.nameSuffix);
    }
}
