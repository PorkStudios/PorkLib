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

package net.daporkchop.lib.concurrent.sun;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The same as {@link sun.nio.ch.NativeThreadSet} except cool
 * <p>
 * no but seriously screw you java! what's the point in having apis if they're all package-private and have compile
 * warnings whenever you use them?
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NativeThreadSet {
    @NonNull
    protected long[] elts;
    protected int used = 0;
    protected boolean waitingToEmpty;

    public NativeThreadSet(int n) {
        this(new long[n]);
    }

    /**
     * Adds the current native thread to this set, returning its index so that
     * it can efficiently be removed later.
     */
    public int add() {
        long th = NativeThread.current();
        // 0 and -1 are treated as placeholders, not real thread handles
        if (th == 0)
            th = -1;
        synchronized (this) {
            int start = 0;
            if (this.used >= this.elts.length) {
                int on = this.elts.length;
                int nn = on * 2;
                long[] nelts = new long[nn];
                System.arraycopy(this.elts, 0, nelts, 0, on);
                this.elts = nelts;
                start = on;
            }
            for (int i = start; i < this.elts.length; i++) {
                if (this.elts[i] == 0) {
                    this.elts[i] = th;
                    this.used++;
                    return i;
                }
            }
            assert false;
            return -1;
        }
    }

    /**
     * Removes the thread at the given index.
     */
    public void remove(int i) {
        synchronized (this) {
            this.elts[i] = 0;
            this.used--;
            if (this.used == 0 && this.waitingToEmpty)
                this.notifyAll();
        }
    }

    /**
     * Signals all threads in this set.
     */
    public void signalAndWait() {
        synchronized (this) {
            int u = this.used;
            int n = this.elts.length;
            for (int i = 0; i < n; i++) {
                long th = this.elts[i];
                if (th == 0)
                    continue;
                if (th != -1)
                    NativeThread.signal(th);
                if (--u == 0)
                    break;
            }
            this.waitingToEmpty = true;
            boolean interrupted = false;
            while (this.used > 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }
}
