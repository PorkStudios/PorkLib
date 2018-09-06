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

package chat;

import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.builder.ClientBuilder;
import net.daporkchop.lib.network.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * @author DaPorkchop_
 */
public class ChatTest {
    /*@Test
    public void test() throws InterruptedException {
        main();
    }*/

    public static void main(String... args) {
        Server server = null;
        Client client = null;
        try {
            out.println("Starting server...");
            server = new ServerBuilder()
                    .setPort(12345)
                    .setPacketProtocol(new ChatProtocol())
                    .setCompression(EnumCompression.GZIP)
                    .setCipherType(BlockCipherType.AES)
                    .setPassword("asdfaa")
                    .build();

            out.println("Started server!");
            out.println("Starting client...");

            client = new ClientBuilder()
                    .setAddress(new InetSocketAddress("localhost", 12345))
                    .setPacketProtocol(new ChatProtocol())
                    .setPassword("asdfaa")
                    .build();

            out.println("Started client!");

            Scanner scanner = new Scanner(System.in);
            String text;
            while (!(text = scanner.nextLine().trim()).isEmpty()) {
                ChatPacket packet = new ChatPacket(text);
                client.send(packet);
            }
            scanner.close();
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        } finally {
            if (client != null) {
                client.close("Closed!");
            }
            if (server != null) {
                server.close();
            }
        }
    }
}
