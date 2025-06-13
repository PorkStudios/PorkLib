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
 *
 */

package unsafe;

import net.daporkchop.lib.unsafe.PCleaner;
import org.junit.Test;

import java.lang.ref.WeakReference;

/**
 * @author DaPorkchop_
 */
public class PCleanerTest {
    volatile Object o;
    volatile PCleaner cleaner;

    @Test
    public void testCollect() {
        BoolRunnable action = new BoolRunnable();

        this.o = new Object[0];
        WeakReference<?> ref = new WeakReference<>(this.o);
        this.cleaner = PCleaner.cleaner(this.o, action);

        assert !action.run;

        this.o = null;
        do { //spin until instance is GCd
            System.gc();
        } while (ref.get() != null);

        //spin until the action is run
        while (!action.run);
    }

    @Test
    public void testCancelBeforeCollect() throws Throwable {
        BoolRunnable action = new BoolRunnable();

        this.o = new Object[0];
        WeakReference<?> ref = new WeakReference<>(this.o);
        this.cleaner = PCleaner.cleaner(this.o, action);

        assert !action.run;

        this.cleaner.cancel();

        assert !action.run;

        this.o = null;
        do { //spin until instance is GCd
            System.gc();
        } while (ref.get() != null);

        //sleep until the cleaner has probably been processed
        Thread.sleep(50L);

        assert !action.run;
    }

    @Test
    public void testReplaceBeforeCollect() {
        BoolRunnable action1 = new BoolRunnable();
        BoolRunnable action2 = new BoolRunnable();

        this.o = new Object[0];
        WeakReference<?> ref = new WeakReference<>(this.o);
        this.cleaner = PCleaner.cleaner(this.o, action1);

        assert !action1.run;
        assert !action2.run;

        this.cleaner.replace(action2);

        assert !action1.run;
        assert !action2.run;

        this.o = null;
        do { //spin until instance is GCd
            System.gc();
        } while (ref.get() != null);

        //spin until the action is run
        while (!action2.run);

        assert !action1.run;
        assert action2.run;
    }

    static class BoolRunnable implements Runnable {
        volatile boolean run;

        @Override
        public synchronized void run() {
            if (this.run) {
                throw new IllegalStateException();
            }

            this.run = true;
        }
    }
}
