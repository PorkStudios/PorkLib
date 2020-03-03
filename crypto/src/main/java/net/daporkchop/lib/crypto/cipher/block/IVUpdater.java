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
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.hash.util.Digest;

import java.util.function.Consumer;

/**
 * A function that updates a block cipher's IV (initialization vector) before initialization
 *
 * @author DaPorkchop_
 */
public interface IVUpdater extends Consumer<byte[]> {
    IVUpdater SHA_256 = ofHash(Digest.SHA_256);
    IVUpdater SHA3_256 = ofHash(Digest.SHA3_256);

    static IVUpdater ofHash(@NonNull Digest digest) {
        ThreadRef<byte[]> cache = ThreadRef.soft(() -> new byte[digest.getHashSize()]);
        return iv -> {
            byte[] buf = cache.get();
            for (int i = 0; i < iv.length; i += buf.length) {
                digest.start(buf).append(iv).hash();
                for (int j = 0; j < buf.length && j + i < iv.length; j++) {
                    iv[i + j] = buf[j];
                }
            }
        };
    }

    @Override
    void accept(byte[] iv);
}
