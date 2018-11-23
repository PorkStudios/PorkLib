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

import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.protocol.pork.DisconnectPacket;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class NetworkTest {
    @Test
    public void test() throws InterruptedException {
        System.out.println("Starting server...");
        PorkServer server = new ServerBuilder<TestConnection>()
                .setAddress(new InetSocketAddress("0.0.0.0", 12345))
                .addProtocol(TestProtocol.INSTANCE)
                .build();
        System.out.println("Server started.");
        Thread.sleep(1000L);
        System.out.println("Starting client...");
        PorkClient client = new ClientBuilder<TestConnection>()
                .setAddress(new InetSocketAddress("localhost", 12345))
                .addProtocol(TestProtocol.INSTANCE)
                .build();
        System.out.println("Client started.");
        Thread.sleep(1000L);

        //client.send("name jef lol");

        /*try (Scanner scanner = new Scanner(System.in))  {
            scanner.nextLine();
        }*/

        System.out.println("Sending some random packets...");
        for (int i = 0; i < 1; i++)    {
            Thread.sleep(75L);
            client.send(new TestPacket("hello world!"));
        }

        System.out.println("Waiting...");
        Thread.sleep(1000L);
        /*for (int i = 0; i < 100; i++)   {
            client.send(new DisconnectPacket("jeff"));
        }*/

        System.out.println("Closing sessions...");
        client.close("ok lol");
        Thread.sleep(1000L);
        server.close();

        Thread.sleep(1000L);
        System.out.println("Done!");
    }
}
