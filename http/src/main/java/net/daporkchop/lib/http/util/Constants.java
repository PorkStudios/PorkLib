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

package net.daporkchop.lib.http.util;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.selection.SelectionPool;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.StatusCode;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Contains various constant values used frequently throughout the library.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Constants {
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

    public final String                USER_AGENT                        = "PorkLib/" + PorkUtil.PORKLIB_VERSION;
    public final SelectionPool<String> DEFAULT_USER_AGENT_SELECTION_POOL = SelectionPool.singleton(USER_AGENT);

    // The maximum length of the query string
    public final int MAX_QUERY_SIZE = 1 << 12; // 4 KiB
    // The maximum length of a single header line (including name)
    public final int MAX_HEADER_SIZE = 1 << 13; // 8 KiB
    // The maximum number of headers per request
    public final int MAX_HEADER_COUNT = 256;

    // The maximum length of a request (query string + headers)
    public final int MAX_REQUEST_SIZE = (32 + MAX_QUERY_SIZE) + MAX_HEADER_SIZE * MAX_HEADER_COUNT;

    /*public final ThreadLocal<byte[]> CACHE_4KB_BUFFER = ThreadLocal.withInitial(() -> new byte[4096]);

    public final Pattern PATTERN_REQUEST = Pattern.compile("^([A-Z]+) ([^ ]+) HTTP/1\\.1$");
    public final Pattern PATTERN_HEADER = Pattern.compile("([\\x20-\\x7E]+): ([\\x20-\\x7E]+)");

    //TODO: neither of these patterns will work with ipv6 addresses
    public final Pattern PATTERN_URL_WITH_PORT = Pattern.compile("^http(s{0,1}):\\/\\/([^:]+):([0-9]{1,4}|[0-5][0-9]{4}|6[0-5]{2}[0-3][0-5])(\\/.+)$");
    public final Pattern PATTERN_URL_NO_PORT = Pattern.compile("^http(s{0,1}):\\/\\/([^\\/]+)(\\/.+)$");

    public final byte[] BYTES_HTTP1_1 = {(byte) 'H', (byte) 'T', (byte) 'T', (byte) 'P', (byte) '/', (byte) '1', (byte) '.', (byte) '1'};
    public final byte[] BYTES_CRLF = {(byte) '\r', (byte) '\n'};
    public final byte[] BYTES_2X_CRLF = {(byte) '\r', (byte) '\n', (byte) '\r', (byte) '\n'};
    public final byte[] BYTES_HEADER_SEPARATOR = {(byte) ':', (byte) ' '};

    public void writeUTF16ToByteBuf(@NonNull ByteBuf dst, @NonNull CharSequence str) {
        if (str instanceof String) {
            writeUTF16ToByteBuf(dst, (String) str);
        } else {
            for (int i = 0, len = str.length(); i < len; i++) {
                dst.writeChar(str.charAt(i));
            }
        }
    }

    public void writeUTF16ToByteBuf(@NonNull ByteBuf dst, @NonNull String str) {
        byte[] buf = CACHE_4KB_BUFFER.get();
        char[] src = PUnsafe.getObject(str, PorkUtil.STRING_VALUE_OFFSET);
        int remaining = src.length * PUnsafe.ARRAY_CHAR_INDEX_SCALE;
        while (remaining > 0) {
            int count = min(remaining, buf.length);
            int i = remaining - count;
            remaining -= count;
            PUnsafe.copyMemory(src, PUnsafe.ARRAY_CHAR_BASE_OFFSET + i, buf, PUnsafe.ARRAY_BYTE_BASE_OFFSET + i, count);
            dst.writeBytes(buf, 0, count);
        }
    }*/

    /*public <I extends RequestFactory<I>> void prepareRequestBuilderForUrl(@NonNull I builder, @NonNull CharSequence url) {
        Matcher matcher = PATTERN_URL_WITH_PORT.matcher(url);
        if (matcher.find()) {
            builder.host(matcher.group(2))
                    .port(Integer.parseInt(matcher.group(3)))
                    .path(matcher.group(4))
                    .https(!matcher.group(1).isEmpty());
        } else if ((matcher = PATTERN_URL_NO_PORT.matcher(url)).find()) {
            boolean https = !matcher.group(1).isEmpty();
            builder.host(matcher.group(2))
                    .port(https ? 443 : 80)
                    .path(matcher.group(3))
                    .https(https);
        } else {
            throw new IllegalArgumentException(String.format("Not a valid http(s) URL: %s", url));
        }
    }*/

    //protected static final Pattern URL_PATTERN = Pattern.compile("^(https?):\\/\\/([^.]+\\...*?)(:?([0-9]{1,5}))?(\\/.*)?$");

    public static URL encodeUrl(@NonNull String url) throws MalformedURLException, UnsupportedEncodingException {
        try {
            URL theURl = new URL(url);
            URI uri = new URI(theURl.getProtocol(), theURl.getUserInfo(), theURl.getHost(), theURl.getPort(), theURl.getPath(), theURl.getQuery(), theURl.getRef());
            return new URL(uri.toASCIIString());
        } catch (URISyntaxException e)  {
            throw new IllegalArgumentException(url, e);
        }
        /*Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new MalformedURLException(url);
        }

        String protocol = matcher.group(1);

        String portTxt = matcher.group(4);
        int port;
        if (portTxt != null)    {
            port = Integer.parseUnsignedInt(portTxt);
        } else if (protocol.length() == 4) {
            port = 80;
        } else {
            port = 443;
        }

        String path = matcher.group(5);
        return new URL(protocol, matcher.group(2), port, path == null ? "" : URLEncoder.encode(path, "UTF-8"));*/
    }
}
