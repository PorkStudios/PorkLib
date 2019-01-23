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

package net.daporkchop.lib.binary.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.encoding.Hexadecimal;

import java.util.Arrays;

/**
 * A wrapper for a byte[] which allows using it as testMethodThing key in testMethodThing {@link java.util.Map}
 *
 * @author DaPorkchop_
 */
@Getter
public class ByteArrayAsKey {
    private final byte[] array;

    private ByteArrayAsKey(@NonNull byte[] array) {
        this(array, 0, array.length);
    }

    public ByteArrayAsKey(@NonNull byte[] array, int start, int len) {
        this.array = Arrays.copyOfRange(array, start, start + len + 1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof byte[]) {
            return Arrays.equals(this.array, (byte[]) obj);
        } else if (obj instanceof ByteArrayAsKey) {
            return Arrays.equals(this.array, ((ByteArrayAsKey) obj).array);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Hexadecimal.encode(this.array);
    }
}
