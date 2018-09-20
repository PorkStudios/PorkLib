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

package big;

import big.protocol.BigProtocol;
import big.protocol.BigPacket;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.EndpointListener;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.packet.Packet;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class BigTestMain {
    public static final byte[] RANDOM_DATA = new byte[1_000_000];

    static {
        ThreadLocalRandom.current().nextBytes(RANDOM_DATA);
    }

    public static void main(String... args) {
        Endpoint.setWRITE_BUFFER_SIZE(2_000_000);
        Endpoint.setOBJECT_BUFFER_SIZE(2_000_000);

        PorkServer<BigSession> server = null;
        PorkClient<BigSession> client = null;

        try {
            server = new ServerBuilder<BigSession>()
                    .setCompression(EnumCompression.GZIP)
                    .setCryptographySettings(new CryptographySettings(
                            CurveType.brainpoolp256t1,
                            BlockCipherType.AES,
                            BlockCipherMode.CBC,
                            BlockCipherPadding.PKCS7
                    ))
                    .setAddress(new InetSocketAddress(12346))
                    .setProtocol(new BigProtocol())
                    .build();

            client = new ClientBuilder<BigSession>()
                    .setAddress(new InetSocketAddress("localhost", 12346))
                    .addListener(new EndpointListener<BigSession>() {
                        @Override
                        public void onConnect(BigSession session) {
                            session.send(new BigPacket(RANDOM_DATA));
                        }

                        @Override
                        public void onDisconnect(BigSession sesion, String reason) {
                        }

                        @Override
                        public void onReceieve(BigSession session, Packet packet) {
                        }
                    })
                    .setProtocol(new BigProtocol())
                    .build();

            Scanner scanner = new Scanner(System.in);
            while (!scanner.nextLine().isEmpty())  {
            }
            scanner.close();
        } catch (Exception e)   {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (Exception e)   {
                e.printStackTrace();
            }
            try {
                if (server != null) {
                    server.close();
                }
            } catch (Exception e)   {
                e.printStackTrace();
            }
        }
    }
}
