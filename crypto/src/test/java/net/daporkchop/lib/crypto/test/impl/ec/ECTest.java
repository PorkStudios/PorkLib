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

package net.daporkchop.lib.crypto.test.impl.ec;

import net.daporkchop.lib.crypto.key.ec.AbstractECKeyPair;
import net.daporkchop.lib.crypto.key.serializer.impl.ECKeySerializer;
import net.daporkchop.lib.crypto.sig.HashTypes;
import net.daporkchop.lib.crypto.sig.ec.AbstractECHelper;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.crypto.test.TestMain;
import net.daporkchop.lib.encoding.basen.Base58;
import org.junit.Test;

public abstract class ECTest<K extends AbstractECKeyPair, H extends AbstractECHelper> {
    @Test
    public void test() {
        for (CurveType curve : CurveType.values()) {
            K keys = getKeys(curve);
            byte[] encoded = ECKeySerializer.INSTANCE.serialize(keys);
            keys = ECKeySerializer.INSTANCE.<K>deserialize(encoded);
            for (HashTypes hash : HashTypes.values()) {
                H helper = getHelper(hash);

                for (byte[] b : TestMain.RANDOM_DATA) {
                    byte[] sig = sign(b, keys, helper);
                    if (!verify(b, sig, keys, helper)) {
                        System.out.println("Public key: " + Base58.encodeBase58(keys.getPublicKey().getEncoded()));
                        System.out.println("Private key: " + Base58.encodeBase58(keys.getPrivateKey().getEncoded()));
                        System.out.println("Data: " + Base58.encodeBase58(b));
                        System.out.println("Signature: " + Base58.encodeBase58(sig));
                        System.out.println("Curve+Hash: " + curve.name + "+" + hash.name());
                        throw new IllegalStateException("Invalid signature!");
                    }
                }
            }
            System.out.println("Successful test of " + curve.name);
        }
    }

    public abstract String getName();

    public abstract K getKeys(CurveType curve);

    public abstract H getHelper(HashTypes hash);

    public abstract byte[] sign(byte[] data, K key, H helper);

    public abstract boolean verify(byte[] data, byte[] sig, K key, H helper);
}
