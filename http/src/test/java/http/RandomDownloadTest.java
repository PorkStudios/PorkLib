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
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.client.builder.BlockingRequestBuilder;
import net.daporkchop.lib.http.client.request.BlockingRequest;
import net.daporkchop.lib.http.impl.java.client.JavaHttpClient;
import net.daporkchop.lib.http.util.StatusCodes;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author DaPorkchop_
 */
public class RandomDownloadTest {
    public static final boolean DEBUG_PRINT = false;

    @Test
    public void test() throws IOException {
        final String url = "http://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json";
        String data = SimpleHTTP.getString(url);
        String data2;

        {
            HttpClient client = new JavaHttpClient();
            String theUrl = url;
            BlockingRequestBuilder requestBuilder = client.prepareBlocking();
            BlockingRequest request = null;
            do {
                if (request != null) request.close();
                request = requestBuilder.configure(theUrl).send();
                System.out.printf("Sending request to \"%s\"...\n", theUrl);
            } while (request.isRedirect() && (theUrl = request.redirectUrl()) != null);

            System.out.println(request.status());
            System.out.println("Headers:");
            request.headers().forEach(System.out::println);
            System.out.print("\n\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream in = request.input()) {
                for (int b; (b = in.read()) != -1; ) {
                    baos.write(b);
                }
            } finally {
                request.close();
                client.close();
            }
            data2 = new String(baos.toByteArray());
        }
        if (DEBUG_PRINT) {
            System.out.println(data);
        }
        if (!data.trim().endsWith("}")) {
            throw new IllegalStateException();
        } else if (!data2.trim().endsWith("}")) {
            throw new IllegalStateException();
        } else if (!data2.equals(data)) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void test2() throws IOException {
        String data = SimpleHTTP.getString(
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
        String response = new JsonParser().parse(SimpleHTTP.getString(String.format("http://httpbin.org/get?data=%s", text))).getAsJsonObject().get("args").getAsJsonObject().get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }

    @Test
    public void testPOST() throws IOException {
        String text = String.format("{\"value\":\"%s\"}", Base58.encodeBase58(TestRandomData.getRandomBytes(64, 128)));
        String response = new JsonParser().parse(SimpleHTTP.postJsonAsString("http://httpbin.org/post", text)).getAsJsonObject().get("data").getAsString();
        if (!text.equals(response)) {
            throw new IllegalStateException(String.format("Data not identical! Sent=%s Received=%s", text, response));
        }
    }
}
