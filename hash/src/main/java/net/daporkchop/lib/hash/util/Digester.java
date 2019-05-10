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

package net.daporkchop.lib.hash.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataOut;
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
        byte[] b = PorkUtil.BUFFER_CACHE_SMALL.get();
        int i;
        while ((i = in.read(b)) != -1) {
            this.append(b, 0, i);
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
            public void write(@NonNull byte[] b, int off, int len) throws IOException {
                Digester.this.append(b, off, len);
            }

            @Override
            public void write(@NonNull byte[] b) throws IOException {
                Digester.this.append(b);
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
