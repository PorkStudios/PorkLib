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

package net.daporkchop.lib.crypto.test.impl.symmetric;

import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.key.serializer.impl.SymmetricKeySerializer;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.crypto.test.TestMain;
import org.junit.Test;

import java.util.Arrays;

public abstract class SymmetricCipherTest<T extends AbstractSymmetricKey, Q extends BlockCipherHelper<T>> {
    @Test
    public void test() {
        System.out.println("Testing " + getType().name + "...");
        T key = genKeys();
        byte[] encoded = SymmetricKeySerializer.INSTANCE.serialize(key);
        key = SymmetricKeySerializer.INSTANCE.<T>deserialize(encoded);
        for (BlockCipherMode mode : BlockCipherMode.values()) {
            if (!mode.isCompatible(getType())) {
                System.out.println("Incompatible cipher + mode combination: " + getType().name + "+" + mode.name);
                continue;
            }
            for (BlockCipherPadding scheme : BlockCipherPadding.values()) {
                Q helper = getHelper(mode, scheme, key);
                for (byte[] b : TestMain.RANDOM_DATA) {
                    //use length prefix to remove padding
                    byte[] encrypted = helper.encrypt(b, true);
                    byte[] decrypted = helper.decrypt(encrypted, true);
                    if (!Arrays.equals(b, decrypted)) {
                        System.out.println("Cipher: " + getType().name + "/" + mode.name + "/" + scheme.name);
                        System.out.println("Original: " + new String(b).replaceAll("\n", ""));
                        System.out.println("Encrypted: " + new String(encrypted).replaceAll("\n", ""));
                        System.out.println("Decrypted: " + new String(decrypted).replaceAll("\n", ""));
                        throw new IllegalStateException("Data didn't match after decrypting.");
                    }
                }
                //System.out.println("Successful test of: " + getType().name + "/" + mode.name + "/" + scheme.name);
            }
        }
    }

    public abstract BlockCipherType getType();

    public abstract Q getHelper(BlockCipherMode mode, BlockCipherPadding scheme, T key);

    public abstract T genKeys();
}
