/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package unsafe;

import net.daporkchop.lib.unsafe.PUnsafe;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author DaPorkchop_
 */
public class UnsafeTest {
    @Test
    public void testAllocateUninitializedArray() {
        for (int len = 0; len < 3; len++) {
            assertEquals("boolean", PUnsafe.allocateUninitializedArray(boolean.class, len).getClass(), boolean[].class);
            assertEquals("byte", PUnsafe.allocateUninitializedArray(byte.class, len).getClass(), byte[].class);
            assertEquals("short", PUnsafe.allocateUninitializedArray(short.class, len).getClass(), short[].class);
            assertEquals("char", PUnsafe.allocateUninitializedArray(char.class, len).getClass(), char[].class);
            assertEquals("int", PUnsafe.allocateUninitializedArray(int.class, len).getClass(), int[].class);
            assertEquals("long", PUnsafe.allocateUninitializedArray(long.class, len).getClass(), long[].class);
            assertEquals("float", PUnsafe.allocateUninitializedArray(float.class, len).getClass(), float[].class);
            assertEquals("double", PUnsafe.allocateUninitializedArray(double.class, len).getClass(), double[].class);

            assertEquals("boolean", ((boolean[]) PUnsafe.allocateUninitializedArray(boolean.class, len)).length, len);
            assertEquals("byte", ((byte[]) PUnsafe.allocateUninitializedArray(byte.class, len)).length, len);
            assertEquals("short", ((short[]) PUnsafe.allocateUninitializedArray(short.class, len)).length, len);
            assertEquals("char", ((char[]) PUnsafe.allocateUninitializedArray(char.class, len)).length, len);
            assertEquals("int", ((int[]) PUnsafe.allocateUninitializedArray(int.class, len)).length, len);
            assertEquals("long", ((long[]) PUnsafe.allocateUninitializedArray(long.class, len)).length, len);
            assertEquals("float", ((float[]) PUnsafe.allocateUninitializedArray(float.class, len)).length, len);
            assertEquals("double", ((double[]) PUnsafe.allocateUninitializedArray(double.class, len)).length, len);

            assertEquals("boolean", PUnsafe.allocateUninitializedBooleanArray(len).length, len);
            assertEquals("byte", PUnsafe.allocateUninitializedByteArray(len).length, len);
            assertEquals("short", PUnsafe.allocateUninitializedShortArray(len).length, len);
            assertEquals("char", PUnsafe.allocateUninitializedCharArray(len).length, len);
            assertEquals("int", PUnsafe.allocateUninitializedIntArray(len).length, len);
            assertEquals("long", PUnsafe.allocateUninitializedLongArray(len).length, len);
            assertEquals("float", PUnsafe.allocateUninitializedFloatArray(len).length, len);
            assertEquals("double", PUnsafe.allocateUninitializedDoubleArray(len).length, len);
        }
    }
}
