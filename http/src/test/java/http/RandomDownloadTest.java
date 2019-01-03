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

package http;import com.google.gson.JsonParser;
import net.daporkchop.lib.common.test.TestRandomData;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.http.SimpleHTTP;
import org.junit.Test;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class RandomDownloadTest {
    @Test
    public void test() throws IOException {
        String data = SimpleHTTP.getString("https://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json");
        if (false) {
            System.out.println(data);
        } else {
            if (!data.trim().endsWith("}")) {
                throw new IllegalStateException();
            }
        }
    }

    @Test
    public void test2() throws IOException {
        String data = SimpleHTTP.getString(
                "https://www.daporkchop.net/contact",
                "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/70.0.3538.77 Chrome/70.0.3538.77 Safari/537.36"
        );
        if (false) {
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
