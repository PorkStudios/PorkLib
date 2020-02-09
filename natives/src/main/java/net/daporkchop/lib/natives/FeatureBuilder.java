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
                Class<F> clazz = PorkUtil.uninitializedClassForName(className, loader);
                return NativeFeature.loadNativeLibrary(libName, clazz) ? PUnsafe.allocateInstance(clazz) : null;
            });
        }
        return this;
    }

    public FeatureBuilder<F> addJava(@NonNull String className) {
        this.implementations.add(() -> PUnsafe.allocateInstance(PorkUtil.uninitializedClassForName(className)));
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
