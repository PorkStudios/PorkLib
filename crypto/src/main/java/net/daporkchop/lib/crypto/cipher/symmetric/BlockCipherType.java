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

package net.daporkchop.lib.crypto.cipher.symmetric;

import net.daporkchop.lib.crypto.cipher.impl.symmetric.AESHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.ARIAHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.BlowFishHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.CAST5Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.CAST6Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.CamelliaHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.DESHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.DSTU_7624_128Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.DSTU_7624_256Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.DSTU_7624_512Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.GOST_28147Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.GOST_3412_2015Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.IDEAHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.NoekeonHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.PorkCrypt2Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.RC2Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.RC6Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.RijndaelHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.SEEDHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.SKIPJACKHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.SM4Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.SerpentHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.Shacal2Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.TEAHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.Threefish_1024Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.Threefish_256Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.Threefish_512Helper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.TwoFishHelper;
import net.daporkchop.lib.crypto.cipher.impl.symmetric.XTEAHelper;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.engine.symmetric.PorkCrypt2Engine;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.AESKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.ARIAKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.BlowFishKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.CAST5Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.CAST6Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.CamelliaKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.DESKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.DSTU_7624_128Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.DSTU_7624_256Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.DSTU_7624_512Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.GOST_28147Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.GOST_3412_2015Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.IDEAKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.NoekeonKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.PorkCrypt2Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.RC2Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.RC6Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.RijndaelKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.SEEDKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.SKIPJACKKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.SM4Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.SerpentKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.Shacal2Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.TEAKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.Threefish_1024Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.Threefish_256Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.Threefish_512Key;
import net.daporkchop.lib.crypto.key.symmetric.impl.TwoFishKey;
import net.daporkchop.lib.crypto.key.symmetric.impl.XTEAKey;
import net.daporkchop.lib.crypto.keygen.symmetric.AESKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.ARIAKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.BlowFishKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.CAST5KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.CAST6KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.CamelliaKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.DESKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.DSTU_7624_128KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.DSTU_7624_256KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.DSTU_7624_512KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.GOST_28147KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.GOST_3412_2015KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.IDEAKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.NoekeonKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.PorkCrypt2KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.RC2KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.RC6KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.RijndaelKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.SEEDKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.SKIPJACKKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.SM4KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.SerpentKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.Shacal2KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.TEAKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.Threefish_1024KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.Threefish_256KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.Threefish_512KeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.TwoFishKeyGen;
import net.daporkchop.lib.crypto.keygen.symmetric.XTEAKeyGen;
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

import javax.crypto.SecretKey;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum BlockCipherType {
    AES("AES",
            AESEngine::new,
            16,
            AESKeyGen::gen,
            (a, b, c) -> new AESHelper(a, b, (AESKey) c),
            AESKey::new),
    ARIA("ARIA",
            ARIAEngine::new,
            16,
            ARIAKeyGen::gen,
            (a, b, c) -> new ARIAHelper(a, b, (ARIAKey) c),
            ARIAKey::new),
    BLOWFISH("BlowFish",
            BlowfishEngine::new,
            8,
            BlowFishKeyGen::gen,
            (a, b, c) -> new BlowFishHelper(a, b, (BlowFishKey) c),
            BlowFishKey::new),
    CAMELLIA("Camellia",
            CamelliaEngine::new,
            16,
            CamelliaKeyGen::gen,
            (a, b, c) -> new CamelliaHelper(a, b, (CamelliaKey) c),
            CamelliaKey::new),
    CAST5("CAST5",
            CAST5Engine::new,
            8,
            CAST5KeyGen::gen,
            (a, b, c) -> new CAST5Helper(a, b, (CAST5Key) c),
            CAST5Key::new),
    CAST6("CAST6",
            CAST6Engine::new,
            16,
            CAST6KeyGen::gen,
            (a, b, c) -> new CAST6Helper(a, b, (CAST6Key) c),
            CAST6Key::new),
    DES("DES",
            DESEngine::new,
            8,
            DESKeyGen::gen,
            (a, b, c) -> new DESHelper(a, b, (DESKey) c),
            DESKey::new),
    DSTU_7624_128("DSTU 7624 (128-bit)",
            () -> new DSTU7624Engine(128),
            16,
            DSTU_7624_128KeyGen::gen,
            (a, b, c) -> new DSTU_7624_128Helper(a, b, (DSTU_7624_128Key) c),
            DSTU_7624_128Key::new),
    DSTU_7624_256("DSTU 7624 (256-bit)",
            () -> new DSTU7624Engine(256),
            32,
            DSTU_7624_256KeyGen::gen,
            (a, b, c) -> new DSTU_7624_256Helper(a, b, (DSTU_7624_256Key) c),
            DSTU_7624_256Key::new),
    DSTU_7624_512("DSTU 7624 (512-bit)",
            () -> new DSTU7624Engine(512),
            64,
            DSTU_7624_512KeyGen::gen,
            (a, b, c) -> new DSTU_7624_512Helper(a, b, (DSTU_7624_512Key) c),
            DSTU_7624_512Key::new),
    GOST_3412_2015("GOST 3412_2015",
            GOST3412_2015Engine::new,
            16,
            GOST_3412_2015KeyGen::gen,
            (a, b, c) -> new GOST_3412_2015Helper(a, b, (GOST_3412_2015Key) c),
            GOST_3412_2015Key::new),
    GOST_28147("GOST 28147",
            GOST28147Engine::new,
            8,
            GOST_28147KeyGen::gen,
            (a, b, c) -> new GOST_28147Helper(a, b, (GOST_28147Key) c),
            GOST_28147Key::new),
    IDEA("IDEA",
            IDEAEngine::new,
            8,
            IDEAKeyGen::gen,
            (a, b, c) -> new IDEAHelper(a, b, (IDEAKey) c),
            IDEAKey::new),
    NOEKEON("Noekeon",
            NoekeonEngine::new,
            16,
            NoekeonKeyGen::gen,
            (a, b, c) -> new NoekeonHelper(a, b, (NoekeonKey) c),
            NoekeonKey::new),
    PORKCRYPT2("PorkCrypt2",
            PorkCrypt2Engine::new,
            16,
            PorkCrypt2KeyGen::gen,
            (a, b, c) -> new PorkCrypt2Helper(a, b, (PorkCrypt2Key) c),
            PorkCrypt2Key::new),
    RC2("RC2",
            RC2Engine::new,
            8,
            RC2KeyGen::gen,
            (a, b, c) -> new RC2Helper(a, b, (RC2Key) c),
            RC2Key::new),
    RC6("RC6",
            RC6Engine::new,
            16,
            RC6KeyGen::gen,
            (a, b, c) -> new RC6Helper(a, b, (RC6Key) c),
            RC6Key::new),
    RIJNDAEL("Rijndael",
            RijndaelEngine::new,
            16,
            RijndaelKeyGen::gen,
            (a, b, c) -> new RijndaelHelper(a, b, (RijndaelKey) c),
            RijndaelKey::new),
    SEED("SEED",
            SEEDEngine::new,
            16,
            SEEDKeyGen::gen,
            (a, b, c) -> new SEEDHelper(a, b, (SEEDKey) c),
            SEEDKey::new),
    SERPENT("Serpent",
            SerpentEngine::new,
            16,
            SerpentKeyGen::gen,
            (a, b, c) -> new SerpentHelper(a, b, (SerpentKey) c),
            SerpentKey::new),
    SHACAL2("Shacal2",
            Shacal2Engine::new,
            32,
            Shacal2KeyGen::gen,
            (a, b, c) -> new Shacal2Helper(a, b, (Shacal2Key) c),
            Shacal2Key::new),
    SKIPJACK("SKIPJACK",
            SkipjackEngine::new,
            8,
            SKIPJACKKeyGen::gen,
            (a, b, c) -> new SKIPJACKHelper(a, b, (SKIPJACKKey) c),
            SKIPJACKKey::new),
    SM4("SM4",
            SM4Engine::new,
            16,
            SM4KeyGen::gen,
            (a, b, c) -> new SM4Helper(a, b, (SM4Key) c),
            SM4Key::new),
    TEA("TEA",
            TEAEngine::new,
            8,
            TEAKeyGen::gen,
            (a, b, c) -> new TEAHelper(a, b, (TEAKey) c),
            TEAKey::new),
    THREEFISH_256("Threefish (256-bit)",
            () -> new ThreefishEngine(256),
            32,
            Threefish_256KeyGen::gen,
            (a, b, c) -> new Threefish_256Helper(a, b, (Threefish_256Key) c),
            Threefish_256Key::new),
    THREEFISH_512("Threefish (512-bit)",
            () -> new ThreefishEngine(512),
            64,
            Threefish_512KeyGen::gen,
            (a, b, c) -> new Threefish_512Helper(a, b, (Threefish_512Key) c),
            Threefish_512Key::new),
    THREEFISH_1024("Threefish (1024-bit)",
            () -> new ThreefishEngine(1024),
            128,
            Threefish_1024KeyGen::gen,
            (a, b, c) -> new Threefish_1024Helper(a, b, (Threefish_1024Key) c),
            Threefish_1024Key::new),
    TWOFISH("TwoFish",
            TwofishEngine::new,
            16,
            TwoFishKeyGen::gen,
            (a, b, c) -> new TwoFishHelper(a, b, (TwoFishKey) c),
            TwoFishKey::new),
    XTEA("XTEA",
            XTEAEngine::new,
            8,
            XTEAKeyGen::gen,
            (a, b, c) -> new XTEAHelper(a, b, (XTEAKey) c),
            XTEAKey::new),
    NONE("", null, -1, null, null, null);

    public final String name;
    public final int blockSize;
    public final Function<byte[], ? extends AbstractSymmetricKey> keyGen;
    public final BiFunction<SecretKey, byte[], AbstractSymmetricKey> keyInstance;
    private final Supplier<BlockCipher> cipherSupplier;
    private final TriFunction<BlockCipherMode, BlockCipherPadding, AbstractSymmetricKey, BlockCipherHelper> helperSupplier;

    BlockCipherType(String name, Supplier<BlockCipher> cipherSupplier, int blockSize, Function<byte[], ? extends AbstractSymmetricKey> keyGen, TriFunction<BlockCipherMode, BlockCipherPadding, AbstractSymmetricKey, BlockCipherHelper> helperSupplier, BiFunction<SecretKey, byte[], AbstractSymmetricKey> keyInstance) {
        this.name = name.intern();
        this.cipherSupplier = cipherSupplier;
        this.blockSize = blockSize;
        this.keyGen = keyGen;
        this.helperSupplier = helperSupplier;
        this.keyInstance = keyInstance;
    }

    public BlockCipher create() {
        return this.cipherSupplier.get();
    }

    public <T extends AbstractSymmetricKey> BlockCipherHelper createHelper(BlockCipherMode mode, BlockCipherPadding padding, T key) {
        return this.helperSupplier.apply(mode, padding, key);
    }

    private interface TriFunction<A, B, C, Z> {
        Z apply(A a, B b, C c);
    }
}
