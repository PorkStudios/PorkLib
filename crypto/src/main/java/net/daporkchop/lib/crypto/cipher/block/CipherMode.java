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
