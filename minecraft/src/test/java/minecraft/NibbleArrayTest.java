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

package minecraft;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.common.nibble.DirectNibbleArray;
import net.daporkchop.lib.minecraft.format.common.nibble.HeapNibbleArray;
import net.daporkchop.lib.minecraft.format.common.nibble.NibbleArray;
import org.junit.Test;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class NibbleArrayTest {
    @Test
    public void testHeapYZX() {
        this.testYZX(new HeapNibbleArray.YZX());
    }

    @Test
    public void testHeapXZY() {
        this.testXZY(new HeapNibbleArray.XZY());
    }
    
    @Test
    public void testDirectYZX() {
        this.testYZX(new DirectNibbleArray.YZX());
    }

    @Test
    public void testDirectXZY() {
        this.testXZY(new DirectNibbleArray.XZY());
    }
    
    private void testYZX(@NonNull NibbleArray arr)  {
        checkState(arr.get(0) == 0);
        checkState(arr.get(1) == 0);

        arr.set(1, 0, 0, 7);
        checkState(arr.get(0) == 0);
        checkState(arr.get(1) == 7);
        checkState(arr.get(0, 0, 0) == 0);
        checkState(arr.get(1, 0, 0) == 7);

        arr.set(0, 0, 0, 12);
        checkState(arr.get(0) == 12);
        checkState(arr.get(1) == 7);
        checkState(arr.get(0, 0, 0) == 12);
        checkState(arr.get(1, 0, 0) == 7);

        checkState(arr.get(16) == 0);
        checkState(arr.get(17) == 0);

        arr.set(1, 0, 1, 7);
        checkState(arr.get(16) == 0);
        checkState(arr.get(17) == 7);
        checkState(arr.get(0, 0, 1) == 0);
        checkState(arr.get(1, 0, 1) == 7);

        arr.set(0, 0, 1, 12);
        checkState(arr.get(16) == 12);
        checkState(arr.get(17) == 7);
        checkState(arr.get(0, 0, 1) == 12);
        checkState(arr.get(1, 0, 1) == 7);

        arr.close();
    }

    private void testXZY(@NonNull NibbleArray arr) {
        checkState(arr.get(0) == 0);
        checkState(arr.get(1) == 0);

        arr.set(0, 1, 0, 7);
        checkState(arr.get(0) == 0);
        checkState(arr.get(1) == 7);
        checkState(arr.get(0, 0, 0) == 0);
        checkState(arr.get(0, 1, 0) == 7);

        arr.set(0, 0, 0, 12);
        checkState(arr.get(0) == 12);
        checkState(arr.get(1) == 7);
        checkState(arr.get(0, 0, 0) == 12);
        checkState(arr.get(0, 1, 0) == 7);

        checkState(arr.get(16) == 0);
        checkState(arr.get(17) == 0);

        arr.set(0, 1, 1, 7);
        checkState(arr.get(16) == 0);
        checkState(arr.get(17) == 7);
        checkState(arr.get(0, 0, 1) == 0);
        checkState(arr.get(0, 1, 1) == 7);

        arr.set(0, 0, 1, 12);
        checkState(arr.get(16) == 12);
        checkState(arr.get(17) == 7);
        checkState(arr.get(0, 0, 1) == 12);
        checkState(arr.get(0, 1, 1) == 7);

        arr.close();
    }
}
