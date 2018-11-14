/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.encoding.compression;

import lombok.NonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@Deprecated
public enum EnumCompression {
    NONE(b -> b, b -> b, b -> b, b -> b),
    GZIP(GZIPHelper::compress, GZIPHelper::inflate, GZIPHelper::deflateStream, GZIPHelper::inflateStream),
    XZIP(XZIPHelper::compress, XZIPHelper::inflate, XZIPHelper::deflateStream, XZIPHelper::inflateStream),
    ZLIB(ZLIBHelper::compress, ZLIBHelper::inflate, ZLIBHelper::deflateStream, ZLIBHelper::inflateStream);

    private static final ThreadLocal<WeakReference<byte[]>> buf = ThreadLocal.withInitial(() -> {
        byte[] b = new byte[1024];
        return new WeakReference<>(b);
    });

    private final Function<byte[], byte[]> compress;
    private final Function<byte[], byte[]> inflate;
    private final Function<OutputStream, OutputStream> compressStream;
    private final Function<InputStream, InputStream> inflateStream;

    EnumCompression(Function<byte[], byte[]> compress, Function<byte[], byte[]> inflate, Function<OutputStream, OutputStream> compressStream, Function<InputStream, InputStream> inflateStream) {
        this.compress = compress;
        this.inflate = inflate;
        this.compressStream = compressStream;
        this.inflateStream = inflateStream;
    }

    static byte[] getBuf() {
        WeakReference<byte[]> reference = buf.get();
        byte[] bytes = reference.get();
        if (bytes == null) {
            bytes = new byte[1024];
            buf.set(new WeakReference<>(bytes));
        }
        return bytes;
    }

    public byte[] compress(@NonNull byte[] input) {
        return this.compress.apply(input);
    }

    public byte[] inflate(@NonNull byte[] input) {
        return this.inflate.apply(input);
    }

    public OutputStream compressStream(@NonNull OutputStream stream) {
        return this.compressStream.apply(stream);
    }

    public InputStream inflateStream(@NonNull InputStream stream) {
        return this.inflateStream.apply(stream);
    }
}
