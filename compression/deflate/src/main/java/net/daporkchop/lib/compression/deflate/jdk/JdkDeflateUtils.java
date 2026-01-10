/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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

package net.daporkchop.lib.compression.deflate.jdk;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.compression.context.PStreamingCompressor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class JdkDeflateUtils {
    public static int flushModeToJdk(PStreamingCompressor.FlushMode flush) {
        switch (flush) {
            case NO:
            case FINISH:
                return Deflater.NO_FLUSH;
            case SYNC:
                return Deflater.SYNC_FLUSH;
            case FULL:
                return Deflater.FULL_FLUSH;
            default:
                throw new IllegalArgumentException(String.valueOf(flush));
        }
    }

    public static boolean supportsByteBufferMethods() {
        return PlatformInfo.JAVA_VERSION >= 9;
    }

    @SneakyThrows
    public static void setInput(Deflater deflater, ByteBuffer buffer) {
        assert supportsByteBufferMethods();
        ByteBufferMethods.Deflater_setInput.invokeExact(deflater, buffer);
    }

    @SneakyThrows
    public static int deflate(Deflater deflater, ByteBuffer buffer, int flush) {
        assert supportsByteBufferMethods();
        return (int) ByteBufferMethods.Deflater_deflate.invokeExact(deflater, buffer, flush);
    }

    @SneakyThrows
    public static void setInput(Inflater inflater, ByteBuffer buffer) {
        assert supportsByteBufferMethods();
        ByteBufferMethods.Inflater_setInput.invokeExact(inflater, buffer);
    }

    @SneakyThrows
    public static int inflate(Inflater inflater, ByteBuffer buffer) {
        assert supportsByteBufferMethods();
        return (int) ByteBufferMethods.Inflater_inflate.invokeExact(inflater, buffer);
    }

    /**
     * @author DaPorkchop_
     */
    static final class ByteBufferMethods {
        private static final MethodHandle Deflater_setInput;
        private static final MethodHandle Deflater_deflate;
        private static final MethodHandle Inflater_setInput;
        private static final MethodHandle Inflater_inflate;

        static {
            try {
                Deflater_setInput = MethodHandles.lookup().findVirtual(Deflater.class, "setInput", MethodType.methodType(void.class, ByteBuffer.class));
                Deflater_deflate = MethodHandles.lookup().findVirtual(Deflater.class, "deflate", MethodType.methodType(int.class, ByteBuffer.class, int.class));
                Inflater_setInput = MethodHandles.lookup().findVirtual(Inflater.class, "setInput", MethodType.methodType(void.class, ByteBuffer.class));
                Inflater_inflate = MethodHandles.lookup().findVirtual(Inflater.class, "inflate", MethodType.methodType(int.class, ByteBuffer.class));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }
}
