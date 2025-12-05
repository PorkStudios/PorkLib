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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JniLoader {
    private static Class<?> requireLookupClass(MethodHandles.Lookup lookup) {
        Class<?> lookupClass = lookup.lookupClass();
        if (lookupClass == null) {
            throw new IllegalArgumentException("provided MethodHandles.Lookup must have a lookup class");
        }
        return lookupClass;
    }

    public static void loadNamedRelocatableLibrary(
            @NonNull MethodHandles.Lookup lookup, @NonNull String libraryName,
            @NonNull String escapedClassName,
            @NonNull NamedLibraryLoader loader) throws Throwable {
        loadNamedRelocatableLibraryWithPrefix(lookup, libraryName,
                getPackagePrefix(requireLookupClass(lookup), escapedClassName),
                loader);
    }

    public static void loadNamedRelocatableLibraryWithPrefix(
            @NonNull MethodHandles.Lookup lookup, @NonNull String libraryName,
            @NonNull String packagePrefix,
            @NonNull NamedLibraryLoader loader) throws Throwable {
        requireLookupClass(lookup);

        String mutexKey = "porklib_natives_load_mutex#" + libraryName;
        Object mutexValue = new Object[0];
        String configKey = "porklib_natives_load_config#" + libraryName;

        val properties = System.getProperties();

        final byte STATE_NEVER = 0;
        final byte STATE_ALLOWED = 1;
        final byte STATE_ALWAYS = 2;

        final class Config {
            public String packagePrefix;

            public byte useCriticalRead;
            public byte useCriticalWrite;

            public byte useGetElementsRead;
            public byte useGetElementsWrite;
        }
        val config = new Config();

        config.packagePrefix = packagePrefix;

        config.useCriticalRead = config.useCriticalWrite = NativeUtils.allowJniCritical() ? STATE_ALLOWED : STATE_NEVER;

        // We never use Get*ArrayElements(): as of this writing (Sep. 2025), there aren't any GC implementations on any Java version
        // which ever pin the arrays. Effectively, this means that the array is always copied, which makes it no better than an
        // implementation using manual buffer allocation and JNI copies (plus, manual copying lets us avoid moving unnecessary
        // data if not the entire array is accessed).
        config.useGetElementsRead = config.useGetElementsWrite = STATE_NEVER;

        //atomically insert the mutex value into the global system properties map so that no other thread can overwrite our other properties while we're loading the library.
        while (properties.putIfAbsent(mutexKey, mutexValue) != null) {
            //contention here is extremely unlikely, so i'll just make this do a spinlock
            Thread.yield();
        }

        try {
            properties.put(configKey, config);

            loader.load(lookup, libraryName);

            if (properties.containsKey(configKey)) {
                throw new AssertionError("the library's JNI_OnLoad function didn't remove the config object from the system properties map");
            }
        } finally {
            //always make sure to clean up by removing all property keys from the map
            properties.remove(configKey);
            properties.remove(mutexKey, mutexValue);
        }
    }

    public static String getPackagePrefix(@NonNull Class<?> referenceClass, @NonNull String escapedClassName) {
        String actualClassName = referenceClass.getName();
        String origClassName = escapedClassName.replace('!', '.');

        if (!actualClassName.endsWith(origClassName)) {
            throw new IllegalArgumentException(referenceClass + "'s name should end with " + origClassName);
        }

        return actualClassName.substring(0, actualClassName.length() - escapedClassName.length()).replace('.', '/');
    }

    @FunctionalInterface
    public interface NamedLibraryLoader {
        /**
         * Attempts to load a JNI library.
         *
         * @param lookup      a {@link MethodHandles.Lookup} whose lookup class provides access to the library and whose class loader will be the one which the library is loaded into
         * @param libraryName the name of the library
         * @throws Throwable if the library cannot be loaded for any reason
         */
        void load(@NonNull MethodHandles.Lookup lookup, @NonNull String libraryName) throws Throwable, SecurityException, UnsatisfiedLinkError;
    }

    /**
     * Attempts to load a JNI library by name.
     * <p>
     * The given library name is concatenated with a platform-dependent suffix, then the resulting string is used as a resource name relative to the given {@link MethodHandles.Lookup}'s lookup class
     * to find the actual resource file.
     *
     * @param lookup      a {@link MethodHandles.Lookup} whose lookup class provides access to the library and whose class loader will be the one which the library is loaded into
     * @param libraryName the name of the library
     * @throws Throwable if the library cannot be loaded for any reason
     */
    public static void loadNamedLibraryFromResource(@NonNull MethodHandles.Lookup lookup, @NonNull String libraryName) throws Throwable {
        Class<?> lookupClass = requireLookupClass(lookup);

        String libraryFileBasename = getLibraryFileBasename(libraryName);
        URL libraryFileUrl = lookupClass.getResource(libraryFileBasename);
        if (libraryFileUrl == null) { //library file couldn't be found
            throw new FileNotFoundException("class: " + lookupClass.getName() + ", resource name: " + libraryFileBasename);
        }

        loadLibraryFromPath(lookup, getLibraryFilePathFromUrl(libraryFileBasename, libraryFileUrl));
    }

    /**
     * Attempts to load a JNI library from a URL to the library file.
     * <p>
     * The URL may be a path to a file on the local filesystem, in which case it will be copied to a file on the local filesystem. The copied file may be cached and re-used between
     * {@link ClassLoader}s or JVM instances.
     *
     * @param lookup      a {@link MethodHandles.Lookup} whose lookup class' class loader will be the one which the library is loaded into
     * @param libraryName the name of the library, used to determine the cached/temporary file name if necessary
     * @throws Throwable if the library cannot be loaded for any reason
     */
    public static void loadNamedLibraryFromUrl(@NonNull MethodHandles.Lookup lookup, @NonNull String libraryName, @NonNull URL libraryFileUrl) throws Throwable {
        requireLookupClass(lookup);

        loadLibraryFromPath(lookup, getLibraryFilePathFromUrl(libraryName, libraryFileUrl));
    }

    /**
     * Attempts to load a JNI library from a path to the library file on the local filesystem.
     *
     * @param lookup      a {@link MethodHandles.Lookup} whose lookup class' class loader will be the one which the library is loaded into
     * @param libraryPath the path to the library file
     * @throws Throwable if the library cannot be loaded for any reason
     */
    private static void loadLibraryFromPath(@NonNull MethodHandles.Lookup lookup, @NonNull Path libraryPath) throws Throwable, SecurityException, UnsatisfiedLinkError {
        requireLookupClass(lookup);

        //calling System.load() through a MethodHandle like this ensures that the library is loaded by the correct ClassLoader
        lookup.findStatic(System.class, "load", MethodType.methodType(void.class, String.class)).invokeExact(libraryPath.toAbsolutePath().toString());
    }

    public static NamedLibraryLoader namedLibraryFromResourceLoader() {
        return JniLoader::loadNamedLibraryFromResource;
    }

    public static NamedLibraryLoader namedLibraryFromResourceLoader(@NonNull String libraryName) {
        return (lookup, ignored) -> loadNamedLibraryFromResource(lookup, libraryName);
    }

    public static NamedLibraryLoader libraryFromPathLoader(@NonNull Path libraryFilePath) {
        return (lookup, libraryName) -> loadLibraryFromPath(lookup, libraryFilePath);
    }

    private static Path getLibraryFilePathFromUrl(@NonNull String libraryName, @NonNull URL libraryFileUrl) throws IOException {
        if ("file".equals(libraryFileUrl.getProtocol())) {
            try {
                //if the path is a raw library path, try to return it as-is
                return Paths.get(libraryFileUrl.toURI());
            } catch (Exception e) {
                //somehow this isn't a valid file path, fall back to next implementation
            }
        }

        //try to cache the library file
        for (LibraryCache cache : LibraryCache.CACHES) {
            Optional<Path> cacheFilePath = cache.getCacheFilePath(libraryFileUrl, getLibraryFileBasename(libraryName));
            if (cacheFilePath.isPresent()) {
                return cacheFilePath.get();
            }
        }

        //fall back to the default approach of creating a regular temporary file and copying the library there
        Path tempFilePath = Files.createTempFile(getTempLibraryFileBase(libraryName), getLibraryFileExtension());
        tempFilePath.toFile().deleteOnExit();
        try (InputStream in = libraryFileUrl.openStream()) {
            Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFilePath;
    }

    private static String getTempLibraryFileBase(@NonNull String libraryName) {
        String mappedName = System.mapLibraryName(getLibraryFileIdentifier(libraryName));
        return mappedName.substring(0, mappedName.indexOf('.'));
    }

    private static String getLibraryFileBasename(@NonNull String libraryName) {
        //return System.mapLibraryName(getLibraryFileIdentifier(libraryName));
        return getLibraryFileIdentifier(libraryName) + getLibraryFileExtension();
    }

    private static String getLibraryFileExtension() {
        String mappedName = System.mapLibraryName("");
        return mappedName.substring(mappedName.indexOf('.'));
    }

    private static String getLibraryFileIdentifier(@NonNull String libraryName) {
        if (libraryName.isEmpty()) {
            return getLibraryFilePlatformSuffix();
        } else {
            return libraryName + '-' + getLibraryFilePlatformSuffix();
        }
    }

    private static String LIBRARY_FILE_PLATFORM_SUFFIX;

    private static String getLibraryFilePlatformSuffix() {
        if (LIBRARY_FILE_PLATFORM_SUFFIX != null) {
            return LIBRARY_FILE_PLATFORM_SUFFIX;
        }

        //race to compute the suffix and cache it (i don't care if this happens to get executed multiple times)
        return LIBRARY_FILE_PLATFORM_SUFFIX = getTargetArch() + '-' + getTargetOS();
    }


    // following code is adapted from the following sources:
    //   https://github.com/openjdk/jdk/blob/c1f698d38bb251941598af5a82a1a230282b718d/src/java.base/share/classes/jdk/internal/util/Architecture.java
    //   https://llvm.org/doxygen/Triple_8cpp_source.html
    //   https://github.com/facebook/rocksdb/blob/main/java/src/main/java/org/rocksdb/util/Environment.java

    private static String getTargetArch() {
        val os_arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        switch (os_arch) {
            case "i386":
            case "x86":
                return "i386";
            case "x64":
            case "x86_64":
            case "x86_64h":
            case "amd64":
                return "x86_64";
            case "s390x":
                return "s390";
            default:
                return os_arch;
        }
    }

    private static String getTargetOS() {
        val os_name = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os_name.contains("linux")) {
            return "linux" + getLibcPostfix();
        } else if (os_name.contains("mac")) {
            return "osx";
        } else if (os_name.contains("win")) {
            return "windows-msvc";
        } else if (os_name.contains("freebsd")) {
            return "freebsd";
        } else if (os_name.contains("openbsd")) {
            return "openbsd";
        } else {
            throw new IllegalArgumentException("\"os.arch\" has unknown value: " + os_name);
        }
    }

    private static String getLibcPostfix() {
        return isMuslLibc() ? "-musl" : "-gnu";
    }

    private static boolean isMuslLibc() {
        try {
            return new ProcessBuilder("/usr/bin/env", "sh", "-c", "ldd /usr/bin/env | grep -q musl").start().waitFor() == 0;
        } catch (InterruptedException | IOException ignored) {
            return false;
        }
    }
}
