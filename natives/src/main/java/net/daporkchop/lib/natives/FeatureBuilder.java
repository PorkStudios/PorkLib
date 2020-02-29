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

package net.daporkchop.lib.natives;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.natives.impl.Feature;
import net.daporkchop.lib.natives.impl.NativeFeature;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * A container around multiple implementations of a {@link Feature}.
 * <p>
 * Serves mainly to automatically choose the best implementation to use, and to avoid unnecessary loading of implementation classes that won't actually end
 * up being used.
 *
 * @param <F> the type of the {@link Feature} to be implemented
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureBuilder<F extends Feature<F>> {
    public static <F extends Feature<F>> FeatureBuilder<F> create(@NonNull Class<?> currentClass) {
        return new FeatureBuilder<>(currentClass);
    }

    private final Collection<Supplier<F>> implementations = new ArrayList<>();

    @NonNull
    private final Class<?> currentClass;

    public FeatureBuilder<F> addNative(@NonNull String className, @NonNull String libName) {
        return this.addNative(className, libName, this.currentClass.getClassLoader());
    }

    public FeatureBuilder<F> addNative(@NonNull String className, @NonNull String libName, @NonNull Class<?> currentClass) {
        return this.addNative(className, libName, currentClass.getClassLoader());
    }

    public FeatureBuilder<F> addNative(@NonNull String className, @NonNull String libName, @NonNull ClassLoader loader) {
        if (NativeFeature.AVAILABLE) {
            this.implementations.add(() -> {
                try {
                    Class<F> clazz = PorkUtil.uninitializedClassForName(className, loader);
                    return NativeFeature.loadNativeLibrary(libName, clazz) ? PUnsafe.allocateInstance(clazz) : null;
                } catch (Exception e)    {
                    return null;
                }
            });
        }
        return this;
    }

    public FeatureBuilder<F> addJava(@NonNull String className) {
        return this.addJava(className, this.currentClass.getClassLoader());
    }

    public FeatureBuilder<F> addJava(@NonNull String className, @NonNull Class<?> currentClass) {
        return this.addJava(className, currentClass.getClassLoader());
    }

    public FeatureBuilder<F> addJava(@NonNull String className, @NonNull ClassLoader loader) {
        this.implementations.add(() -> PUnsafe.allocateInstance(PorkUtil.uninitializedClassForName(className, loader)));
        return this;
    }

    public F build() {
        for (Supplier<F> implementation : this.implementations) {
            F value = implementation.get();
            if (value != null) {
                return value;
            }
        }

        throw new IllegalStateException("No implementations could be loaded!");
    }
}
