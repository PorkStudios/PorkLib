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

package overflow;

import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.network.endpoint.EndpointListener;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.packet.Packet;
import overflow.protocol.BigPacket;
import overflow.protocol.OverflowProtocol;
import overflow.protocol.TinyPacket;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class OverflowTestMain {
    public static final byte[] RANDOM_DATA = new byte[1 << 20];

    static {
        ThreadLocalRandom.current().nextBytes(RANDOM_DATA);
    }

    public static void main(String... args) {
        PorkServer<OverflowSession> server = null;
        PorkClient<OverflowSession> client = null;

        try {
            server = new ServerBuilder<OverflowSession>()
                    //.setCompression(EnumCompression.GZIP)
                    .setCryptographySettings(new CryptographySettings(
                            CurveType.brainpoolp256t1,
                            BlockCipherType.AES,
                            BlockCipherMode.CBC,
                            BlockCipherPadding.PKCS7
                    ))
                    .setAddress(new InetSocketAddress(12346))
                    .setProtocol(new OverflowProtocol())
                    .build();

            client = new ClientBuilder<OverflowSession>()
                    .setAddress(new InetSocketAddress("localhost", 12346))
                    .addListener(new EndpointListener<OverflowSession>() {
                        @Override
                        public void onConnect(OverflowSession session) {
                            synchronized (RANDOM_DATA) {
                                RANDOM_DATA.notifyAll();
                            }
                        }

                        @Override
                        public void onDisconnect(OverflowSession sesion, String reason) {
                        }

                        @Override
                        public void onReceieve(OverflowSession session, Packet packet) {
                        }
                    })
                    .setProtocol(new OverflowProtocol())
                    .build();

            synchronized (RANDOM_DATA) {
                RANDOM_DATA.wait();
            }

            for (int i = 1; false && i <= 1024; i <<= 1) {
                System.out.printf("Sending small packets (multiplier: x%d)\n", i);
                TinyPacket packet = new TinyPacket(i);
                for (int j = 1 << 15; j > 0; j--) {
                    client.send(packet);
                }
                System.out.println("Sent!");
            }
            for (int i = 128; i > 0; i--) {
                System.out.println("Sending 1MB packet...");
                client.send(new BigPacket(RANDOM_DATA));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (server != null) {
                    server.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
