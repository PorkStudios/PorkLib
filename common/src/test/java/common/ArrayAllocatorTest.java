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

package common;

import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.ref.ReferenceType;
import org.junit.Test;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class ArrayAllocatorTest {
    @Test
    public void testAlloc() {
        ArrayAllocator<byte[]> alloc = ArrayAllocator.pow2(byte[]::new, ReferenceType.STRONG, 2);
        byte[] arr; //this is totally unsafe, never do this in real code
        try (Handle<byte[]> handle = alloc.atLeast(31)) {
            arr = handle.get();
            checkState(arr.length == 32, "array length was %d, expected 32", arr.length);
        }

        try (Handle<byte[]> handle = alloc.atLeast(31)) {
            checkState(handle.get() == arr, "31");
        }
        try (Handle<byte[]> handle = alloc.atLeast(32)) {
            checkState(handle.get() == arr, "32");
        }
        try (Handle<byte[]> handle = alloc.atLeast(17)) {
            checkState(handle.get() == arr, "17");
        }
        try (Handle<byte[]> handle = alloc.atLeast(33)) {
            checkState(handle.get() != arr, "33");
            checkState(handle.get().length == 64, "array length was %d, expected 64", handle.get().length);
        }
        try (Handle<byte[]> handle = alloc.atLeast(16)) {
            checkState(handle.get() != arr, "16");
            checkState(handle.get().length == 16, "array length was %d, expected 16", handle.get().length);
        }
    }
}
