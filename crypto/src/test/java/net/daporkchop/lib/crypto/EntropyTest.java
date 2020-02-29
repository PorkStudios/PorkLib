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

package net.daporkchop.lib.crypto;

import net.daporkchop.lib.common.misc.TestRandomData;
import net.daporkchop.lib.crypto.keygen.EntropyPool;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author DaPorkchop_
 */
public class EntropyTest {
    @Test
    public void test() {
        long buffer = 1L << 8L;

        Arrays.stream(TestRandomData.randomBytes).parallel().forEach(b -> {
            EntropyPool pool = new EntropyPool(buffer);
            pool.update(b);
            byte[] randomness1 = pool.get(16, new Random(1234567890L));
            pool = new EntropyPool(buffer);
            pool.update(b);
            byte[] randomness2 = pool.get(16, new Random(1234567890L));
            if (!Arrays.equals(randomness1, randomness2))   {
                throw new IllegalStateException("invalid data!");
            }
        });
    }
}
