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

package net.daporkchop.lib.natives.impl;

import lombok.NonNull;
import net.daporkchop.lib.natives.Feature;
import net.daporkchop.lib.natives.NativeFeature;

import java.lang.reflect.Constructor;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * A native implementation of a {@link Feature}.
 *
 * @author DaPorkchop_
 */
public class NativeFeatureImplementation<F extends Feature<F>> extends FeatureImplementation<F> {
    protected final String libName;
    protected final ClassLoader loader;

    public NativeFeatureImplementation(String className, @NonNull String libName, @NonNull ClassLoader loader) {
        super(className);

        this.libName = libName;
        this.loader = loader;
    }

    @Override
    public F create() throws Throwable {
        Class<F> clazz = uncheckedCast(NativeFeature.loadNativeLibrary(this.libName, this.className, this.loader));
        Constructor<F> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    @Override
    public String toString() {
        return String.format("NativeFeatureImplementation(class=%s, lib=%s, loader=%s)", this.className, this.libName, this.loader);
    }
}
