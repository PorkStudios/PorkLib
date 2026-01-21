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
