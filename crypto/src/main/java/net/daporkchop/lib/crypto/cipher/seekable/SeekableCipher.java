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

package net.daporkchop.lib.crypto.cipher.seekable;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.Cipher;
import net.daporkchop.lib.common.math.PMath;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Cipher} that supports random access
 *
 * @author DaPorkchop_
 */
public abstract class SeekableCipher implements Cipher {
    protected final AtomicLong encryptionOffsetCounter = new AtomicLong(0L);
    protected final AtomicLong decryptionOffsetCounter = new AtomicLong(0L);

    /**
     * This method should not be used here in favor of {@link #encrypt(byte[], long)}.
     */
    @Override
    @Deprecated
    public byte[] encrypt(@NonNull byte[] plaintext) {
        return this.encrypt(plaintext, this.encryptionOffsetCounter.getAndAdd(this.getOffsetRequired(plaintext.length)));
    }

    /**
     * This method should not be used here in favor of {@link #decrypt(byte[], long)}}.
     */
    @Override
    @Deprecated
    public byte[] decrypt(@NonNull byte[] ciphertext) {
        return this.decrypt(ciphertext, this.decryptionOffsetCounter.getAndAdd(this.getOffsetRequired(ciphertext.length)));
    }

    /**
     * This method should not be used here in favor of {@link #encrypt(OutputStream, long, long)}
     */
    @Override
    @Deprecated
    public OutputStream encrypt(@NonNull OutputStream out) {
        long offsetRequired = this.getOffsetRequired(Integer.MAX_VALUE);
        return this.encrypt(out, this.encryptionOffsetCounter.getAndAdd(offsetRequired), offsetRequired);
    }

    /**
     * This method should not be used here in favor of {@link #decrypt(InputStream, long, long)}
     */
    @Override
    @Deprecated
    public InputStream decrypt(@NonNull InputStream in) {
        long offsetRequired = this.getOffsetRequired(Integer.MAX_VALUE);
        return this.decrypt(in, this.decryptionOffsetCounter.getAndAdd(offsetRequired), offsetRequired);
    }

    /**
     * Encrypt a message
     *
     * @param plaintext the plaintext message to encrypt
     * @param offset    the offset to decrypt at. for stream ciphers this should be given in bytes, for
     *                  block ciphers it should be given in blocks
     * @return the encrypted message
     */
    public abstract byte[] encrypt(@NonNull byte[] plaintext, long offset);

    /**
     * Decrypt a message
     *
     * @param ciphertext the ciphertext message to decrypt
     * @param offset     the offset to encrypt at. for stream ciphers this should be given in bytes, for
     *                   block ciphers it should be given in blocks
     * @return the decrypted message
     */
    public abstract byte[] decrypt(@NonNull byte[] ciphertext, long offset);

    /**
     * Wrap an {@link OutputStream} to encrypt data written to it
     *
     * @param out           the {@link OutputStream} that encrypted data will be written to
     * @param offset        the offset to encrypt at. for stream ciphers this should be given in bytes, for
     *                      block ciphers it should be given in blocks
     * @param messageLength the length of the message that will be encrypted, in bytes
     * @return an {@link OutputStream} that will encrypt data written to it
     */
    public abstract OutputStream encrypt(@NonNull OutputStream out, long offset, long messageLength);

    /**
     * Wrap an {@link InputStream} to decrypt data read from it
     *
     * @param in            the {@link InputStream} that encrypted data will be read from
     * @param offset        the offset to encrypt at. for stream ciphers this should be given in bytes, for
     *                      block ciphers it should be given in blocks
     * @param messageLength the length of the message that will be decrypted, in bytes
     * @return an {@link InputStream} that will decrypt data read from it
     */
    public abstract InputStream decrypt(@NonNull InputStream in, long offset, long messageLength);

    /**
     * Get the amount that the offset needs to be incremented by after encrypting/decrypting a message
     * with the given length
     *
     * @param messageLength the length of the message
     * @return the amount that the offset needs to be incremented by
     */
    public long getOffsetRequired(long messageLength) {
        if (messageLength < 0) {
            throw new IllegalArgumentException("Length must be more than 0!");
        } else if (this.getBlockSize() == -1) {
            return messageLength;
        } else {
            return PMath.roundUp(messageLength, this.getBlockSize());
        }
    }

    /**
     * Gets this cipher's block size.
     * <p>
     * If -1, this cipher does not use blocks.
     *
     * @return this cipher's block size
     */
    public abstract int getBlockSize();
}
