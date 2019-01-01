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
         * The first 64 bits of the fractional parts of the square roots
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
