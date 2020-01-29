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
