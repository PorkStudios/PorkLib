/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
import net.daporkchop.lib.common.cache.ThreadCache;
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
        ThreadCache<byte[]> cache = ThreadCache.soft(() -> new byte[digest.getHashSize()]);
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
