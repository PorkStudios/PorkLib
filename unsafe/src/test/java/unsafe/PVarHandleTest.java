/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package unsafe;

import net.daporkchop.lib.unsafe.PVarHandle;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * @author DaPorkchop_
 */
public class PVarHandleTest {
    public int intPlain;
    public volatile int intVolatile;

    @Test
    public void test() throws Exception {
        PVarHandle handle_intPlain = PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "intPlain", int.class);
        PVarHandle handle_intVolatile = PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "intVolatile", int.class);
        MethodHandle plainGetter_intPlain = handle_intPlain.getPlainInvoker();
        MethodHandle plainGetter_intVolatile = handle_intVolatile.getPlainInvoker();
        MethodHandle volatileGetter_intPlain = handle_intPlain.getVolatileInvoker();
        MethodHandle volatileGetter_intVolatile = handle_intVolatile.getVolatileInvoker();
        MethodHandle acquireGetter_intPlain = handle_intPlain.getAcquireInvoker();
        MethodHandle acquireGetter_intVolatile = handle_intVolatile.getAcquireInvoker();
        MethodHandle releaseSetter_intPlain = handle_intPlain.setReleaseInvoker();
        MethodHandle releaseSetter_intVolatile = handle_intVolatile.setReleaseInvoker();
        int i = 0;
    }
}
