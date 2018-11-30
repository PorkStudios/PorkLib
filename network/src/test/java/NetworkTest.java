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

import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.block.CipherMode;
import net.daporkchop.lib.crypto.cipher.block.CipherPadding;
import net.daporkchop.lib.crypto.cipher.block.CipherType;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.protocol.api.ProtocolManager;
import net.daporkchop.lib.network.protocol.netty.sctp.SctpProtocolManager;
import net.daporkchop.lib.network.protocol.netty.tcp.TcpProtocolManager;
import net.daporkchop.lib.network.protocol.raknet.RakNetProtocolManager;
import org.junit.Test;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
public class NetworkTest implements Logging {
    private static final Collection<ProtocolManager> MANAGERS = Arrays.asList(
            null
            , TcpProtocolManager.INSTANCE
            //, RakNetProtocolManager.INSTANCE
            , SctpProtocolManager.INSTANCE
    );

    static {
        logger.setLevel(4);
        logger.add(new File("./test_out/test_network.log"), true);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {
        MANAGERS.forEach(manager -> {
            if (manager == null)    {
                return;
            }

            logger.info("Testing transport: ${0}", manager.getClass());
            logger.info("Starting server...");
            Server server = new ServerBuilder()
                    .setManager(manager)
                    .setAddress(new InetSocketAddress("0.0.0.0", 12345))
                    .addProtocol(TestProtocol.INSTANCE)
                    .setCompression(Compression.GZIP_HIGH)
                    .setCryptographySettings(new CryptographySettings(
                            CurveType.B_283,
                            CipherType.BLOWFISH,
                            CipherMode.CBC,
                            CipherPadding.PKCS7
                    ))
                    .build();
            logger.info("Server started.");

            logger.info("Starting client...");
            Client client = new ClientBuilder()
                    .setManager(manager)
                    .setAddress(new InetSocketAddress("localhost", 12345))
                    .addProtocol(TestProtocol.INSTANCE)
                    .build();
            logger.info("Client started.");

            {
                int count = 3;
                logger.info("Sending ${0} random packets...", count);
                for (int i = 0; i < count; i++) {
                    sleep(75L);
                    client.getDefaultChannel().send(new TestPacket("hello from client!"), true);
                    server.broadcast(new TestPacket("hello from server!"));
                }
                server.broadcast(
                        new TestPacket("\nI'd just like to interject for moment. What you're referring to as Linux, is in fact, GNU/Linux, or as I've recently taken to calling it, GNU plus Linux. Linux is not an operating system unto itself, but rather another free component of a fully functioning GNU system made useful by the GNU corelibs, shell utilities and vital system components comprising a full OS as defined by POSIX.\n\nMany computer users run a modified version of the GNU system every day, without realizing it. Through a peculiar turn of events, the version of GNU which is widely used today is often called Linux, and many of its users are not aware that it is basically the GNU system, developed by the GNU Project.\n\nThere really is a Linux, and these people are using it, but it is just a part of the system they use. Linux is the kernel: the program in the system that allocates the machine's resources to the other programs that you run. The kernel is an essential part of an operating system, but useless by itself; it can only function in the context of a complete operating system. Linux is normally used in combination with the GNU operating system: the whole system is basically GNU with Linux added, or GNU/Linux. All the so-called Linux distributions are really distributions of GNU/Linux!"),
                        true
                );
            }

            logger.info("Closing...");
            client.close("client closing...");
            server.close();

            sleep(1000L);
            logger.info("Done!");
        });
    }
}
