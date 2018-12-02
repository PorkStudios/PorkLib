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