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
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A simple cache for native library files which have been extracted from relative URL paths.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class LibraryCache {
    /*
     * Implementation notes:
     *
     * We manage access to the cache using both a file lock and a globally shared mutex. This allows us to ensure exclusive access not only from other processes, but also from other
     * instances of LibraryCache (even if they belong to different ClassLoaders).
     */

    static final List<LibraryCache> CACHES;

    static {
        List<LibraryCache> caches = Collections.emptyList();
        if (!Boolean.getBoolean("net.daporkchop.lib.natives.disableLibraryCache")) {
            String explicitCacheDir = System.getProperty("net.daporkchop.lib.natives.libraryCacheDir");
            Path cacheDir = explicitCacheDir != null
                    ? Paths.get(explicitCacheDir)
                    : Paths.get(System.getProperty("java.io.tmpdir"), "net.daporkchop.lib.natives.libraryCache_v1");

            try {
                Files.createDirectories(cacheDir);

                //test if it's possible to lock the lock file
                try (FileChannel ch = FileChannel.open(cacheDir.resolve("LOCK"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                     FileLock ignored = ch.lock()) {
                    //do nothing
                }

                System.getProperties().putIfAbsent("net.daporkchop.lib.natives.LibraryCache.cacheMutex", new Object[0]);
                Object cacheMutex = System.getProperties().get("net.daporkchop.lib.natives.LibraryCache.cacheMutex");

                caches = Collections.singletonList(new LibraryCache(cacheDir, cacheMutex));
            } catch (IOException e) {
                //failed to create the cache directory, so we won't use the cache
            }
        }
        CACHES = caches;
    }

    @SneakyThrows(NoSuchAlgorithmException.class)
    private static String hashFile(@NonNull URL file) throws IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); //this should always be supported

        //read and hash the entire file
        try (InputStream in = file.openStream()) {
            byte[] buf = new byte[PorkUtil.bufferSize()];
            for (int len; (len = in.read(buf)) >= 0; ) {
                digest.update(buf, 0, len);
            }
        }

        //convert the file contents to hexadecimal
        //TODO: there's a dedicated class for this in newer java versions
        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest()) {
            result.append(Integer.toHexString((1 << Byte.SIZE) | Byte.toUnsignedInt(b)).substring(1));
        }
        return result.toString();
    }

    private static boolean areFilesEqual(@NonNull URL a, @NonNull Path b) throws IOException {
        //read and hash the entire file
        try (InputStream inA = a.openStream();
             InputStream inB = Files.newInputStream(b)) {
            final int bufferSize = PorkUtil.bufferSize();
            byte[] bufA = new byte[bufferSize];
            byte[] bufB = new byte[bufferSize];

            while (true) {
                int lenA = inA.read(bufA);
                int lenB = inB.read(bufB);
                if (lenA != lenB) {
                    return false;
                } else if (lenA < 0) { //EOF reached
                    break;
                } else if (lenA == bufA.length //hack to perform Arrays.equals() on part of an array
                        ? !Arrays.equals(bufA, bufB)
                        : !Arrays.equals(Arrays.copyOf(bufA, lenA), Arrays.copyOf(bufB, lenB))) { //TODO: there's a dedicated intrinsic for this in newer java versions
                    return false;
                }
            }
        }
        return true;
    }

    private final @NonNull Path cacheDir;
    private final @NonNull Object cacheMutex;

    public Optional<Path> getCacheFilePath(@NonNull URL libraryFileUrl, @NonNull String libraryFileBasename) {
        synchronized (this.cacheMutex) {
            try (FileChannel lockChannel = FileChannel.open(this.cacheDir.resolve("LOCK"), StandardOpenOption.WRITE);
                 FileLock ignored = lockChannel.lock()) {
                //check if we can re-use the library copy already in the cache
                Path cacheFilePath = this.cacheDir.resolve(hashFile(libraryFileUrl)).resolve(libraryFileBasename);
                if (Files.exists(cacheFilePath) && areFilesEqual(libraryFileUrl, cacheFilePath)) {
                    return Optional.of(cacheFilePath);
                }

                //copy the library file to a temporary file in the cache directory
                Path tmpCacheFilePath = Files.createTempFile(Files.createDirectories(cacheFilePath.getParent()), null, null);
                try {
                    try (InputStream in = libraryFileUrl.openStream()) {
                        Files.copy(in, tmpCacheFilePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    //TODO: it would be good if we could make the file immutable on linux (maybe using chattr?)
                    Files.move(tmpCacheFilePath, cacheFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } finally {
                    Files.deleteIfExists(tmpCacheFilePath);
                }

                return Optional.of(cacheFilePath);
            } catch (IOException e) {
                //something went wrong while trying to look for the file in the cache, fall back to next implementation
                return Optional.empty();
            }
        }
    }
}
