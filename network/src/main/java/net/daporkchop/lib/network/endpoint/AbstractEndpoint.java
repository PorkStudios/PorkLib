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

package net.daporkchop.lib.network.endpoint;

import lombok.*;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.PaddingScheme;
import net.daporkchop.lib.crypto.exchange.ECDHHelper;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import net.daporkchop.lib.crypto.keygen.ec.ECDHKeyGen;
import net.daporkchop.lib.crypto.sig.ec.ECCurves;
import net.daporkchop.lib.network.TransmissionProtocol;
import net.daporkchop.lib.network.protocol.PacketProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedHandler;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionDataStructureFactory;
import net.daporkchop.lib.network.protocol.filter.keepalive.KeepAliveFactory;
import net.daporkchop.lib.network.protocol.filter.packet.PacketCodecFactory;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Common fields and methods shared by both client and server endpoints
 *
 * @author DaPorkchop_
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Setter(AccessLevel.PROTECTED)
@Getter
public abstract class AbstractEndpoint {
    private static final Executor executor = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors() << 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final Map<ECCurves, CompletableFuture<ECDHKeyPair>> keypairs = new ConcurrentHashMap<>();

    /**
     * The encapsulated protocol used for communicating with the network protocol
     */
    private final transient EncapsulatedProtocol encapsulatedProtocol = new EncapsulatedProtocol(this);
    /**
     * Whether or not this endpoint is currently in operation
     */
    protected transient volatile boolean open = true;
    /**
     * The transmission protocol to use
     */
    @NonNull
    private final TransmissionProtocol transmissionProtocol;
    /**
     * The packet protocol used for communicating
     */
    @NonNull
    private final PacketProtocol packetProtocol;

    /**
     * If not null on server end, a matching password will need to be sent by the client in order
     * to connect.
     */
    private final String password;

    /**
     * Gets a completablefuture for an ecdh key pair for the given key type. if there isn't already a key generated for the given
     * key type, a new one will be generated asynchronously.
     *
     * @param curve the curve type to be used
     * @return a completablefututure that points (or will point) to an ecdh key pair for the given curve type
     */
    public synchronized static CompletableFuture<ECDHKeyPair> getECKeyPair(@NonNull ECCurves curve) {
        CompletableFuture<ECDHKeyPair> future = keypairs.get(curve);
        if (future == null) {
            CompletableFuture<ECDHKeyPair> reee = future = new CompletableFuture<>();
            executor.execute(() -> {
                try {
                    reee.complete(ECDHKeyGen.gen(curve));
                } catch (Throwable t) {
                    reee.completeExceptionally(t);
                }
            });
            keypairs.put(curve, future);
        }
        return future;
    }

    public static ECDHKeyPair getECKeyPairNow(@NonNull ECCurves curve) {
        try {
            return getECKeyPair(curve).get();
        } catch (InterruptedException
                | ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start generating a common ECDH secret in the background using the given block cipher settings
     *
     * @param publicKey     the ecdh public key
     * @param privateKey    the ecdh private key
     * @param cipherType    the block cipher type (algorithm)
     * @param cipherMode    the block cipher mode
     * @param cipherPadding the block cipher padding scheme
     * @return a completable future that will point to a {@link BlockCipherHelper} with the given settings
     */
    public static CompletableFuture<BlockCipherHelper> getCipherHelper(@NonNull PublicKey publicKey, @NonNull PrivateKey privateKey, @NonNull BlockCipherType cipherType, @NonNull BlockCipherMode cipherMode, @NonNull PaddingScheme cipherPadding) {
        CompletableFuture<BlockCipherHelper> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                AbstractSymmetricKey key = ECDHHelper.generateKey(privateKey, publicKey, cipherType);
                future.complete(cipherType.createHelper(cipherMode, cipherPadding, key));
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /**
     * Shut down this endpoint completely, closing any open connections
     */
    public abstract void close();

    /**
     * Checks if this endpoint is a server
     *
     * @return whether or not this endpoint is a server
     */
    public abstract boolean isServer();

    protected final void populateService(@NonNull IoService service) {
        this.populateService(service, executor);
    }

    protected final void populateService(@NonNull IoService service, @NonNull Executor executor) {
        service.getFilterChain().addLast("threads", new ExecutorFilter(executor));
        service.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PacketCodecFactory(this)));
        service.getFilterChain().addLast("keepalive", new KeepAliveFilter(new KeepAliveFactory(), IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE, 2, 10));

        service.setHandler(new EncapsulatedHandler(this));

        service.setSessionDataStructureFactory(SessionDataStructureFactory.getInstance());
    }
}
