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

package net.daporkchop.lib.crypto.cipher.symmetric.iv;

import net.daporkchop.lib.hash.HashAlg;

import java.util.function.Supplier;

/**
 * A list of all supported IV updater modes
 *
 * @author DaPorkchop_
 */
public enum UpdaterMode {
    NONE(NoneUpdater::new),
    HASH_MD2(() -> new HashUpdater(HashAlg.MD2)),
    HASH_MD4(() -> new HashUpdater(HashAlg.MD4)),
    HASH_MD5(() -> new HashUpdater(HashAlg.MD5)),
    HASH_RIPEMD_128(() -> new HashUpdater(HashAlg.RIPEMD_128)),
    HASH_RIPEMD_160(() -> new HashUpdater(HashAlg.RIPEMD_160)),
    HASH_SHA_160(() -> new HashUpdater(HashAlg.SHA_160)),
    HASH_SHA_256(() -> new HashUpdater(HashAlg.SHA_256)),
    HASH_SHA_384(() -> new HashUpdater(HashAlg.SHA_384)),
    HASH_SHA_512(() -> new HashUpdater(HashAlg.SHA_512)),
    INCREMENT(IncrementUpdater::new);

    private final Supplier<IVUpdater> supplier;

    UpdaterMode(Supplier<IVUpdater> supplier) {
        this.supplier = supplier;
    }

    public IVUpdater getUpdater() {
        return this.supplier.get();
    }
}
