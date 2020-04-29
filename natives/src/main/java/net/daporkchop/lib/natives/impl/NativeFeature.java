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
import net.daporkchop.lib.common.system.PlatformInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Abstraction of an implementation of a {@link Feature} using native code.
 *
 * @author DaPorkchop_
 */
public abstract class NativeFeature<F extends Feature<F>> implements Feature<F> {
    private static final String LIB_FMT;
    private static final String LIB_EXT;

    public static final boolean AVAILABLE;

    static {
        //these are the platforms that we compile native libraries for
        switch (PlatformInfo.OPERATING_SYSTEM) {
            case Linux:
                switch (PlatformInfo.ARCHITECTURE) {
                    case ARM:
                        LIB_FMT = "arm-linux-gnueabihf/"; //TODO: something about hard float detection
                        break;
                    case AARCH64:
                        LIB_FMT = "aarch64-linux-gnu/";
                        break;
                    case x86_64:
                        LIB_FMT = "x86_64-linux-gnu/";
                        break;
                    default:
                        LIB_FMT = null;
                }
                LIB_EXT = LIB_FMT == null ? null : ".so";
                break;
            case Windows:
                switch (PlatformInfo.ARCHITECTURE) {
                    case x86_64:
                        LIB_FMT = "x86_64-w64-mingw32/";
                        break;
                    default:
                        LIB_FMT = null;
                }
                LIB_EXT = LIB_FMT == null ? null : ".dll";
                break;
            default:
                LIB_EXT = LIB_FMT = null;
        }

        AVAILABLE = LIB_FMT != null;
    }

    /**
     * Attempts to load a native library.
     *
     * @param libName     the base name of the library
     * @param className   the canonical name of the class that the library will be loaded from
     * @param classLoader the {@link ClassLoader} that provides the library and the class
     * @return whether or not the library could be successfully loaded
     */
    public static LoadResult loadNativeLibrary(@NonNull String libName, @NonNull String className, @NonNull ClassLoader classLoader) {
        if (!NativeFeature.AVAILABLE) {
            return LoadResult.UNAVAILABLE;
        }

        String libPath = LIB_FMT + libName + LIB_EXT;
        if (classLoader.getResource(libPath) == null) {
            //library file couldn't be found
            return LoadResult.MISSING_RESOURCE;
        }

        Class<?> clazz;
        try {
            //attempt to find the class before making any files
            clazz = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            return LoadResult.MISSING_CLASS;
        }

        File tempFile;
        try (InputStream in = classLoader.getResourceAsStream(libPath)) {
            //create new library file
            tempFile = File.createTempFile(libName, LIB_EXT);
            tempFile.deleteOnExit();

            //copy library from resource to temp directory
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return LoadResult.EXCEPTION;
        }

        try {
            //pretend to load the library from the other class rather than NativeFeature
            Method method = Runtime.class.getDeclaredMethod("load0", Class.class, String.class);
            method.setAccessible(true);
            method.invoke(Runtime.getRuntime(), clazz, tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return LoadResult.EXCEPTION;
        } catch (UnsatisfiedLinkError e)    {
            e.printStackTrace();
            return LoadResult.UNSATISFIED_LINK;
        }

        return LoadResult.SUCCESS;
    }

    @Override
    public boolean isNative() {
        return true;
    }

    /**
     * Possible results of {@link #loadNativeLibrary(String, String, ClassLoader)}.
     *
     * @author DaPorkchop_
     */
    public enum LoadResult {
        /**
         * The native library was loaded successfully.
         */
        SUCCESS,
        /**
         * Native libraries are (currently) unavailable on this platform.
         */
        UNAVAILABLE,
        /**
         * The native library file could not be loaded as a resource from the provided {@link ClassLoader}.
         */
        MISSING_RESOURCE,
        /**
         * No class with the given name could be found by the provided {@link ClassLoader}.
         */
        MISSING_CLASS,
        /**
         * An exception occurred while attempting to load the native library.
         */
        EXCEPTION,
        /**
         * The requested library could not be loaded due to the absence of a required dependency.
         */
        UNSATISFIED_LINK;
    }
}
