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

import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.engines.TEAEngine;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.XTEAEngine;

import java.util.function.Supplier;

@AllArgsConstructor
public enum CipherType {
    AES("AES", 16, AESEngine::new),
    ARIA("ARIA", 16, ARIAEngine::new),
    BLOWFISH("BlowFish", 8, BlowfishEngine::new),
    CAMELLIA("Camellia", 16, CamelliaEngine::new),
    CAST5("CAST5", 8, CAST5Engine::new),
    CAST6("CAST6", 16, CAST6Engine::new),
    DES("DES", 8, DESEngine::new),
    DSTU_7624_128("DSTU 7624 (128-bit)", 16, () -> new DSTU7624Engine(128)),
    DSTU_7624_256("DSTU 7624 (256-bit)", 32, () -> new DSTU7624Engine(256)),
    DSTU_7624_512("DSTU 7624 (512-bit)", 64, () -> new DSTU7624Engine(512)),
    //my god these two are slow
    //GOST_3412_2015("GOST 3412_2015", GOST3412_2015Engine::new, 16, 32, 16),
    //GOST_28147("GOST 28147", GOST28147Engine::new, 8, 32, 8),
    IDEA("IDEA", 8, IDEAEngine::new),
    NOEKEON("Noekeon", 16, NoekeonEngine::new),
    RC2("RC2", 8, RC2Engine::new),
    RC6("RC6", 16, RC6Engine::new),
    RIJNDAEL("Rijndael", 16, RijndaelEngine::new),
    SEED("SEED", 16, SEEDEngine::new),
    SERPENT("Serpent", 16, SerpentEngine::new),
    SHACAL2("Shacal2", 32, Shacal2Engine::new),
    SKIPJACK("SKIPJACK", SkipjackEngine::new, 8, 32, 8),
    SM4("SM4", 16, SM4Engine::new),
    TEA("TEA", TEAEngine::new, 8, 16, 8),
    THREEFISH_256("Threefish (256-bit)", 32, () -> new ThreefishEngine(256)),
    THREEFISH_512("Threefish (512-bit)", 64, () -> new ThreefishEngine(512)),
    THREEFISH_1024("Threefish (1024-bit)", 128, () -> new ThreefishEngine(1024)),
    TWOFISH("TwoFish", 16, TwofishEngine::new),
    XTEA("XTEA", XTEAEngine::new, 8, 16, 8),
    NONE("", -1, null);

    public final String name;
    private final Supplier<BlockCipher> cipherSupplier;
    public int blockSize;
    public int keySize;
    public int ivSize;

    CipherType(String name, int size, Supplier<BlockCipher> cipherSupplier) {
        this.name = name;
        this.blockSize = this.ivSize = this.keySize = size;
        this.cipherSupplier = cipherSupplier;
    }

    public BlockCipher create() {
        return this.cipherSupplier.get();
    }
}
