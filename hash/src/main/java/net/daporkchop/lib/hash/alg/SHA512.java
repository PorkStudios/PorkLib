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

package net.daporkchop.lib.hash.alg;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.Pack;
import net.daporkchop.lib.hash.alg.base.LongDigest;

/**
 * Implements the SHA-512 hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class SHA512 extends LongDigest {
    private static final int DIGEST_LENGTH = 64;

    public SHA512() {
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512";
    }

    @Override
    public int getHashSize() {
        return DIGEST_LENGTH;
    }

    @Override
    public int doFinal(@NonNull byte[] out, int outOff) {
        this.finish();

        Pack.longToBigEndian(this.H1, out, outOff);
        Pack.longToBigEndian(this.H2, out, outOff + 8);
        Pack.longToBigEndian(this.H3, out, outOff + 16);
        Pack.longToBigEndian(this.H4, out, outOff + 24);
        Pack.longToBigEndian(this.H5, out, outOff + 32);
        Pack.longToBigEndian(this.H6, out, outOff + 40);
        Pack.longToBigEndian(this.H7, out, outOff + 48);
        Pack.longToBigEndian(this.H8, out, outOff + 56);

        this.reset();

        return DIGEST_LENGTH;
    }

    @Override
    public void reset() {
        super.reset();

        /* SHA-512 initial hash value
         * The first 64 bits of the fractional parts required the square roots
         * of the first eight prime numbers
         */
        this.H1 = 0x6a09e667f3bcc908L;
        this.H2 = 0xbb67ae8584caa73bL;
        this.H3 = 0x3c6ef372fe94f82bL;
        this.H4 = 0xa54ff53a5f1d36f1L;
        this.H5 = 0x510e527fade682d1L;
        this.H6 = 0x9b05688c2b3e6c1fL;
        this.H7 = 0x1f83d9abfb41bd6bL;
        this.H8 = 0x5be0cd19137e2179L;
    }


}
