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

package net.daporkchop.lib.crypto.sig.ec.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.SignatureAlgorithms;
import net.daporkchop.lib.crypto.sig.ec.SignatureHelper;
import net.daporkchop.lib.crypto.sig.ec.hackery.ECSignatureSpi;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.hash.util.DigestAlg;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;

import java.io.IOException;
import java.math.BigInteger;

public class ECDSAHelper extends SignatureHelper<EllipticCurveKeyPair> {
    public ECDSAHelper(@NonNull Digest digest) {
        super(() -> new ECSignatureSpi(new AsBouncyCastleDigestWrapper(digest.getSupplier().get()), SignatureAlgorithms.ECDSA.supplier.get(), new PlainDSAEncoder()));
    }

    @RequiredArgsConstructor
    @Getter
    private static class AsBouncyCastleDigestWrapper implements org.bouncycastle.crypto.Digest {
        @NonNull
        private final DigestAlg digest;

        @Override
        public String getAlgorithmName() {
            return this.digest.getAlgorithmName();
        }

        @Override
        public int getDigestSize() {
            return this.digest.getHashSize();
        }

        @Override
        public void update(byte in) {
            this.digest.update(in);
        }

        @Override
        public void update(byte[] in, int inOff, int len) {
            this.digest.update(in, inOff, len);
        }

        @Override
        public int doFinal(byte[] out, int outOff) {
            return this.digest.doFinal(out, outOff);
        }

        @Override
        public void reset() {
            this.digest.reset();
        }
    }

    public static class PlainDSAEncoder implements DSAEncoder {
        @Override
        public byte[] encode(BigInteger var1, BigInteger var2) throws IOException {
            byte[] var3 = this.makeUnsigned(var1);
            byte[] var4 = this.makeUnsigned(var2);
            byte[] var5;
            if (var3.length > var4.length) {
                var5 = new byte[var3.length * 2];
            } else {
                var5 = new byte[var4.length * 2];
            }

            System.arraycopy(var3, 0, var5, var5.length / 2 - var3.length, var3.length);
            System.arraycopy(var4, 0, var5, var5.length - var4.length, var4.length);
            return var5;
        }

        private byte[] makeUnsigned(BigInteger var1) {
            byte[] var2 = var1.toByteArray();
            if (var2[0] == 0) {
                byte[] var3 = new byte[var2.length - 1];
                System.arraycopy(var2, 1, var3, 0, var3.length);
                return var3;
            } else {
                return var2;
            }
        }

        @Override
        public BigInteger[] decode(byte[] var1) throws IOException {
            BigInteger[] var2 = new BigInteger[2];
            byte[] var3 = new byte[var1.length / 2];
            byte[] var4 = new byte[var1.length / 2];
            System.arraycopy(var1, 0, var3, 0, var3.length);
            System.arraycopy(var1, var3.length, var4, 0, var4.length);
            var2[0] = new BigInteger(1, var3);
            var2[1] = new BigInteger(1, var4);
            return var2;
        }
    }
}
