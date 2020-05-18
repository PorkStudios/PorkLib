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

package collections;

import lombok.NonNull;
import net.daporkchop.lib.collections.map.map2d.HashCache2d;
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
        Map2d<String> map = new HashCache2d<String>(256) {
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
