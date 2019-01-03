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

package binary;import net.daporkchop.lib.binary.stream.bit.NBitArray;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class TestNBitArray {
    @Test
    public void test() {
        int[] reference = new int[1024];
        for (int i = 0; i < reference.length; i++) {
            reference[i] = ThreadLocalRandom.current().nextInt();
        }

        for (int i = 1; i < 32; i++) {
            int mask = (1 << i) - 1;
            NBitArray array = new NBitArray(1024, i);
            for (int j = 0; j < reference.length; j++) {
                array.set(j, reference[j] & mask);
            }
            for (int j = 0; j < reference.length; j++) {
                int a = array.get(j);
                int b = reference[j] & mask;
                if (a != b) {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
