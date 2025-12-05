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
import lombok.SneakyThrows;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.natives.util.exception.NativeFeaturesUnavailableException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Abstraction of an implementation of a {@link Feature} using native code.
 *
 * @author DaPorkchop_
 */
public abstract class NativeFeature<F extends Feature> implements Feature {
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
     * @param lookup  a {@link MethodHandles.Lookup} whose lookup class provides access to the library and whose class loader will be the one which the library is loaded into
     * @param libName the base name of the library, or {@code null}
     * @throws Throwable if the library cannot be loaded for any reason
     */
    protected static void loadNativeLibrary(@NonNull MethodHandles.Lookup lookup, String libName) throws SecurityException, UnsatisfiedLinkError, UncheckedIOException {
        if (libName == null) {
            libName = "";
        }

        if (!NativeFeature.AVAILABLE) {
            throw new NativeFeaturesUnavailableException(libName);
        }

        Class<?> lookupClass = lookup.lookupClass();
        if (lookupClass == null) {
            throw new IllegalArgumentException("provided MethodHandles.Lookup must have a lookup class");
        }

        String libPath = resourcePath(libName);
        URL libUrl = lookupClass.getResource(libPath);
        if (libUrl == null) { //library file couldn't be found
            throw new UncheckedIOException(new FileNotFoundException("resource: " + libPath + ", class: " + lookupClass.getName()));
        }

        loadNativeLibraryFile(lookup, libUrl, lookupClass.getName() + (libName.isEmpty() ? "" : '-' + libName), '.' + LIB_EXT);
    }

    /**
     * Attempts to load a native library.
     *
     * @param lookup a {@link MethodHandles.Lookup} whose lookup class provides access to the library and whose class loader will be the one which the library is loaded into
     * @param srcUrl the {@link URL} to the library file to be loaded
     * @throws Throwable if the library cannot be loaded for any reason
     */
    protected static void loadNativeLibraryFile(@NonNull MethodHandles.Lookup lookup, @NonNull URL srcUrl, @NonNull String fileNameBase) throws SecurityException, UnsatisfiedLinkError, RuntimeException {
        if (lookup.lookupClass() == null) {
            throw new IllegalArgumentException("provided MethodHandles.Lookup must have a lookup class");
        }

        loadNativeLibraryFile(lookup, srcUrl, fileNameBase, LIB_EXT);
    }

    private static void loadNativeLibraryFile(@NonNull MethodHandles.Lookup lookup, @NonNull URL srcUrl, @NonNull String fileNameBase, @NonNull String fileNameExtension) throws SecurityException, UnsatisfiedLinkError, RuntimeException {
        loadNativeLibraryFile(lookup, getLibraryFilePath(srcUrl, fileNameBase, fileNameExtension));
    }

    private static Path getLibraryFilePath(@NonNull URL srcUrl, @NonNull String fileNameBase, @NonNull String fileNameExtension) throws RuntimeException {
        if ("file".equals(srcUrl.getProtocol())) {
            try {
                //if the path is a raw library path, try to return it as-is
                return Paths.get(srcUrl.toURI());
            } catch (Exception e) {
                //somehow this isn't a valid file path, fall back to next implementation
            }
        }

        //try to cache the library file
        for (LibraryCache cache : LibraryCache.CACHES) {
            Optional<Path> cacheFilePath = cache.getCacheFilePath(srcUrl, fileNameBase + fileNameExtension);
            if (cacheFilePath.isPresent()) {
                return cacheFilePath.get();
            }
        }

        //fall back to the default approach of creating a regular temporary file and copying the library there
        try {
            Path tempFilePath = Files.createTempFile(fileNameBase, '.' + fileNameExtension);
            tempFilePath.toFile().deleteOnExit();
            try (InputStream in = srcUrl.openStream()) {
                Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFilePath;
        } catch (IOException e) {
            throw new UncheckedIOException("failed to copy library to temp file", e);
        }
    }

    @SneakyThrows
    private static void loadNativeLibraryFile(@NonNull MethodHandles.Lookup lookup, @NonNull Path libraryPath) throws SecurityException, UnsatisfiedLinkError {
        //calling System.load() through a MethodHandle like this ensures that the library is loaded by the correct ClassLoader
        lookup.findStatic(System.class, "load", MethodType.methodType(void.class, String.class)).invokeExact(libraryPath.toAbsolutePath().toString());
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
