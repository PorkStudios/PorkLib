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

package net.daporkchop.lib.hash.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.encoding.basen.Base58;

import java.util.Arrays;
import java.util.Base64;

/**
 * The result value of a hash function
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class Hash {
    @NonNull
    private final byte[] hash;

    public String toHex() {
        return Hexadecimal.encode(this.hash);
    }

    public String toBase64() {
        return Base64.getEncoder().encodeToString(this.hash);
    }

    public String toBase58() {
        return Base58.encodeBase58(this.hash);
    }

    public String toBase58WithHeaders(byte version, @NonNull String prefix) {
        return Base58WithHeaders.encode(version, prefix, this.hash);
    }

    @Override
    public String toString() {
        return String.format("Hash(%s)", Hexadecimal.encode(this.hash));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.hash);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Hash) {
            obj = ((Hash) obj).hash;
        }
        return obj instanceof byte[] && Arrays.equals(this.hash, (byte[]) obj);
    }
}
