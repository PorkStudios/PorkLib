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
 */

package net.daporkchop.lib.natives;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.natives.util.exception.NoFeatureImplementationsFoundError;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class FeatureLoader {
    public static <F extends Feature<F>> F loadService(@NonNull MethodHandles.Lookup lookup, @NonNull Class<F> featureClass) throws NoFeatureImplementationsFoundError {
        return loadService(lookup, featureClass, lookup.lookupClass().getClassLoader());
    }

    public static <F extends Feature<F>> F loadService(@NonNull MethodHandles.Lookup lookup, @NonNull Class<F> featureClass, @NonNull ClassLoader loader) throws NoFeatureImplementationsFoundError {
        NoFeatureImplementationsFoundError root = null;
        for (Iterator<F> itr = ServiceLoader.load(featureClass, loader).iterator(); ; ) {
            try {
                if (!itr.hasNext()) {
                    break;
                }

                return itr.next();
            } catch (ServiceConfigurationError e) {
                if (Boolean.getBoolean("porklib.native.printStackTraces")) {
                    e.printStackTrace();
                }

                if (root == null) {
                    root = new NoFeatureImplementationsFoundError("all implementations failed to load: " + featureClass.getName());
                }
                root.addSuppressed(e);
            }
        }

        if (root == null) {
            root = new NoFeatureImplementationsFoundError("no implementations found: " + featureClass.getName());
        }
        throw root;
    }
}
