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
