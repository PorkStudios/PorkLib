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

package net.daporkchop.lib.hash.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used by {@link Digest} for adding things to a hash
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Digester {
    @NonNull
    private DigestAlg digest;
    @NonNull
    private final byte[] hashBuf;

    public Digester(@NonNull DigestAlg digest) {
        this(digest, new byte[digest.getHashSize()]);
    }

    public Digester append(byte b) {
        this.digest.update(b);
        return this;
    }

    public Digester append(int b) {
        if (b >= 256 || b < 0) {
            throw new IllegalStateException("input value must be in range 0-255!");
        }
        this.digest.update((byte) b);
        return this;
    }

    public Digester append(@NonNull byte[] arr) {
        this.digest.update(arr);
        return this;
    }

    public Digester append(@NonNull byte[] arr, int start, int len) {
        this.digest.update(arr, start, len);
        return this;
    }

    public Digester append(@NonNull File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return this.append(in);
        }
    }

    public Digester append(@NonNull InputStream in) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get())    {
            byte[] b = handle.get();
            int i;
            while ((i = in.read(b)) != -1) {
                this.append(b, 0, i);
            }
        }
        in.close();
        return this;
    }

    public DataOut appendStream()   {
        return new DataOut() {
            @Override
            public void close() throws IOException {
            }

            @Override
            public void write(int b) throws IOException {
                Digester.this.append((byte) b);
            }

            @Override
            public void write(@NonNull byte[] src, int start, int length) throws IOException {
                Digester.this.append(src, start, length);
            }

            @Override
            public void write(@NonNull byte[] src) throws IOException {
                Digester.this.append(src);
            }
        };
    }

    public Hash hash() {
        this.digest.doFinal(this.hashBuf, 0);
        this.digest = null;
        return new Hash(this.hashBuf);
    }

    public byte[] hashToByteArray() {
        this.digest.doFinal(this.hashBuf, 0);
        this.digest = null;
        return this.hashBuf;
    }
}
