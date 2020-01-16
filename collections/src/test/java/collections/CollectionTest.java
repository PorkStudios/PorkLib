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

package collections;

import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.impl.list.BigLinkedList;
import net.daporkchop.lib.collections.impl.list.PArrayList;
import net.daporkchop.lib.logging.Logging;
import org.junit.Test;

import java.util.function.Supplier;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class CollectionTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testLists() {
        for (PList<Integer> list : (PList<Integer>[]) new PList[]   {
                new BigLinkedList<>(),
                new PArrayList() //TODO: doesn't work
        })  {
            logger.info("Testing %s...", list.getClass());
            for (int i = 8192; i > 0; i--)  {
                list.add(i);
            }
            assert !list.isEmpty();
            assert list.size() == 8192L;
            assert list.contains(7);
            assert list.contains(8192);
            assert !list.contains(0);
            assert !list.contains(8193);
            for (int i = 64; i > 32; i--)  {
                list.remove((Integer) i);
            }
            assert list.size() == 8192L - 32L;
            assert list.contains(7);
            assert list.contains(8192);
            assert !list.contains(0);
            assert !list.contains(8193);
            assert list.contains(32);
            assert list.contains(65);
            assert !list.contains(33);
            assert !list.contains(64);

            {
                long l = 0L;
                for (PIterator<Integer> iterator = list.iterator(); iterator.hasNext(); l++)    {
                    int val = iterator.next();
                    if (val == 1000)    {
                        iterator.set(9001); //it's over 9000!
                    } else if (val >= 2000 && val <= 2500)    {
                        iterator.recompute(i -> i == 2250 ? 9002 : i);
                    }
                }
                assert l == list.size();
                assert !list.contains(1000);
                assert list.contains(9001);
                assert list.contains(2000);
                assert list.contains(2500);
                assert list.contains(9002);
                assert !list.contains(2250);
            }

            long index = list.indexOf(9001);
            assert index != -1L;
            list.add(index - 25L, 0);
            assert list.indexOf(9001) == index + 1L;
            assert list.indexOf(0) == index - 25L;
        }
    }
}
