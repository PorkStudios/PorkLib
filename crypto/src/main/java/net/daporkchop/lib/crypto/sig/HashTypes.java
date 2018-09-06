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

package net.daporkchop.lib.crypto.sig;

import net.daporkchop.lib.hash.helper.TigerHelper;
import net.daporkchop.lib.hash.helper.WhirlpoolHelper;
import net.daporkchop.lib.hash.helper.md.MD2Helper;
import net.daporkchop.lib.hash.helper.md.MD4Helper;
import net.daporkchop.lib.hash.helper.md.MD5Helper;
import net.daporkchop.lib.hash.helper.ripemd.RipeMD128Helper;
import net.daporkchop.lib.hash.helper.ripemd.RipeMD160Helper;
import net.daporkchop.lib.hash.helper.sha.Sha256Helper;
import net.daporkchop.lib.hash.helper.sha.Sha384Helper;
import net.daporkchop.lib.hash.helper.sha.Sha512Helper;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;

import java.util.function.Function;
import java.util.function.Supplier;

public enum HashTypes {
    MD2(MD2Helper::md2, MD2Digest::new),
    MD4(MD4Helper::md4, MD4Digest::new),
    MD5(MD5Helper::md5, MD5Digest::new),
    RIPEMD128(RipeMD128Helper::ripeMD128, RIPEMD128Digest::new),
    RIPEMD160(RipeMD160Helper::ripeMD160, RIPEMD160Digest::new),
    TIGER(TigerHelper::tiger, TigerDigest::new),
    SHA_256(Sha256Helper::sha256, SHA256Digest::new),
    SHA_384(Sha384Helper::sha384, SHA384Digest::new),
    SHA_512(Sha512Helper::sha512, SHA512Digest::new),
    WHIRLPOOL(WhirlpoolHelper::whirlpool, WhirlpoolDigest::new);

    private final Function<byte[], byte[]> hash;
    private final Supplier<Digest> supplier;

    HashTypes(Function<byte[], byte[]> hash, Supplier<Digest> supplier) {
        this.hash = hash;
        this.supplier = supplier;
    }

    public byte[] hash(byte[] in) {
        if (in == null) throw new IllegalArgumentException("Cannot hash null!");
        return hash.apply(in);
    }

    public Digest getAsDigest() {
        return supplier.get();
    }
}
