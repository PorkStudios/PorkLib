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

package net.daporkchop.lib.crypto.cipher.impl.symmetric;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.iv.UpdaterMode;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.PaddingScheme;
import net.daporkchop.lib.crypto.key.symmetric.impl.Threefish_1024Key;

public class Threefish_1024Helper extends BlockCipherHelper<Threefish_1024Key> {
    public Threefish_1024Helper(@NonNull BlockCipherMode mode, @NonNull PaddingScheme scheme, @NonNull Threefish_1024Key key) {
        this(mode, scheme, key, UpdaterMode.HASH_SHA_256);
    }

    public Threefish_1024Helper(@NonNull BlockCipherMode mode, @NonNull PaddingScheme scheme, @NonNull Threefish_1024Key key, @NonNull UpdaterMode updater) {
        super(BlockCipherType.THREEFISH_1024, mode, scheme, key, updater);
    }
}
