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

package http;

import com.google.gson.JsonParser;
import net.daporkchop.lib.common.misc.TestRandomData;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.http.Http;
import net.daporkchop.lib.http.header.Header;
import org.junit.Test;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class HttpTest {
    public static final boolean DEBUG_PRINT = false;

    @Test
    public void test() throws IOException {
        final String url = "http://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json";
        String data = Http.getString(url);

        if (DEBUG_PRINT) {
            System.out.println(data);
        }
        new JsonParser().parse(data);
    }

    @Test
    public void test2() throws IOException {
        String data = Http.getString(
                "https://www.daporkchop.net/contact",
                Header.of("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/70.0.3538.77 Chrome/70.0.3538.77 Safari/537.36")
        );
        if (DEBUG_PRINT) {
            System.out.println(data);
        }
    }

    @Test
    public void testGET() throws IOException {
        String text = Base58.encodeBase58(TestRandomData.getRandomBytes(64, 128));
        String response = new JsonParser().parse(Http.getString(String.format("http://httpbin.org/get?data=%s", text))).getAsJsonObject()
                .get("args").getAsJsonObject()
                .get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }

    @Test
    public void testPOST() throws IOException {
        String text = String.format("{\"value\":\"%s\"}", Base58.encodeBase58(TestRandomData.getRandomBytes(64, 128)));
        String response = Http.postJsonString("http://httpbin.org/post", text);
        response = new JsonParser().parse(response).getAsJsonObject().get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }
}
