/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

import lombok.NonNull;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.natives.util.exception.NativeFeaturesUnavailableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Abstraction of an implementation of a {@link Feature} using native code.
 *
 * @author DaPorkchop_
 */
public abstract class NativeFeature<F extends Feature<F>> implements Feature<F> {
    private static final String LIB_ARCH;
    private static final String LIB_EXT;

    public static final boolean AVAILABLE;

    static {
        //these are the platforms that we compile native libraries for
        switch (PlatformInfo.OPERATING_SYSTEM) {
            case Linux:
                switch (PlatformInfo.ARCHITECTURE) {
                    case ARM:
                        LIB_ARCH = "arm-linux-gnueabihf"; //TODO: something about hard float detection
                        break;
                    case AARCH64:
                        LIB_ARCH = "aarch64-linux-gnu";
                        break;
                    case x86_64:
                        LIB_ARCH = "x86_64-linux-gnu";
                        break;
                    default:
                        LIB_ARCH = null;
                }
                LIB_EXT = LIB_ARCH == null ? null : "so";
                break;
            case Windows:
                switch (PlatformInfo.ARCHITECTURE) {
                    case x86_64:
                        LIB_ARCH = "x86_64-w64-mingw32";
                        break;
                    default:
                        LIB_ARCH = null;
                }
                LIB_EXT = LIB_ARCH == null ? null : "dll";
                break;
            default:
                LIB_EXT = LIB_ARCH = null;
        }

        AVAILABLE = LIB_ARCH != null;
    }

    private static String resourcePath(@NonNull String libName) {
        if (libName.startsWith("/")) {
            libName = libName.substring(1);
        }
        String format = libName.isEmpty() ? "%1$s.%3$s" : "%1$s/%2$s.%3$s";
        return String.format(format, LIB_ARCH, libName, LIB_EXT);
    }

    /**
     * Attempts to load a native library.
     *
     * @param libName     the base name of the library
     * @param className   the canonical name of the class that the library will be loaded from
     * @param classLoader the {@link ClassLoader} that provides the library and the class
     * @return the class that the library was loaded from
     * @throws Throwable if an exception occurs while loading the library
     */
    public static Class<?> loadNativeLibrary(@NonNull String libName, @NonNull String className, @NonNull ClassLoader classLoader) throws Throwable {
        if (!NativeFeature.AVAILABLE) {
            throw new NativeFeaturesUnavailableException(libName);
        }

        //attempt to find the class before making any files
        Class<?> clazz = Class.forName(className, false, classLoader);

        String libPath = resourcePath(libName);

        //create new library file
        File tempFile = File.createTempFile(libName + UUID.randomUUID(), '.' + LIB_EXT);

        try (InputStream in = clazz.getResourceAsStream(libPath)) {
            if (in == null) {//library file couldn't be found
                //delete temporary file now
                tempFile.delete();

                throw new FileNotFoundException("resource: " + libPath + ", class: " + clazz.getCanonicalName());
            }

            //copy library from resource to temp directory
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        //mark temporary file for deletion
        tempFile.deleteOnExit();

        try {
            //pretend to load the library from the other class rather than NativeFeature
            Method method = Runtime.class.getDeclaredMethod("load0", Class.class, String.class);
            method.setAccessible(true);
            method.invoke(Runtime.getRuntime(), clazz, tempFile.getAbsolutePath());
        } catch (Exception e) {
            if (PlatformInfo.JAVA_VERSION >= 8) {
                new RuntimeException("you are running java 9+, which is bad and not good. this means that native libraries will be loaded from the incorrect classloader.", e).printStackTrace();
            }
            //fallback to System.load
            System.load(tempFile.getAbsolutePath());
        }

        return clazz;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
