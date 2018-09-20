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

package map;

import net.daporkchop.lib.primitive.iterator.IntegerIterator;
import net.daporkchop.lib.primitive.iterator.LongIterator;
import net.daporkchop.lib.primitive.iterator.bi.IntegerLongIterator;
import net.daporkchop.lib.primitive.map.IntegerLongMap;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
public abstract class MapTest {
    @Test
    public void test() {
        IntegerLongMap map = this.newMap();
        for (int i = 0; i < 1000; i++) {
            map.put(i, ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        }
        System.out.println(map.getSize());
        map.forEachEntry((key, value) -> {
            if (key % 250 == 0) System.out.println(key + " " + value);
        });
        {
            IntegerIterator iterator = map.keyIterator();
            int i = 0;
            while (iterator.hasNext()) {
                int key = iterator.advance();
                i++;
            }
            System.out.println("Iterated over " + i + " keys");
            AtomicInteger j = new AtomicInteger(0);
            map.forEachKey(a -> j.incrementAndGet());
            System.out.println("Iterated over " + j.get() + " keys");
        }
        {
            LongIterator iterator = map.valueIterator();
            int i = 0;
            int j = 0;
            while (iterator.hasNext()) {
                i++;
                long value = iterator.advance();
                if (i % 2 == 0 || i % 3 == 0) {
                    j++;
                    iterator.remove();
                }
            }
            System.out.println("Iterated over " + i + " values, removing " + j);
        }
        map.remove(5);
        map.remove(6);
        map.remove(7);
        map.remove(8);
        map.remove(9);
        {
            IntegerLongIterator iterator = map.entryIterator();
            int i = 0;
            while (iterator.hasNext()) {
                iterator.advance();
                iterator.get().getK();
                iterator.get().getV();
                i++;
            }
            System.out.println("Iterated over " + i + " entries (out of " + map.getSize() + ")");
        }
        for (int i = 0; i < 1000; i++) {
            map.put(i + 1000, ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        }
    }

    protected abstract IntegerLongMap newMap();
}
