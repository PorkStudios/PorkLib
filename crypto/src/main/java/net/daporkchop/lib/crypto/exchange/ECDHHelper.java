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

package net.daporkchop.lib.crypto.exchange;

import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * @author DaPorkchop_
 */
public class ECDHHelper {
    private static Method engineInit;
    private static Method engineDoPhase;
    private static Method engineGenerateSecret;

    static {
        //intellij stop flagging this as duplicate code! this is a different class
        try {
            engineInit = KeyAgreementSpi.class.getDeclaredMethod("engineInit", Key.class, SecureRandom.class);
            engineDoPhase = KeyAgreementSpi.class.getDeclaredMethod("engineDoPhase", Key.class, boolean.class);
            engineGenerateSecret = KeyAgreementSpi.class.getDeclaredMethod("calcSecret");

            engineInit.setAccessible(true);
            engineDoPhase.setAccessible(true);
            engineGenerateSecret.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static byte[] generateCommonSecret(PrivateKey ownKey, PublicKey remoteKey) {
        KeyAgreementSpi spi = new KeyAgreementSpi.DH();
        try {
            engineInit.invoke(spi, ownKey, null);
            engineDoPhase.invoke(spi, remoteKey, true);

            return (byte[]) engineGenerateSecret.invoke(spi);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException(t);
        }
    }
}