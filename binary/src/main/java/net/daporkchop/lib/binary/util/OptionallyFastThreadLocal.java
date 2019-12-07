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

package net.daporkchop.lib.binary.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.cache.ThreadCache;

import java.util.function.Supplier;

/**
 * Implementation of {@link ThreadCache} that attempts to delegate to a {@link FastThreadLocal} if possible, but falls back to a normal Java {@link ThreadLocal}
 * if the current thread isn't a {@link FastThreadLocalThread}.
 *
 * @param <T> the type of value
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class OptionallyFastThreadLocal<T> implements ThreadCache<T> {
    protected final FastThreadLocal<T> fast = new FastThreadLocal<>();
    protected final ThreadLocal<T>     java = new ThreadLocal<>();

    @NonNull
    protected final Supplier<T> factory;

    @Override
    public T get() {
        T val;
        if (Thread.currentThread() instanceof FastThreadLocalThread) {
            if ((val = this.fast.get()) == null) {
                if ((val = this.factory.get()) == null) {
                    throw new IllegalStateException();
                } else {
                    this.fast.set(val);
                }
            }
        } else {
            if ((val = this.java.get()) == null) {
                if ((val = this.factory.get()) == null) {
                    throw new IllegalStateException();
                } else {
                    this.java.set(val);
                }
            }
        }
        return val;
    }

    @Override
    public T getUncached() {
        return this.factory.get();
    }
}
