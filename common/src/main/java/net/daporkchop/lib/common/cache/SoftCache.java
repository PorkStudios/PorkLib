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

package net.daporkchop.lib.common.cache;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Implementation of {@link Cache} that uses a {@link SoftReference} to store its value.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class SoftCache<T> implements Cache<T> {
    @NonNull
    protected final Supplier<T> factory;

    protected SoftReference<T> ref;

    @Override
    public T get() {
        T val;
        if (this.ref == null || (val = this.ref.get()) == null) {
            synchronized (this) {
                //check again after obtaining lock, it may have been set by another thread
                if (this.ref == null || (val = this.ref.get()) == null) {
                    this.ref = new SoftReference<>(val = Objects.requireNonNull(this.factory.get()));
                }
            }
        }
        return val;
    }
}
