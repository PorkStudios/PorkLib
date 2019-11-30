/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.system.Architecture;
import net.daporkchop.lib.common.system.OperatingSystem;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * A wrapper around multiple distinct implementations of something.
 *
 * @param <T> the type of the feature to be implemented
 * @author DaPorkchop_
 */
public final class NativeCode<T> implements Supplier<T> {
    private static String LIB_ARCH;
    private static String LIB_EXT;

    public static void loadNativeLibrary(@NonNull String name)  {
        if (!NativeImpl.AVAILABLE)  {
            throw new UnsupportedOperationException("native libraries are not available!");
        } else if (LIB_ARCH == null)  {
            synchronized (NativeCode.class) {
                if (LIB_ARCH == null) {
                    switch (PlatformInfo.OPERATING_SYSTEM)  {
                        case Linux:
                            LIB_EXT = "so";
                            switch (PlatformInfo.ARCHITECTURE)  {
                                case x86_64:
                                    LIB_ARCH = "x86_64-linux-gnu";
                                    break;
                                case x86:
                                    LIB_ARCH = "x86-linux-gnu";
                                    break;
                            }
                            break;
                        case Windows:
                            if (PlatformInfo.ARCHITECTURE == Architecture.x86_64)   {
                                LIB_EXT = "dll";
                                LIB_ARCH = "x86_64-w64-mingw32";
                            }
                            break;
                    }
                    if (LIB_ARCH == null || LIB_EXT == null)    {
                        throw new IllegalStateException();
                    }
                }
            }
        }
        try {
            File file = File.createTempFile(name + System.nanoTime(), ".so");
            file.deleteOnExit();
            try (InputStream is = NativeCode.class.getResourceAsStream(String.format("/%s/lib%s.%s", LIB_ARCH, name, LIB_EXT));
                 OutputStream os = new FileOutputStream(file))   {
                byte[] arr = new byte[PUnsafe.pageSize()];
                for (int b; (b = is.read(arr)) >= 0; os.write(arr, 0, b));
            }
            System.load(file.getAbsolutePath());
        } catch (Exception e)    {
            throw new RuntimeException(String.format("Unable to load library \"%s\"", name), e);
        }
    }

    private final Impl<T> implementation;

    @SafeVarargs
    public NativeCode(@NonNull Supplier<Impl<T>>... implementationFactories) {
        for (Supplier<Impl<T>> implementationFactory : implementationFactories) {
            Impl<T> implementation = implementationFactory.get();
            if (implementation.available()) {
                this.implementation = implementation;
                return;
            }
        }

        throw new IllegalStateException("No implementations found!");
    }

    @Override
    public T get() {
        return this.implementation.get();
    }

    /**
     * An implementation for use by {@link NativeCode}.
     *
     * @param <T> the type of the feature to be implemented
     * @author DaPorkchop_
     */
    @Getter
    @Accessors(fluent = true)
    public static abstract class Impl<T> implements Supplier<T> {
        protected final boolean available = this._available();

        @Override
        public T get() {
            if (this.available) {
                return this._get();
            } else {
                throw new IllegalStateException("Not available!");
            }
        }

        protected abstract T _get();

        protected abstract boolean _available();
    }

    /**
     * Extension of {@link Impl} for use by implementations that actually use native code.
     * <p>
     * Eliminates the boilerplate of checking if the current system is supported.
     *
     * @param <T> the type of the feature to be implemented
     * @author DaPorkchop_
     */
    public static abstract class NativeImpl<T> extends Impl<T> {
        /**
         * Whether or not native libraries are available.
         */
        public static final boolean AVAILABLE =
                ((PlatformInfo.ARCHITECTURE == Architecture.x86 || PlatformInfo.ARCHITECTURE == Architecture.x86_64) && PlatformInfo.OPERATING_SYSTEM == OperatingSystem.Linux)
                || (PlatformInfo.ARCHITECTURE == Architecture.x86_64 && PlatformInfo.OPERATING_SYSTEM == OperatingSystem.Windows);

        @Override
        protected boolean _available() {
            return AVAILABLE;
        }
    }
}
