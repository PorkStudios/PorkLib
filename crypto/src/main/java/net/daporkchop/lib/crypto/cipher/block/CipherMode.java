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

package net.daporkchop.lib.crypto.cipher.block;

import lombok.NonNull;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.PGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * A block cipher can operate with one of many modes. The mode defines how individual blocks are
 * related to each other
 *
 * @author DaPorkchop_
 */
public enum CipherMode {
    CBC("CBC", (cipher, integer) -> new CBCBlockCipher(cipher)),
    OFB("OFB", OFBBlockCipher::new),
    CFB("CFB", CFBBlockCipher::new, true),
    CTR("CTR", (cipher, integer) -> new KCTRBlockCipher(cipher), true),
    PGP_CFB("PGP_CFB", (cipher, integer) -> new PGPCFBBlockCipher(cipher, false)),
    SIC("SIC", (cipher, integer) -> new SICBlockCipher(cipher), true);

    private static final CipherMode[] STREAMABLE_MODES;

    static {
        STREAMABLE_MODES = Arrays.stream(values()).filter(mode -> mode.streamSupported).toArray(CipherMode[]::new);
    }

    public static CipherMode[] streamableModes() {
        return STREAMABLE_MODES;
    }
    public final String name;
    public final boolean streamSupported;
    private final BiFunction<BlockCipher, Integer, BlockCipher> cipherFunction;

    CipherMode(String name, BiFunction<BlockCipher, Integer, BlockCipher> cipherFunction) {
        this(name, cipherFunction, false);
    }

    CipherMode(@NonNull String name, @NonNull BiFunction<BlockCipher, Integer, BlockCipher> cipherFunction, boolean streamSupported) {
        this.name = name.intern();
        this.cipherFunction = cipherFunction;
        this.streamSupported = streamSupported;
    }

    public BlockCipher wrap(@NonNull BlockCipher cipher) {
        return this.cipherFunction.apply(cipher, cipher.getBlockSize());
    }

    public StreamCipher streamify(@NonNull BlockCipher cipher) {
        if (!this.streamSupported) {
            throw new IllegalStateException(String.format("Cipher mode %s cannot be used as a stream!", this.name));
        }
        return (StreamCipher) this.wrap(cipher);
    }
}
