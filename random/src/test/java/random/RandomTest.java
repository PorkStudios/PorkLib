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

package random;

import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;
import net.daporkchop.lib.random.impl.ThreadLocalPRandom;
import org.junit.Assume;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class RandomTest {
    @Test
    public void testRandom()    {
        PRandom r1 = new FastPRandom(1234L);
        PRandom r2 = new FastPRandom(1234L);

        Assume.assumeTrue(r1.nextBoolean() == r2.nextBoolean());
        Assume.assumeTrue(r1.nextInt() == r2.nextInt());
        Assume.assumeTrue(r1.nextLong() == r2.nextLong());
        Assume.assumeTrue(r1.nextFloat() == r2.nextFloat());
        Assume.assumeTrue(r1.nextGaussianFloat() == r2.nextGaussianFloat());
        Assume.assumeTrue(r1.nextDouble() == r2.nextDouble());
        Assume.assumeTrue(r1.nextGaussianDouble() == r2.nextGaussianDouble());

        byte[] b1 = new byte[ThreadLocalPRandom.current().nextInt(1024, 2048)];
        byte[] b2 = b1.clone();
        r1.nextBytes(b1);
        r2.nextBytes(b2);
        Assume.assumeTrue(Arrays.equals(b1, b2));
    }

    @Test
    public void benchmarkRandom()   {
        Object[] rngs =  {
                new FastPRandom(),
                ThreadLocalPRandom.current(),
                new Random(),
                ThreadLocalRandom.current(),
                io.netty.util.internal.ThreadLocalRandom.current()
        };

        final int rounds = 1000000;
        final byte[] arr = new byte[8192];

        System.out.printf("Testing random implementations: generating %d bytes %d times\n", arr.length, rounds);

        Arrays.stream(rngs)
                .map(rng -> {
                    long start = System.currentTimeMillis();
                    if (rng instanceof Random) {
                        Random r = (Random) rng;
                        for (int i = 0; i < rounds; i++)    {
                            r.nextBytes(arr);
                        }
                    } else if (rng instanceof PRandom) {
                        PRandom r = (PRandom) rng;
                        for (int i = 0; i < rounds; i++)    {
                            r.nextBytes(arr);
                        }
                    } else {
                        throw new IllegalArgumentException(PorkUtil.className(rng));
                    }
                    return new Tuple<>(System.currentTimeMillis() - start, PorkUtil.className(rng));
                })
                .sorted(Comparator.comparingLong(Tuple::getA))
                .forEach(tuple -> System.out.printf("%.2fs: %s\n", tuple.getA() / 1000.0d, tuple.getB()));
    }
}
