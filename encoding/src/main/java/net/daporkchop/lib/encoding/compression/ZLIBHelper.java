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

package net.daporkchop.lib.encoding.compression;

import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * @author DaPorkchop_
 */
@Deprecated
public class ZLIBHelper {
    public static byte[] compress(byte[] in) {
        byte[] result = new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length);
             DeflaterOutputStream dos = new DeflaterOutputStream(baos) {{
                 this.def.setLevel(Deflater.BEST_COMPRESSION);
             }}) {
            dos.write(in);
            dos.close();
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] inflate(byte[] in) {
        byte[] result = new byte[0];
        try (ByteArrayInputStream bais = new ByteArrayInputStream(in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InflaterInputStream dis = new InflaterInputStream(bais)) {
            byte[] buf = EnumCompression.getBuf();
            int len;
            while ((len = dis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static OutputStream deflateStream(@NonNull OutputStream stream) {
        return new DeflaterOutputStream(stream) {{
            this.def.setLevel(Deflater.BEST_COMPRESSION);
        }};
    }

    public static InputStream inflateStream(@NonNull InputStream stream) {
        return new InflaterInputStream(stream);
    }
}
