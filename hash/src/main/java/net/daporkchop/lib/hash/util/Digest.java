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

package net.daporkchop.lib.hash.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.hash.alg.SHA3;
import net.daporkchop.lib.hash.alg.Whirlpool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * A user-friendly wrapper around {@link DigestAlg}, providing some additional functions
 *
 * @author DaPorkchop_
 */
public class Digest implements BaseDigest {
    public static final Digest MD5 = new Digest(net.daporkchop.lib.hash.alg.MD5::new);
    public static final Digest SHA1 = new Digest(net.daporkchop.lib.hash.alg.SHA1::new);
    public static final Digest SHA_256 = new Digest(net.daporkchop.lib.hash.alg.SHA256::new);
    public static final Digest SHA512 = new Digest(net.daporkchop.lib.hash.alg.SHA512::new);
    public static final Digest SHA3_256 = new Digest(net.daporkchop.lib.hash.alg.SHA3::new);
    public static final Digest SHA3_512 = new Digest(() -> new SHA3(512));
    public static final Digest WHIRLPOOL = new Digest(Whirlpool::new);

    private final ThreadLocal<DigestAlg> digestCache;
    @Getter
    private final String algorithmName;
    @Getter
    private final int hashSize;
    @Getter
    private final int internalBufferSize;
    @Getter
    private final Supplier<DigestAlg> supplier;

    public Digest(@NonNull Supplier<DigestAlg> supplier) {
        this.supplier = supplier;
        this.digestCache = ThreadLocal.withInitial(supplier);
        DigestAlg alg = this.digestCache.get();
        this.algorithmName = alg.getAlgorithmName();
        this.hashSize = alg.getHashSize();
        this.internalBufferSize = alg.getInternalBufferSize();
    }

    public Digester start() {
        return new Digester(this.digestCache.get());
    }

    public Digester start(@NonNull byte[] hashOut)  {
        if (hashOut.length < this.hashSize) {
            throw new IllegalStateException(String.format("Hash size must be at least %d bytes, but found %d!", this.hashSize, hashOut.length));
        }
        return new Digester(this.digestCache.get(), hashOut);
    }

    public Hash hash()  {
        return new Digester(this.digestCache.get()).hash();
    }

    public Hash hash(@NonNull byte[] data)  {
        return new Digester(this.digestCache.get())
                .append(data)
                .hash();
    }

    public Hash hash(@NonNull byte[]... data)  {
        Digester digester = new Digester(this.digestCache.get());
        for (byte[] b : data)   {
            digester.append(b);
        }
        return digester.hash();
    }

    public Hash hash(@NonNull File file) throws IOException {
        return new Digester(this.digestCache.get())
                .append(file)
                .hash();
    }

    public Hash hash(@NonNull InputStream in) throws IOException {
        return new Digester(this.digestCache.get())
                .append(in)
                .hash();
    }
}
