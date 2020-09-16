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

package binary;

import net.daporkchop.lib.binary.bit.BitArray;
import net.daporkchop.lib.binary.bit.packed.PackedBitArray;
import net.daporkchop.lib.binary.bit.padded.PaddedBitArray;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.daporkchop.lib.common.util.PValidation.checkState;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
public class TestBitArray {
    @Test
    public void test() {
        int[] reference = new int[1024];
        for (int i = 0; i < reference.length; i++) {
            reference[i] = ThreadLocalRandom.current().nextInt();
        }

        BiFunction<Integer, Integer, BitArray>[] creators = uncheckedCast(new BiFunction[2]);
        creators[0] = PackedBitArray::new;
        creators[1] = PaddedBitArray::new;

        for (BiFunction<Integer, Integer, BitArray> f : creators) {
            for (int i = 1; i < 32; i++) {
                int mask = (1 << i) - 1;
                try (BitArray array = f.apply(i, reference.length)) {
                    for (int j = 0; j < reference.length; j++) {
                        array.set(j, reference[j] & mask);
                    }
                    for (int j = 0; j < reference.length; j++) {
                        int a = array.get(j);
                        int b = reference[j] & mask;
                        checkState(a == b, "%d != %d @ %d bits with %s", a, b, i, array);
                    }
                }
            }
        }
    }
}
