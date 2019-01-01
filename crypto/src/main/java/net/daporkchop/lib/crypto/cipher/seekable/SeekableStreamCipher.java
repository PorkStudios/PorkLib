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

import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.crypto.cipher.CipherInitSide;
import net.daporkchop.lib.crypto.key.CipherKey;
import org.bouncycastle.crypto.SkippingStreamCipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.BufferUnderflowException;
import java.util.function.Supplier;

/**
 * An implementation of {@link SeekableCipher} that can encrypt and decrypt messages using an implementation
 * of {@link SkippingStreamCipher}
 *
 * @author DaPorkchop_
 */
public class SeekableStreamCipher extends SeekableCipher {
    private final Supplier<SkippingStreamCipher> cipherSupplier;
    private final ThreadLocal<SoftReference<SkippingStreamCipher>> encryptionCipherCache = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<SoftReference<SkippingStreamCipher>> decryptionCipherCache = ThreadLocal.withInitial(() -> null);
    private final CipherKey encryptionKey;
    private final CipherKey decryptionKey;

    public SeekableStreamCipher(@NonNull Supplier<SkippingStreamCipher> cipherSupplier, @NonNull CipherKey key, @NonNull CipherInitSide side) {
        this.cipherSupplier = cipherSupplier;

        this.encryptionKey = new CipherKey(side.ivSetter.apply(key.getKey(), true), side.ivSetter.apply(key.getIV(), false));
        this.decryptionKey = new CipherKey(side.ivSetter.apply(key.getKey(), false), side.ivSetter.apply(key.getIV(), true));
    }

    @Override
    public byte[] encrypt(@NonNull byte[] plaintext, long offset) {
        byte[] b = new byte[plaintext.length];
        if (plaintext.length != 0) {
            SkippingStreamCipher cipher = this.getEncryptionCipher();
            if (cipher.getPosition() != offset) {
                cipher.seekTo(offset);
            }
            cipher.processBytes(plaintext, 0, plaintext.length, b, 0);
        }
        return b;
    }

    @Override
    public byte[] decrypt(@NonNull byte[] ciphertext, long offset) {
        byte[] b = new byte[ciphertext.length];
        if (ciphertext.length != 0) {
            SkippingStreamCipher cipher = this.getDecryptionCipher();
            if (cipher.getPosition() != offset) {
                cipher.seekTo(offset);
            }
            cipher.processBytes(ciphertext, 0, ciphertext.length, b, 0);
        }
        return b;
    }

    @Override
    public OutputStream encrypt(@NonNull OutputStream theOut, long theOffset, long messageLength) {
        //we don't actually use messageLength here, it's up to the end user to do something with it
        return new OutputStream() {
            private final OutputStream out = theOut;
            private final long offset = theOffset;
            private final SkippingStreamCipher cipher = SeekableStreamCipher.this.getEncryptionCipher();

            {
                if (this.cipher.getPosition() != this.offset) {
                    this.cipher.seekTo(this.offset);
                }
            }

            @Override
            public void write(int b) throws IOException {
                this.out.write(this.cipher.returnByte((byte) b) & 0xFF);
            }

            @Override
            public void write(@NonNull byte[] b, int off, int len) throws IOException {
                if (off < 0 || len < 0) {
                    throw new IllegalArgumentException();
                } else if (b.length < off + len)   {
                    throw new BufferUnderflowException();
                } else {
                    byte[] buf = PorkUtil.BUFFER_CACHE_SMALL.get();
                    int bytes;
                    for (int i = 0; i < len; i += bytes)   {
                        bytes = Math.min(len - i, buf.length);
                        this.cipher.processBytes(b, off + i, bytes, buf, 0);
                        this.out.write(buf, 0, bytes);
                    }
                }
            }

            @Override
            public void flush() throws IOException {
                this.out.flush();
            }

            @Override
            public void close() throws IOException {
                this.out.close();
            }
        };
    }

    @Override
    public InputStream decrypt(@NonNull InputStream theIn, long theOffset, long messageLength) {
        //we don't actually use messageLength here, it's up to the end user to do something with it
        return new InputStream() {
            private final InputStream in = theIn;
            private final long offset = theOffset;
            private final SkippingStreamCipher cipher = SeekableStreamCipher.this.getDecryptionCipher();

            {
                if (this.cipher.getPosition() != this.offset) {
                    this.cipher.seekTo(this.offset);
                }
            }

            @Override
            public int read() throws IOException {
                int i = this.in.read();
                if (i == -1)    {
                    return -1;
                } else {
                    return this.cipher.returnByte((byte) i) & 0xFF;
                }
            }

            @Override
            public int read(@NonNull byte[] b, int off, int len) throws IOException {
                if (off < 0 || len < 0) {
                    throw new IllegalArgumentException();
                } else if (b.length < off + len)   {
                    throw new BufferUnderflowException();
                } else {
                    byte[] buf = PorkUtil.BUFFER_CACHE_SMALL.get();
                    int i;
                    int j = -1;
                    int bytes = Math.min(len, buf.length);
                    while ((i = this.in.read(buf, 0, bytes)) != -1)  {
                        if(j == -1) {
                            j = 0;
                        }
                        this.cipher.processBytes(buf, 0, bytes, b, off + j);
                        j += i;
                        if ((bytes = Math.min(len - j, buf.length)) == 0)   {
                            //we're done!
                            break;
                        }
                    }
                    return j;
                }
            }

            @Override
            public long skip(long n) throws IOException {
                if (this.in.skip(n) != n)   {
                    throw new IllegalStateException("InputStream didn't skip bytes!");
                } else if (this.cipher.skip(n) != n)    {
                    throw new IllegalStateException("Cipher didn't skip bytes!");
                } else {
                    return n;
                }
            }

            @Override
            public int available() throws IOException {
                return this.in.available();
            }

            @Override
            public void close() throws IOException {
                this.in.close();
            }
        };
    }

    @Override
    public long getOffsetRequired(long messageLength) {
        return messageLength;
    }

    @Override
    public int getBlockSize() {
        return -1;
    }

    protected SkippingStreamCipher getEncryptionCipher() {
        SoftReference<SkippingStreamCipher> ref = this.encryptionCipherCache.get();
        SkippingStreamCipher cipher;
        if (ref == null || (cipher = ref.get()) == null) {
            cipher = this.cipherSupplier.get();
            this.encryptionCipherCache.set(new SoftReference<>(cipher));
            cipher.init(true, this.encryptionKey);
        }
        return cipher;
    }

    protected SkippingStreamCipher getDecryptionCipher() {
        SoftReference<SkippingStreamCipher> ref = this.decryptionCipherCache.get();
        SkippingStreamCipher cipher;
        if (ref == null || (cipher = ref.get()) == null) {
            cipher = this.cipherSupplier.get();
            this.decryptionCipherCache.set(new SoftReference<>(cipher));
            cipher.init(false, this.decryptionKey);
        }
        return cipher;
    }
}
