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

package net.daporkchop.lib.crypto.cipher;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.hash.util.Digest;

import java.util.function.BiFunction;

/**
 * Used for setting a starting IV when initializing a cipher.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public enum CipherInitSide {
    SERVER((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    CLIENT((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    /**
     * Should only be used if data is to be read and written in the same direction (e.g. for encrypting a file)
     */
    ONE_WAY((iv, recv) -> iv.clone());

    @NonNull
    public final BiFunction<byte[], Boolean, byte[]> ivSetter;
}
