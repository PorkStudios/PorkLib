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

package net.daporkchop.lib.crypto.sig.asymmetric;

import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;

import java.io.IOException;
import java.math.BigInteger;

public class PlainDSAEncoder implements DSAEncoder {
    public static final PlainDSAEncoder INSTANCE = new PlainDSAEncoder();

    private PlainDSAEncoder() {
    }

    private static byte[] doEncode(BigInteger r, BigInteger s) {
        byte[] first = doMakeUnsigned(r);
        byte[] second = doMakeUnsigned(s);
        byte[] res;

        if (first.length > second.length) {
            res = new byte[first.length * 2];
        } else {
            res = new byte[second.length * 2];
        }

        System.arraycopy(first, 0, res, res.length / 2 - first.length, first.length);
        System.arraycopy(second, 0, res, res.length - second.length, second.length);

        return res;
    }

    private static byte[] doMakeUnsigned(BigInteger val) {
        byte[] res = val.toByteArray();

        if (res[0] == 0) {
            byte[] tmp = new byte[res.length - 1];

            System.arraycopy(res, 1, tmp, 0, tmp.length);

            return tmp;
        }

        return res;
    }

    private static BigInteger[] doDecode(byte[] encoding) {
        BigInteger[] sig = new BigInteger[2];

        byte[] first = new byte[encoding.length / 2];
        byte[] second = new byte[encoding.length / 2];

        System.arraycopy(encoding, 0, first, 0, first.length);
        System.arraycopy(encoding, first.length, second, 0, second.length);

        sig[0] = new BigInteger(1, first);
        sig[1] = new BigInteger(1, second);

        return sig;
    }

    @Override
    public byte[] encode(BigInteger r, BigInteger s) throws IOException {
        return doEncode(r, s);
    }

    @Override
    public BigInteger[] decode(byte[] sig) throws IOException {
        return doDecode(sig);
    }
}
