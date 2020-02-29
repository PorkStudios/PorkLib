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

package net.daporkchop.lib.crypto.sig.ec;

import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.sig.ec.hackery.ECSignatureSpi;

import java.lang.ref.SoftReference;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.function.Supplier;

public abstract class SignatureHelper<P extends EllipticCurveKeyPair> {
    protected final ThreadLocal<SoftReference<ECSignatureSpi>> tl;
    protected final Supplier<ECSignatureSpi> supplier;

    protected SignatureHelper(Supplier<ECSignatureSpi> supplier) {
        this.supplier = supplier;
        this.tl = ThreadLocal.withInitial(() -> new SoftReference<>(supplier.get()));
    }

    public byte[] sign(byte[] data, P key) {
        ECSignatureSpi spi = this.get();
        try {
            spi.engineInitSign(key.getPrivateKey());
            spi.engineUpdate(data, 0, data.length);
            return spi.engineSign();
        } catch (InvalidKeyException
                | SignatureException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public boolean verify(byte[] sig, byte[] data, P key) {
        ECSignatureSpi spi = this.get();
        try {
            spi.engineInitVerify(key.getPublicKey());
            spi.engineUpdate(data, 0, data.length);
            return spi.engineVerify(sig);
        } catch (InvalidKeyException
                | SignatureException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    protected ECSignatureSpi get() {
        ECSignatureSpi spi;
        if ((spi = this.tl.get().get()) == null) {
            spi = this.supplier.get();
            this.tl.set(new SoftReference<>(spi));
        }
        return spi;
    }
}
