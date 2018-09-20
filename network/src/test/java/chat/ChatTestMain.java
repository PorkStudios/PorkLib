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

import chat.protocol.ChatProtocol;
import chat.protocol.MessagePacket;
import chat.protocol.SetNamePacket;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.encoding.compression.EnumCompression;
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
public class ChatTestMain {
    public static void main(String... args) {
        PorkServer<ChatSession> server = null;
        PorkClient<ChatSession> client = null;

        try {
            server = new ServerBuilder<ChatSession>()
                    .setCompression(EnumCompression.GZIP)
                    .setCryptographySettings(new CryptographySettings(
                            CurveType.brainpoolp256t1,
                            BlockCipherType.AES,
                            BlockCipherMode.CBC,
                            BlockCipherPadding.PKCS7
                    ))
                    .setAddress(new InetSocketAddress(12346))
                    .setProtocol(new ChatProtocol() {
                        @Override
                        public ChatSession newSession() {
                            return new ChatSession(); //don't set the name on the server
                        }
                    })
                    .build();

            client = new ClientBuilder<ChatSession>()
                    .setAddress(new InetSocketAddress("localhost", 12346))
                    .addListener(new EndpointListener<ChatSession>() {
                        @Override
                        public void onConnect(ChatSession session) {
                            session.send(new SetNamePacket(session.name));
                        }

                        @Override
                        public void onDisconnect(ChatSession sesion, String reason) {
                        }

                        @Override
                        public void onReceieve(ChatSession session, Packet packet) {
                        }
                    })
                    .setProtocol(new ChatProtocol() {
                        @Override
                        public ChatSession newSession() {
                            return new ChatSession(String.format("User #%d", ThreadLocalRandom.current().nextInt(0, 100)));
                        }
                    })
                    .build();

            Scanner scanner = new Scanner(System.in);
            String text;
            while (!(text = scanner.nextLine()).isEmpty()) {
                client.send(new MessagePacket(text));
            }
            scanner.close();
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
