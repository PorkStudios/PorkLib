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

package net.daporkchop.lib.crypto.sig.ec.impl;

import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.HashTypes;
import net.daporkchop.lib.crypto.sig.ec.AbstractECHelper;
import net.daporkchop.lib.crypto.sig.SignatureAlgorithms;
import net.daporkchop.lib.crypto.sig.ec.hackery.ECSignatureSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;

import java.io.IOException;
import java.math.BigInteger;

public class ECDSAHelper extends AbstractECHelper<EllipticCurveKeyPair> {
    public ECDSAHelper(HashTypes hash) {
        super(() -> new ECSignatureSpi(hash.getAsDigest(), SignatureAlgorithms.ECDSA.supplier.get(), new PlainDSAEncoder()));
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
