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

package net.daporkchop.lib.common.misc;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A bunch of random byte arrays for use in test classes.
 * <p>
 * Really shouldn't be used outside of unit tests.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class TestRandomData {
    public static final byte[][] randomBytes = new byte[32][];

    static {
        try {
            Class.forName("org.junit.Test");
        } catch (ClassNotFoundException e)   {
            throw new RuntimeException("JUnit not found! Is this a unit testing environment?", e);
        }
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = randomBytes.length - 1; i >= 0; i--) {
            r.nextBytes(randomBytes[i] = new byte[r.nextInt(1024, 8192)]);
        }
    }

    public static byte[] getRandomBytes(int minLen, int maxLen) {
        return getRandomBytes(ThreadLocalRandom.current().nextInt(minLen, maxLen));
    }

    public static byte[] getRandomBytes(int len) {
        byte[] b = new byte[len];
        ThreadLocalRandom.current().nextBytes(b);
        return b;
    }
}
