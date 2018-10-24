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

package net.daporkchop.lib.crypto.cipher.block;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum CipherMode {
    CBC("CBC",
            (cipher, integer) -> new CBCBlockCipher(cipher),
            null),
    OFB("OFB",
            OFBBlockCipher::new,
            null),
    CFB("CFB",
            CFBBlockCipher::new,
            null),
    OPENPGP_CFB("OpenPGPCFB",
            (cipher, integer) -> new OpenPGPCFBBlockCipher(cipher),
            ParametersWithIV::getParameters);

    public final String name;
    private final BiFunction<BlockCipher, Integer, BlockCipher> cipherFunction;
    private final Function<ParametersWithIV, CipherParameters> parametersFunction;

    CipherMode(String name,
               BiFunction<BlockCipher, Integer, BlockCipher> cipherFunction,
               Function<ParametersWithIV, CipherParameters> parametersFunction,
               CipherType... incompatible) {
        this.name = name.intern();
        this.cipherFunction = cipherFunction;
        this.parametersFunction = parametersFunction == null ? parameters -> parameters : parametersFunction;
    }

    public BlockCipher wrap(BlockCipher cipher) {
        return this.cipherFunction.apply(cipher, cipher.getBlockSize());
    }

    public CipherParameters getParametersFromKey(ParametersWithIV parameters) {
        return this.parametersFunction.apply(parameters);
    }
}
