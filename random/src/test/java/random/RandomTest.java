/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package random;

import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastJavaPRandom;
import net.daporkchop.lib.random.impl.FastPRandom;
import net.daporkchop.lib.random.impl.ThreadLocalPRandom;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class RandomTest {
    private static final ThreadLocal<byte[]> BENCHMARK_CACHE = ThreadLocal.withInitial(() -> new byte[8192]);
    private static final int BENCHMARK_ROUNDS = 1000000 / 10;

    @Test
    public void testRandom()    {
        PRandom r1 = new FastPRandom(1234L);
        PRandom r2 = new FastPRandom(1234L);

        Assert.assertEquals(r1.nextBoolean(), r2.nextBoolean());
        Assert.assertEquals(r1.nextInt(), r2.nextInt());
        Assert.assertEquals(r1.nextLong(), r2.nextLong());
        Assert.assertEquals(r1.nextFloat(), r2.nextFloat(), 0.0f);
        Assert.assertEquals(r1.nextGaussianFloat(), r2.nextGaussianFloat(), 0.0f);
        Assert.assertEquals(r1.nextDouble(), r2.nextDouble(), 0.0d);
        Assert.assertEquals(r1.nextGaussianDouble(), r2.nextGaussianDouble(), 0.0d);

        byte[] b1 = new byte[ThreadLocalPRandom.current().nextInt(1024, 2048)];
        byte[] b2 = b1.clone();
        r1.nextBytes(b1);
        r2.nextBytes(b2);
        Assert.assertArrayEquals(b1, b2);
    }

    @Test
    public void testFastJavaRandomCorrect()    {
        PRandom r1 = new FastJavaPRandom(1234L);
        Random r2 = new Random(1234L);

        Assert.assertEquals(r1.nextBoolean(), r2.nextBoolean());
        Assert.assertEquals(r1.nextInt(), r2.nextInt());
        Assert.assertEquals(r1.nextLong(), r2.nextLong());
        Assert.assertEquals(r1.nextFloat(), r2.nextFloat(), 0.0f);
        Assert.assertEquals(r1.nextGaussianFloat(), (float) r2.nextGaussian(), 0.0f);
        Assert.assertEquals(r1.nextDouble(), r2.nextDouble(), 0.0d);
        Assert.assertEquals(r1.nextGaussianDouble(), r2.nextGaussian(), 0.0d);

        byte[] b1 = new byte[ThreadLocalPRandom.current().nextInt(1024, 2048)];
        byte[] b2 = b1.clone();
        r1.nextBytes(b1);
        r2.nextBytes(b2);
        Assert.assertArrayEquals(b1, b2);
    }

    @Test
    public void benchmarkRandom()   {
        Object[] rngs =  {
                ThreadLocalPRandom.current(),
                new Random(),
                ThreadLocalRandom.current(),
                new FastPRandom(),
                new FastJavaPRandom()
        };

        System.out.printf("Testing random implementations: generating %d bytes %d times\n", BENCHMARK_CACHE.get().length, BENCHMARK_ROUNDS);

        System.out.println("Initial warmup phase...");

        Arrays.stream(rngs).parallel().forEach(RandomTest::doTest);

        System.out.println("Benchmarking...");

        Arrays.stream(rngs).parallel()
                .map(rng -> {
                    long start = System.currentTimeMillis();
                    doTest(rng);
                    return new Tuple<>(System.currentTimeMillis() - start, PorkUtil.className(rng));
                })
                .sorted(Comparator.comparingLong(Tuple::getA))
                .collect(Collectors.toList())
                .forEach(tuple -> System.out.printf("%.2fs: %s\n", tuple.getA() / 1000.0d, tuple.getB()));
    }

    private static void doTest(Object rng)  {
        final byte[] arr = BENCHMARK_CACHE.get();
        if (rng instanceof Random) {
            final Random r = (Random) rng;
            for (int i = 0; i < BENCHMARK_ROUNDS; i++)    {
                r.nextBytes(arr);
            }
        } else if (rng instanceof PRandom) {
            final PRandom r = (PRandom) rng;
            for (int i = 0; i < BENCHMARK_ROUNDS; i++)    {
                r.nextBytes(arr);
            }
        } else {
            throw new IllegalArgumentException(PorkUtil.className(rng));
        }
    }
}
