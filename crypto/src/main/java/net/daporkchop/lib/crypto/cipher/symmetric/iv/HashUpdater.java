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

package net.daporkchop.lib.crypto.cipher.symmetric.iv;

import lombok.Data;
import lombok.NonNull;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.hash.HashAlg;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Hashes the current IV with the key, and sets that as the new IV
 *
 * @author DaPorkchop_
 */
@Data
public class HashUpdater<P extends ParametersWithIV> implements IVUpdater<P> {
    private final HashAlg alg;

    @Override
    public void updateEncrypt(@NonNull AbstractSymmetricKey key) {
        doThing(key);
    }

    @Override
    public void updateDecrypt(@NonNull AbstractSymmetricKey key) {
        doThing(key);
    }

    private void doThing(AbstractSymmetricKey key) {
        byte[] iv = key.getIV();
        byte[] ivN = new byte[iv.length];
        int index = 0;
        while (index < iv.length) {
            byte[] hash = alg.hash(key.getKey(), iv, ivN);
            System.arraycopy(hash, 0, ivN, index, Math.min(iv.length - index, hash.length));
            index += hash.length;
        }
        key.setIV(ivN);
    }
}
