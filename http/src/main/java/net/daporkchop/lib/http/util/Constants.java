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

package net.daporkchop.lib.http.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static net.daporkchop.lib.math.primitive.PMath.min;

/**
 * Contains various constant values used frequently throughout the library.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Constants {
    public final ThreadLocal<byte[]> CACHE_4KB_BUFFER = ThreadLocal.withInitial(() -> new byte[4096]);

    public final IntObjectMap<StatusCode> STATUS_CODES_BY_NUMERIC_ID = Stream.<StatusCode[]>of(StatusCodes.values())
            .flatMap(Arrays::stream)
            .collect(Collector.<StatusCode, IntObjectMap<StatusCode>>of(
                    IntObjectHashMap::new,
                    (map, code) -> map.put(code.code(), code),
                    (map1, map2) -> {
                        map2.forEach(map1::put);
                        return map1;
                    }
            ));

    public final Pattern PATTERN_REQUEST = Pattern.compile("^([A-Z]+) ([^ ]+) HTTP/1\\.1$");
    public final Pattern PATTERN_HEADER = Pattern.compile("([\\x20-\\x7E]+): ([\\x20-\\x7E]+)");

    public final byte[] BYTES_HTTP1_1 = {(byte) 'H', (byte) 'T', (byte) 'T', (byte) 'P', (byte) '/', (byte) '1', (byte) '.', (byte) '1'};
    public final byte[] BYTES_CRLF = {(byte) '\r', (byte) '\n'};
    public final byte[] BYTES_2X_CRLF = {(byte) '\r', (byte) '\n', (byte) '\r', (byte) '\n'};
    public final byte[] BYTES_HEADER_SEPARATOR = {(byte) ':', (byte) ' '};

    // The maximum length of the query string
    public final int MAX_QUERY_SIZE = 1 << 12; // 4 KiB
    // The maximum length of a single header line (including name)
    public final int MAX_HEADER_SIZE = 1 << 13; // 8 KiB
    // The maximum number of headers per request
    public final int MAX_HEADER_COUNT = 256;

    public void writeUTF16ToByteBuf(@NonNull ByteBuf dst, @NonNull CharSequence str) {
        if (str instanceof String)  {
            writeUTF16ToByteBuf(dst, (String) str);
        } else {
            for (int i = 0, len = str.length(); i < len; i++)   {
                dst.writeChar(str.charAt(i));
            }
        }
    }

    public void writeUTF16ToByteBuf(@NonNull ByteBuf dst, @NonNull String str) {
        byte[] buf = CACHE_4KB_BUFFER.get();
        char[] src = PUnsafe.getObject(str, PorkUtil.OFFSET_STRING_VALUE);
        int remaining = src.length * PUnsafe.ARRAY_CHAR_INDEX_SCALE;
        while (remaining > 0)   {
            int count = min(remaining, buf.length);
            int i = remaining - count;
            remaining -= count;
            PUnsafe.copyMemory(src, PUnsafe.ARRAY_CHAR_BASE_OFFSET + i, buf, PUnsafe.ARRAY_BYTE_BASE_OFFSET + i, count);
            dst.writeBytes(buf, 0, count);
        }
    }
}
