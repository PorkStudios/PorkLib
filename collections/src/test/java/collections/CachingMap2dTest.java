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

import lombok.NonNull;
import net.daporkchop.lib.collections.map.map2d.CachingMap2d;
import net.daporkchop.lib.collections.map.map2d.Map2d;
import net.daporkchop.lib.math.vector.i.Vec2i;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author DaPorkchop_
 */
public class CachingMap2dTest {
    @Test
    public void test()  {
        Collection<Vec2i> evictedPositions = new HashSet<>();
        Map2d<String> map = new CachingMap2d<String>(256) {
            @Override
            protected void onEvicted(int x, int y, @NonNull String value) {
                if (!evictedPositions.add(new Vec2i(x, y))) {
                    throw new IllegalStateException(x + "," + y);
                }
            }
        };

        assert map.put(0, 0, "a") == null;
        assert map.put(1, 0, "a") == null;
        assert map.size() == 2;
        assert map.put(0, 0, "b") == "a";
        assert map.size() == 2;
        assert map.putIfAbsent(0, 0, "b") == "b";
        assert map.putIfAbsent(0, 0, "b") == "b";
        assert map.size() == 2;
        assert map.remove(1, 0) == "a";
        assert map.remove(1, 0) == null;
        assert map.size() == 1;

        for (int y = 1;; y++)   {
            String val = map.putIfAbsent(0, y, "c");
            assert val == null || val == "c";
            if (map.get(0, 0) != "b")  {
                System.out.printf("Found collision: (0,0), (0,%d)\n", y);
                assert y == 70;
                break;
            }
        }
    }
}
