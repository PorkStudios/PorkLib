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

package net.daporkchop.lib.crypto.cipher;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.block.*;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipher;
import net.daporkchop.lib.crypto.cipher.stream.StreamCipherType;
import net.daporkchop.lib.crypto.key.CipherKey;
import net.daporkchop.lib.hash.util.Digest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * A cipher can encrypt and decrypt messages
 *
 * @author DaPorkchop_
 */
public interface Cipher {
    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key) {
        return createBlock(type, mode, padding, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        return createBlock(type, mode, padding, key, side, IVUpdater.SHA3_256);
    }

    static Cipher createBlock(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side, @NonNull IVUpdater ivUpdater) {
        return new BlockCipher(type, mode, padding, key, side, ivUpdater);
    }

    static Cipher createStream(@NonNull StreamCipherType type, @NonNull CipherKey key) {
        return createStream(type, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createStream(@NonNull StreamCipherType type, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        return new StreamCipher(type, key, side);
    }

    static Cipher createPseudoStream(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key) {
        return createPseudoStream(type, mode, key, CipherInitSide.ONE_WAY);
    }

    static Cipher createPseudoStream(@NonNull CipherType type, @NonNull CipherMode mode, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        return new StreamCipher(type, mode, key, side);
    }

    /**
     * Encrypt a message
     *
     * @param plaintext the plaintext (unencrypted) message
     * @return the encrypted ciphertext
     */
    byte[] encrypt(@NonNull byte[] plaintext);

    /**
     * Decrypt a message
     *
     * @param ciphertext the ciphertext (encrypted) message
     * @return the decrypted plaintext
     */
    byte[] decrypt(@NonNull byte[] ciphertext);

    /**
     * Gets an {@link OutputStream} that will encrypt data written to it
     *
     * @param out an {@link OutputStream} to write encrypted data to
     * @return an {@link OutputStream} that will encrypt data written to it
     */
    OutputStream encrypt(@NonNull OutputStream out);

    /**
     * Gets an {@link InputStream} that will decrypt data read from it
     *
     * @param in an {@link InputStream} to read encrypted data from
     * @return an {@link InputStream} that will decrypt data read from it
     */
    InputStream decrypt(@NonNull InputStream in);
}

