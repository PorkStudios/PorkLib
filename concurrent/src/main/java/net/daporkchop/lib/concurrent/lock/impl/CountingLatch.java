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

package net.daporkchop.lib.concurrent.lock.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.lock.Latch;
import net.daporkchop.lib.concurrent.util.DefaultListenable;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class CountingLatch extends DefaultListenable<Latch> implements Latch {
    protected static final long TICKETS_OFFSET = PUnsafe.pork_getOffset(CountingLatch.class, "tickets");

    @NonNull
    @Getter
    protected volatile int tickets;

    @Override
    public void release() {
        if (PUnsafe.getAndAddInt(this, TICKETS_OFFSET, -1) == 1) {
            //decremented from 1 to 0
            synchronized (this.mutex) {
                this.fireListeners();
                this.mutex.notifyAll();
            }
        }
    }

    @Override
    public void sync() {
        synchronized (this.mutex) {
            while (this.tickets > 0) {
                try {
                    this.mutex.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void syncInterruptably() throws InterruptedException {
        synchronized (this.mutex) {
            while (this.tickets > 0) {
                this.mutex.wait();
            }
        }
    }

    @Override
    protected void doFireListener(@NonNull Consumer<Latch> listener) {
        listener.accept(this);
    }
}
