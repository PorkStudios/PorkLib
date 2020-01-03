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

package net.daporkchop.lib.common.cache;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.NonNull;

import java.util.function.Supplier;

/**
 * Implementation of {@link ThreadCache} that attempts to delegate to a {@link FastThreadLocal} if possible, but falls back to a normal Java {@link ThreadLocal}
 * if the current thread isn't a {@link FastThreadLocalThread}.
 *
 * @param <T> the type of value
 * @author DaPorkchop_
 */
public final class FastThreadCache<T> implements ThreadCache<T> {
    protected final Supplier<T> factory;
    protected final Object      obj;
    protected final boolean     netty;

    public FastThreadCache(@NonNull Supplier<T> factory) {
        this.factory = factory;

        Object obj;
        boolean netty;
        try {
            Class.forName("io.netty.util.concurrent.FastThreadLocal"); //make sure class exists

            obj = new FastThreadLocal<T>();
            netty = true;
        } catch (ClassNotFoundException e) {
            obj = new ThreadLocal<T>();
            netty = false;
        }
        this.obj = obj;
        this.netty = netty;
    }

    @Override
    public T get() {
        return this.netty ? this.getNetty() : this.getJava();
    }

    private T getNetty() {
        @SuppressWarnings("unchecked")
        FastThreadLocal<T> tl = (FastThreadLocal<T>) this.obj;
        T val = tl.getIfExists();
        if (val == null) {
            if ((val = this.factory.get()) == null) {
                throw new IllegalStateException();
            }
            tl.set(val);
        }
        return val;
    }

    private T getJava() {
        @SuppressWarnings("unchecked")
        ThreadLocal<T> tl = (ThreadLocal<T>) this.obj;
        T val = tl.get();
        if (val == null) {
            if ((val = this.factory.get()) == null) {
                throw new IllegalStateException();
            }
            tl.set(val);
        }
        return val;
    }

    @Override
    public T getUncached() {
        return this.factory.get();
    }
}
