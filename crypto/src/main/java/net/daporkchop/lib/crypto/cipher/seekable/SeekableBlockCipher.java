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

package net.daporkchop.lib.crypto.cipher.seekable;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.cipher.block.IVUpdater;
import net.daporkchop.lib.crypto.key.CipherKey;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * An implementation of {@link SeekableCipher} that is able to make a block cipher seekable.
 * <p>
 * This makes use of the {@link CipherMode#CTR} mode, and as such does not accept a mode as testMethodThing parameter.
 * <p>
 * This also makes use of a large amount of reflection. This class should explain pretty well why I almost
 * always make things protected when I can :/
 * <p>
 * Updating the IV (nonce in this case) automatically is not supported, as it would defeat the purpose of having a random access
 * cipher. It can still be updated manually by calling {@link IVUpdater#accept(byte[])} on {@link CipherKey#getIV()}
 *
 * @author DaPorkchop_
 */
public class SeekableBlockCipher extends SeekableCipher {
    protected static final Field kctr_iv;
    protected static final Field kctr_ofbV;
    protected static final Field kctr_ofbOutV;
    protected static final Field kctr_byteCount;

    static {
        Field iv = null;
        Field ofbV = null;
        Field ofbOutV = null;
        Field byteCount = null;
        try {
            {
                iv = KCTRBlockCipher.class.getDeclaredField("iv");
                iv.setAccessible(true);
            }
            {
                ofbV = KCTRBlockCipher.class.getDeclaredField("ofbV");
                ofbV.setAccessible(true);
            }
            {
                ofbOutV = KCTRBlockCipher.class.getDeclaredField("ofbOutV");
                ofbOutV.setAccessible(true);
            }
            {
                byteCount = KCTRBlockCipher.class.getDeclaredField("byteCount");
                byteCount.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } finally {
            kctr_iv = iv;
            kctr_ofbV = ofbV;
            kctr_ofbOutV = ofbOutV;
            kctr_byteCount = byteCount;
        }
    }

    protected final CipherKey encryptionKey;
    protected final CipherKey decryptionKey;
    @Getter
    protected final int blockSize;
    protected final ThreadLocal<CTRSeekable> cipherCache;

    public SeekableBlockCipher(@NonNull CipherType type, @NonNull CipherPadding padding, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this.encryptionKey = new CipherKey(side.ivSetter.apply(key.getKey(), true), side.ivSetter.apply(key.getIV(), false));
        this.decryptionKey = new CipherKey(side.ivSetter.apply(key.getKey(), false), side.ivSetter.apply(key.getIV(), true));
        this.blockSize = type.blockSize;

        this.cipherCache = ThreadLocal.withInitial(() -> new CTRSeekable(type.create(), padding));
    }

    @Override
    public byte[] encrypt(@NonNull byte[] plaintext, long offset) {
        CTRSeekable seekable = this.cipherCache.get();
        PaddedBufferedBlockCipher cipher = seekable.cipher;
        seekable.seek(offset);
        cipher.init(true, this.encryptionKey);
        byte[] encrypted = new byte[cipher.getOutputSize(plaintext.length)];
        int tam = cipher.processBytes(plaintext, 0, plaintext.length, encrypted, 0);
        try {
            cipher.doFinal(encrypted, tam);
            return encrypted;
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(@NonNull byte[] ciphertext, long offset) {
        CTRSeekable seekable = this.cipherCache.get();
        PaddedBufferedBlockCipher cipher = seekable.cipher;
        seekable.seek(offset);
        cipher.init(false, this.decryptionKey);
        byte[] decrypted = new byte[cipher.getOutputSize(ciphertext.length)];
        int tam = cipher.processBytes(ciphertext, 0, ciphertext.length, decrypted, 0);
        try {
            cipher.doFinal(decrypted, tam);
            return decrypted;
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream encrypt(@NonNull OutputStream out, long offset, long messageLength) {
        //we don't actually use messageLength here, it's up to the end user to do something with it
        CTRSeekable seekable = this.cipherCache.get();
        PaddedBufferedBlockCipher cipher = seekable.cipher;
        seekable.seek(offset);
        cipher.init(true, this.encryptionKey);
        return new CipherOutputStream(out, cipher);
    }

    @Override
    public InputStream decrypt(@NonNull InputStream in, long offset, long messageLength) {
        //we don't actually use messageLength here, it's up to the end user to do something with it
        CTRSeekable seekable = this.cipherCache.get();
        PaddedBufferedBlockCipher cipher = seekable.cipher;
        seekable.seek(offset);
        cipher.init(false, this.decryptionKey);
        return new CipherInputStream(in, cipher);
    }

    /**
     * A wrapper on top of {@link KCTRBlockCipher} to handle automagical updating of IVs and stuff
     */
    protected class CTRSeekable extends KCTRBlockCipher {
        protected final PaddedBufferedBlockCipher cipher;
        protected final byte[] the_iv;
        protected final byte[] the_ofbV;
        protected long pos;

        public CTRSeekable(@NonNull BlockCipher cipher, @NonNull CipherPadding padding) {
            super(cipher);
            this.cipher = new PaddedBufferedBlockCipher(this, padding.create());
            try {
                this.the_iv = (byte[]) kctr_iv.get(this);
                this.the_ofbV = (byte[]) kctr_ofbV.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void init(boolean forEncryption, @NonNull CipherParameters params) throws IllegalArgumentException {
            if (params instanceof CipherKey) {
                super.init(forEncryption, params);
            } else {
                throw new IllegalStateException(params.getClass().getCanonicalName());
            }
        }

        @Override
        public void reset() {
            super.reset();
            this.the_ofbV[0] = (byte) (((this.the_iv[0] & 0xFFL) + (this.pos & 0xFFL)) & 0xFFL);
            this.the_ofbV[1] = (byte) (((this.the_iv[1] & 0xFFL) + ((this.pos >>> 8L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[2] = (byte) (((this.the_iv[2] & 0xFFL) + ((this.pos >>> 16L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[3] = (byte) (((this.the_iv[3] & 0xFFL) + ((this.pos >>> 24L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[4] = (byte) (((this.the_iv[4] & 0xFFL) + ((this.pos >>> 32L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[5] = (byte) (((this.the_iv[5] & 0xFFL) + ((this.pos >>> 40L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[6] = (byte) (((this.the_iv[6] & 0xFFL) + ((this.pos >>> 48L) & 0xFFL)) & 0xFFL);
            this.the_ofbV[7] = (byte) (((this.the_iv[7] & 0xFFL) + ((this.pos >>> 56L) & 0xFFL)) & 0xFFL);
            this.setByteCount(0);
        }

        public void seek(long pos) {
            this.pos = pos;
        }

        public int getByteCount() {
            try {
                return kctr_byteCount.getInt(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public void setByteCount(int byteCount) {
            try {
                kctr_byteCount.setInt(this, byteCount);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
