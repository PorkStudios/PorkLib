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

package protocol;

import io.netty.buffer.ByteBuf;
import net.daporkchop.lib.common.test.TestRandomData;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.packet.handler.MessageHandler;
import protocol.packet.SimpleTestPacket;
import protocol.packet.TestChannelsPacket;

import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
public class TestProtocol extends UserProtocol<TestConnection> {
    public static final TestProtocol INSTANCE = new TestProtocol();

    private TestProtocol() {
        super("Test", 1, 123);
    }

    @Override
    protected void registerPackets() {
        this.register(
                new SimpleTestPacket.MessageHandler(),
                new TestChannelsPacket.TestChannelsHandler()
        );
        this.register((msg, connection, channelId) -> {
                    int id = msg.readMedium();
                    int len = TestRandomData.randomBytes[id].length;
                    if (msg.readableBytes() != len) {
                        throw new IllegalStateException("Invalid data length!");
                    } else {
                        byte[] b = new byte[len];
                        msg.readBytes(b);
                        if(!Arrays.equals(b, TestRandomData.randomBytes[id]))   {
                            throw new IllegalStateException("Invalid data received!");
                        }
                    }
                }, 25);
    }

    @Override
    public TestConnection newConnection() {
        return new TestConnection();
    }
}
