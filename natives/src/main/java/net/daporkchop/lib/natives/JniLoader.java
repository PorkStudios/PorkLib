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

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JniLoader {
    public static void loadJniLibraryInPrefix(@NonNull String libraryName, @NonNull String packagePrefix, @NonNull Runnable loadFunction) {
        String mutexKey = "porklib_natives_load_mutex#" + libraryName;
        Object mutexValue = new Object[0];
        String packagePrefixKey = "porklib_natives_load_packagePrefix#" + libraryName;

        val properties = System.getProperties();

        //atomically insert the mutex value into the global system properties map so that no other thread can overwrite our other properties while we're loading the library.
        while (properties.putIfAbsent(mutexKey, mutexValue) != null) {
            //contention here is extremely unlikely, so i'll just make this do a spinlock
            Thread.yield();
        }

        try {
            properties.put(packagePrefixKey, packagePrefix);

            loadFunction.run();

            if (properties.containsKey(packagePrefixKey)) {
                throw new AssertionError("the library's JNI_OnLoad function didn't remove the package prefix from the system properties map");
            }
        } finally {
            //always make sure to clean up by removing all property keys from the map
            properties.remove(packagePrefixKey);
            properties.remove(mutexKey, mutexValue);
        }
    }
}
