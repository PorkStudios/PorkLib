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

package net.daporkchop.lib.network.builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.TransmissionProtocol;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.endpoint.server.TCPServer;
import net.daporkchop.lib.network.protocol.PacketProtocol;

/**
 * @author DaPorkchop_
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ServerBuilder {
    @NonNull
    private EnumCompression compression = EnumCompression.NONE;

    @NonNull
    private BlockCipherType cipherType = BlockCipherType.NONE;

    @NonNull
    private BlockCipherMode cipherMode = BlockCipherMode.CBC;

    @NonNull
    private BlockCipherPadding cipherPadding = BlockCipherPadding.PKCS7;

    @NonNull
    private CurveType curveType = CurveType.brainpoolp192r1;

    @NonNull
    private TransmissionProtocol transmissionProtocol = TransmissionProtocol.TCP;

    @NonNull
    private PacketProtocol packetProtocol;

    /**
     * If not null, a matching password will be required on the client end in order to connect
     */
    private String password;

    private int port;

    public Server build() {
        if (this.transmissionProtocol == TransmissionProtocol.TCP) {
            return new TCPServer(this);
        } else {
            //TODO:
            throw new UnsupportedOperationException("UDP server");
        }
    }
}
