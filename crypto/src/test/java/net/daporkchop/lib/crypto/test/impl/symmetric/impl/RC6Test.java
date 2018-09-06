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

package net.daporkchop.lib.crypto.test.impl.symmetric.impl;

import net.daporkchop.lib.crypto.cipher.impl.symmetric.RC6Helper;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.key.symmetric.impl.RC6Key;
import net.daporkchop.lib.crypto.keygen.symmetric.RC6KeyGen;
import net.daporkchop.lib.crypto.test.impl.symmetric.SymmetricCipherTest;

public class RC6Test extends SymmetricCipherTest<RC6Key, RC6Helper> {
    @Override
    public BlockCipherType getType() {
        return BlockCipherType.RC6;
    }

    @Override
    public RC6Helper getHelper(BlockCipherMode mode, BlockCipherPadding scheme, RC6Key key) {
        return new RC6Helper(mode, scheme, key);
    }

    @Override
    public RC6Key genKeys() {
        return RC6KeyGen.gen();
    }
}
