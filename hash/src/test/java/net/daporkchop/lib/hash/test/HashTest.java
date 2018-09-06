/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.hash.test;

import net.daporkchop.lib.encoding.Hexadecimal;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class HashTest {
    private static final byte[] RANDOM_DATA = new byte[8192];

    static {
        ThreadLocalRandom.current().nextBytes(RANDOM_DATA);
    }

    @Test
    public void aaa_test() {
        System.out.println(getName());
        System.out.println(Hexadecimal.encode(hash(RANDOM_DATA)));
        RANDOM_DATA[0] += 1;
        System.out.println(Hexadecimal.encode(hash(RANDOM_DATA)));
    }

    @Test
    public void aab_testEmpty() {
        System.out.println(Hexadecimal.encode(hash(new byte[0])));
        System.out.println(Hexadecimal.encode(hash(new byte[1])));
        System.out.println(Hexadecimal.encode(hash(new byte[2])));
    }

    protected abstract byte[] hash(byte[] b);

    protected abstract String getName();
}
