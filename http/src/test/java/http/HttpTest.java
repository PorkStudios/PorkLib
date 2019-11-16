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

package http;

import com.google.gson.JsonParser;
import net.daporkchop.lib.common.test.TestRandomData;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.http.Http;
import net.daporkchop.lib.http.impl.java.JavaHttpClient;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class HttpTest {
    public static final boolean DEBUG_PRINT = false;

    @Test
    public void test() throws IOException {
        final String url = "http://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json";
        String data2;

        JavaHttpClient client = new JavaHttpClient();
        try {
            if (true)   {
                client.request().url(url)
                        .followRedirects(true)
                        .downloadToFile(new File("/home/daporkchop/Desktop/test/colors.json"), true)
                        .send()
                        .syncBody();
                return;
            }

            data2 = client.request().url(url)
                    .followRedirects(true)
                    .aggregateToString()
                    .send()
                    .syncBodyAndGet().value();
        } finally {
            client.close().syncUninterruptibly();
        }

        String data = Http.getString(url);

        if (DEBUG_PRINT) {
            System.out.println(data);
        }
        if (!data.trim().endsWith("}")) {
            throw new IllegalStateException();
        } else if (!data2.trim().endsWith("}")) {
            throw new IllegalStateException();
        } else if (!data.equals(data2)) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void test2() throws IOException {
        String data = Http.getString(
                "https://www.daporkchop.net/contact",
                "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/70.0.3538.77 Chrome/70.0.3538.77 Safari/537.36"
        );
        if (DEBUG_PRINT) {
            System.out.println(data);
        }
    }

    @Test
    public void testGET() throws IOException {
        String text = Base58.encodeBase58(TestRandomData.getRandomBytes(64, 128));
        String response = new JsonParser().parse(Http.getString(String.format("http://httpbin.org/get?data=%s", text))).getAsJsonObject().get("args").getAsJsonObject().get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }

    @Test
    public void testPOST() throws IOException {
        String text = String.format("{\"value\":\"%s\"}", Base58.encodeBase58(TestRandomData.getRandomBytes(64, 128)));
        String response = new JsonParser().parse(Http.postJsonAsString("http://httpbin.org/post", text)).getAsJsonObject().get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }
}
