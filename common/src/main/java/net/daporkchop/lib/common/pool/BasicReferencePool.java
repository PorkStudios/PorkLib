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

package net.daporkchop.lib.common.pool;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A basic implementation of {@link ReferencePool}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class BasicReferencePool<T extends PooledRefCounted> implements ReferencePool<T> {
    protected final Set<T> delegate = new HashSet<>();
    @NonNull
    protected final Supplier<T> supplier;

    @Override
    public synchronized T get() {
        T instance;
        if (this.delegate.isEmpty() && (instance = this.supplier.get()) == null) {
            throw new IllegalStateException("supplier may not return null!");
        } else {
            Iterator<T> iterator = this.delegate.iterator();
            instance = iterator.next();
            iterator.remove();
        }
        return instance;
    }

    @Override
    public synchronized void release(@NonNull T instance) {
        if (instance.refCount() != 0) {
            throw new IllegalArgumentException(String.format("Instance has non-zero reference count: %d", instance.refCount()));
        } else {
            this.delegate.add(instance);
        }
    }
}
