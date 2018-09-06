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

package net.daporkchop.lib.gdxnetwork.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.exchange.ECDHHelper;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.crypto.keygen.ec.ECDHKeyGen;
import net.daporkchop.lib.crypto.sig.ec.CurveType;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public class CryptHelper {
    private static final Map<CurveType, ECDHKeyPair> keyPairs = new EnumMap<>(CurveType.class);

    private final CryptographySettings settings;
    private BlockCipherHelper cipherHelper;

    public CryptHelper(CryptographySettings settings) {
        this.settings = settings;
    }

    public static ECDHKeyPair getKeypair(@NonNull CurveType type) {
        synchronized (keyPairs) {
            return keyPairs.computeIfAbsent(type, ECDHKeyGen::gen);
        }
    }

    public void initCipher(PublicKey remoteKey) {
        if (this.settings != null)  {
            assert remoteKey != null;

            synchronized (this) {
                ECDHKeyPair keyPair = getKeypair(this.settings.getCurveType());
                AbstractSymmetricKey key = ECDHHelper.generateKey(keyPair.getPrivateKey(), remoteKey, this.settings.getCipherType());
                this.cipherHelper = this.settings.getCipherType().createHelper(this.settings.getCipherMode(), this.settings.getCipherPadding(), key);
            }
        }
    }

    public InputStream wrap(@NonNull InputStream in)    {
        if (this.settings == null)  {
            return in;
        } else {
            return this.cipherHelper.decryptionStream(in);
        }
    }

    public OutputStream wrap(@NonNull OutputStream out)    {
        if (this.settings == null)  {
            return out;
        } else {
            return this.cipherHelper.encryptionStream(out);
        }
    }
}
