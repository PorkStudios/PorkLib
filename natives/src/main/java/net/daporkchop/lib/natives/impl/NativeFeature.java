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
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

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
                        LIB_FMT = "/arm-linux-gnueabihf/lib"; //TODO: something about hard float detection
                        break;
                    case AARCH64:
                        LIB_FMT = "/aarch64-linux-gnu/lib";
                        break;
                    case x86_64:
                        LIB_FMT = "/x86_64-linux-gnu/lib";
                        break;
                    default:
                        LIB_FMT = null;
                }
                LIB_EXT = LIB_FMT == null ? null : ".so";
                break;
            case Windows:
                switch (PlatformInfo.ARCHITECTURE) {
                    case x86_64:
                        LIB_FMT = "/x86_64-w64-mingw32/lib";
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

    public static boolean loadNativeLibrary(@NonNull String name, @NonNull Class<?> clazz) {
        if (!NativeFeature.AVAILABLE) {
            return false;
        }

        try (InputStream is = clazz.getResourceAsStream(LIB_FMT + name + LIB_EXT)) {
            if (is == null) {
                //library doesn't exist
                return false;
            }

            File file = File.createTempFile(name, LIB_EXT);
            file.deleteOnExit();
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] arr = new byte[PUnsafe.pageSize()];
                for (int b; (b = is.read(arr)) >= 0; os.write(arr, 0, b)) ;
            }

            try {
                Method method = Runtime.class.getDeclaredMethod("load0", Class.class, String.class);
                method.setAccessible(true);
                method.invoke(Runtime.getRuntime(), clazz, file.getAbsolutePath());
            } catch (Exception e) {
                //this really isn't as good because it means that the library is considered to have been loaded by NativeFeature
                System.load(file.getAbsolutePath());
            }

            return true;
        } catch (Exception | UnsatisfiedLinkError e) {
            return false;
        }
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
