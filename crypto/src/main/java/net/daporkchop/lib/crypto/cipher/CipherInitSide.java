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

package net.daporkchop.lib.crypto.cipher;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.hash.util.Digest;

import java.util.function.BiFunction;

/**
 * Used for setting a starting IV when initializing a cipher.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public enum CipherInitSide {
    SERVER((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    CLIENT((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    /**
     * Should only be used if data is to be read and written in the same direction (e.g. for encrypting a file)
     */
    ONE_WAY((iv, recv) -> iv.clone());

    @NonNull
    public final BiFunction<byte[], Boolean, byte[]> ivSetter;
}
