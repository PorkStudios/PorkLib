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

package net.daporkchop.lib.network.endpoint.server;

import lombok.Data;
import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.PaddingScheme;
import net.daporkchop.lib.crypto.sig.ec.ECCurves;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import org.apache.mina.core.service.IoAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Abstract code shared between server implementations
 *
 * @author DaPorkchop_
 */
@Data
public abstract class Server extends AbstractEndpoint {
    /**
     * The compression type to use
     */
    @NonNull
    private final EnumCompression compression;
    /**
     * The encryption algorithm to use
     */
    @NonNull
    private final BlockCipherType cipherType;
    /**
     * The mode of the encryption
     * <p>
     * If encryption is disabled ({@link #cipherType} is set to {@link BlockCipherType#NONE}), this field is null
     */
    private final BlockCipherMode cipherMode;
    /**
     * The padding scheme to use for encryption
     * <p>
     * If encryption is disabled ({@link #cipherType} is set to {@link BlockCipherType#NONE}), this field is null
     */
    private final PaddingScheme cipherPadding;
    /**
     * The elliptic curve type to use for ECDH key exchange
     * <p>
     * If encryption is disabled ({@link #cipherType} is set to {@link BlockCipherType#NONE}), this field is null
     */
    private final ECCurves curveType;

    @NonNull
    private final IoAcceptor ioAcceptor;

    public Server(@NonNull ServerBuilder builder, @NonNull IoAcceptor ioAcceptor) {
        super(builder.getTransmissionProtocol(), builder.getPacketProtocol(), builder.getPassword());

        this.ioAcceptor = ioAcceptor;
        this.populateService(this.ioAcceptor);

        this.compression = builder.getCompression();
        this.cipherType = builder.getCipherType();
        this.cipherMode = builder.getCipherMode();
        this.cipherPadding = builder.getCipherPadding();
        this.curveType = builder.getCurveType();

        //start async generation if not already generated
        getECKeyPair(this.getCurveType());

        try {
            this.getIoAcceptor().bind(new InetSocketAddress(builder.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to start server", e);
        }
    }

    @Override
    public void close() {
        this.ioAcceptor.setCloseOnDeactivation(true);
        this.ioAcceptor.getManagedSessions().values().forEach(session -> {
            System.out.println("Server closing session " + session.toString());
            SessionData.ENCAPSULATED_SESSION.<EncapsulatedSession>get(session).close("Server closed", true);
            session.closeNow().awaitUninterruptibly();
        });
        this.ioAcceptor.unbind();
        this.ioAcceptor.dispose(true);
    }

    @Override
    public boolean isServer() {
        return true;
    }
}
