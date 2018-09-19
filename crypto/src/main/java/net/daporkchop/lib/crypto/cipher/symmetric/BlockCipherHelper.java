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

package net.daporkchop.lib.crypto.cipher.symmetric;

import lombok.NonNull;
import net.daporkchop.lib.crypto.BouncyCastleInit;
import net.daporkchop.lib.crypto.cipher.symmetric.iv.IVUpdater;
import net.daporkchop.lib.crypto.cipher.symmetric.iv.UpdaterMode;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

public abstract class BlockCipherHelper<P extends AbstractSymmetricKey> {

    static {
        BouncyCastleInit.loadClass();
    }

    public final BlockCipherMode mode;
    public final BlockCipherType type;
    public final BlockCipherPadding padding;
    private final ThreadLocal<WeakReference<BufferedBlockCipher>> tl;
    private final Supplier<BufferedBlockCipher> supplier;
    private final AbstractSymmetricKey encrypt, decrypt;
    private final IVUpdater updater;

    protected BlockCipherHelper(@NonNull BlockCipherType type, @NonNull BlockCipherMode mode, @NonNull BlockCipherPadding scheme, @NonNull P key, @NonNull UpdaterMode updater) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null!");
        if (mode == null) throw new IllegalArgumentException("Mode cannot be null!");
        if (scheme == null) throw new IllegalArgumentException("Padding scheme cannot be null!");
        if (!mode.isCompatible(type))
            throw new IllegalArgumentException("Cipher type \"" + type.name + "\" is incompatible with cipher mode \"" + mode.name + "\"!");

        this.supplier = () -> {
            BlockCipher cipher = type.create();
            cipher = mode.wrap(cipher);
            return new PaddedBufferedBlockCipher(cipher, scheme.getPadding().create());
        };
        this.tl = ThreadLocal.withInitial(() -> new WeakReference<>(supplier.get()));
        this.mode = mode;
        this.type = type;
        this.padding = scheme;

        KeyParameter sharedKey = (KeyParameter) key.getParameters();
        this.encrypt = type.keyInstance.apply(new SecretKeySpec(sharedKey.getKey(), "aaa"), key.getIV());
        this.decrypt = type.keyInstance.apply(new SecretKeySpec(sharedKey.getKey(), "aaa"), key.getIV());
        this.updater = updater.getUpdater();
    }

    private static byte[] prefixLength(byte[] data) {
        byte[] prefixed = new byte[data.length + 4];
        System.arraycopy(data, 0, prefixed, 4, data.length);
        System.arraycopy(ByteBuffer.allocate(4).putInt(data.length).array(), 0, prefixed, 0, 4);
        return prefixed;
    }

    /**
     * Encrypts a given byte array
     *
     * @param data the bytes to encrypt
     * @return the encrypted bytes
     */
    public byte[] encrypt(byte[] data) {
        return encrypt(data, false);
    }

    /**
     * Encrypts a given byte array
     *
     * @param data         the bytes to encrypt
     * @param lengthPrefix if true, 32-bit (4-byte) length header is prefixed to the bytes and encrypted
     *                     with it. this is useful for discarding any padding that may be added to
     *                     the end of the encrypted data
     * @return the encrypted bytes
     */
    public byte[] encrypt(byte[] data, boolean lengthPrefix) {
        if (lengthPrefix) {
            data = prefixLength(data);
        }
        BufferedBlockCipher cipher = get();

        synchronized (this.encrypt) {
            //update key
            this.updater.updateEncrypt(this.encrypt);
            cipher.init(true, mode.getParametersFromKey(this.encrypt));
        }

        //actually do the encryption
        byte[] encrypted = new byte[cipher.getOutputSize(data.length)];
        int tam = cipher.processBytes(data, 0, data.length, encrypted, 0);
        doCipher(cipher, tam, encrypted);
        return encrypted;
    }

    /**
     * Decrypts a given byte array
     *
     * @param data the bytes to decrypt
     * @return the decrypted bytes
     */
    public byte[] decrypt(byte[] data) {
        return decrypt(data, false);
    }

    /**
     * Decrypts a given byte array
     *
     * @param data         the bytes to decrypt
     * @param lengthPrefix if true, 32-bit (4-byte) length header way prefixed to the bytes during
     *                     encryption. this is useful for discarding any padding that may be added to
     *                     the end of the encrypted data
     * @return the decrypted bytes
     */
    public byte[] decrypt(byte[] data, boolean lengthPrefix) {
        BufferedBlockCipher cipher = get();

        synchronized (this.decrypt) {
            //update key
            this.updater.updateDecrypt(this.decrypt);
            cipher.init(false, mode.getParametersFromKey(this.decrypt));
        }

        byte[] decrypted = new byte[cipher.getOutputSize(data.length)];
        if (lengthPrefix && decrypted.length < 4) {
            throw new IllegalArgumentException("Length header is missing!");
        }
        int tam = cipher.processBytes(data, 0, data.length, decrypted, 0);
        doCipher(cipher, tam, decrypted);
        if (lengthPrefix) {
            int totalLength =
                    (((decrypted[0] & 0xFF) << 24)
                            | ((decrypted[1] & 0xFF) << 16)
                            | ((decrypted[2] & 0xFF) << 8)
                            | (decrypted[3] & 0xFF));
            byte[] unPrefixed = new byte[totalLength];
            System.arraycopy(decrypted, 4, unPrefixed, 0, totalLength);
            return unPrefixed;
        }
        return decrypted;
    }

    /**
     * Gets an output stream that will encrypt data using this cipher helper's key
     *
     * @param stream the stream that the encrypted data will be written to
     * @return a stream that encrypts written data and writes it to the given stream
     */
    public OutputStream encryptionStream(@NonNull OutputStream stream) {
        BufferedBlockCipher cipher = get();

        synchronized (this.encrypt) {
            //update key
            this.updater.updateEncrypt(this.encrypt);
            cipher.init(true, mode.getParametersFromKey(this.encrypt));
        }

        return new CipherOutputStream(stream, cipher);
        //return new BufferedOutputStream(new CipherOutputStream(stream, cipher), this.type.blockSize >> 3);
    }

    /**
     * Gets an input stream that will decrypt data using this cipher helper's key
     *
     * @param stream the stream that the decrypt data will be read from
     * @return a stream that reads data from the given stream and decrypts it
     */
    public InputStream decryptionStream(@NonNull InputStream stream) {
        BufferedBlockCipher cipher = get();

        synchronized (this.decrypt) {
            //update key
            this.updater.updateDecrypt(this.decrypt);
            cipher.init(false, this.mode.getParametersFromKey(this.decrypt));
        }

        return new CipherInputStream(stream, cipher);
        //return new BufferedInputStream(new CipherInputStream(stream, cipher, this.type.blockSize >> 3), this.type.blockSize >> 3);
    }

    private void doCipher(BufferedBlockCipher c, int tam, byte[] b) {
        try {
            c.doFinal(b, tam);
        } catch (CryptoException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private BufferedBlockCipher get() {
        BufferedBlockCipher cipher;
        if ((cipher = tl.get().get()) == null) {
            cipher = supplier.get();
            tl.set(new WeakReference<>(cipher));
        }
        return cipher;
    }
}
