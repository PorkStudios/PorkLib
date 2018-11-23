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
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author DaPorkchop_
 */
@Deprecated
public class XZIPHelper {
    public static byte[] compress(@NonNull byte[] in) {
        byte[] result = new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length);
             XZOutputStream xos = new XZOutputStream(baos, new LZMA2Options())) {
            xos.write(in);
            xos.close();
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] inflate(@NonNull byte[] in) {
        byte[] result = new byte[0];
        try (ByteArrayInputStream bais = new ByteArrayInputStream(in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XZInputStream xis = new XZInputStream(bais)) {
            byte[] buf = EnumCompression.getBuf();
            int len;
            while ((len = xis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static OutputStream deflateStream(@NonNull OutputStream stream) {
        try {
            return new XZOutputStream(stream, new LZMA2Options());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to initialize GZIP output stream");
        }
    }

    public static InputStream inflateStream(@NonNull InputStream stream) {
        try {
            return new XZInputStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to initialize GZIP input stream (invalid GZIP headers?)");
        }
    }
}
