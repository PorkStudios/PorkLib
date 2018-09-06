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

package net.daporkchop.lib.minecraft.bedrock.client;

import io.gomint.jraknet.ClientSocket;
import io.gomint.jraknet.SocketEvent;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.api.PingData;
import net.daporkchop.lib.minecraft.api.exception.AlreadyClosedException;
import net.daporkchop.lib.minecraft.api.exception.AlreadyConnectedException;
import net.daporkchop.lib.minecraft.api.exception.IllegalProtocolException;
import net.daporkchop.lib.minecraft.common.BaseClient;
import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author DaPorkchop_
 */
public class BedrockClient extends BaseClient {
    @NonNull
    private final ClientSocket socket;

    final Map<SocketAddress, CompletableFuture<SocketEvent.PingPongInfo>> queuedPings = new ConcurrentHashMap<>();

    public BedrockClient(Protocol protocol) {
        super(protocol);
        if (protocol.getPlatform() != this.getPlatform()) {
            throw new IllegalProtocolException(protocol);
        }

        this.socket = new ClientSocket();
        this.socket.setMojangModificationEnabled(true);
        this.socket.setEventHandler(new ClientEventHandler(this));
        try {
            this.socket.initialize();
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.BEDROCK;
    }

    @Override
    public boolean isRunning() {
        synchronized (this.connected) {
            return this.connected.get();
        }
    }

    @Override
    public void disconnect() {
        synchronized (this.connected)   {
            if (!this.isRunning() && !this.socket.isInitialized())  {
                throw new AlreadyClosedException();
            }
            this.connected.set(false);
            this.socket.close();
        }
    }

    @Override
    public void connect(@NonNull InetSocketAddress address) {
        synchronized (this.connected)   {
            if (this.isRunning())   {
                throw new AlreadyConnectedException();
            }
            this.socket.connect(address);
        }
    }

    @Override
    public synchronized PingData ping(@NonNull InetSocketAddress address) {
        CompletableFuture<SocketEvent.PingPongInfo> future;
        synchronized (this.queuedPings) {
            if (this.queuedPings.containsKey(address)) {
                throw new IllegalStateException("Ping already queued for address: " + address.toString());
            }
            future = new CompletableFuture<>();
            this.queuedPings.put(address, future);
        }
        this.socket.ping(address);
        try {
            SocketEvent.PingPongInfo info = future.get(10L, TimeUnit.SECONDS);
            String[] split = info.getMotd().split(";");
            System.out.println(info.getMotd());
            if (split.length != 9 || !"MCPE".equals(split[0])) {
                throw new IllegalStateException("Invalid MOTD: " + info.getMotd() + "(semicolon count:" + (split.length - 1) + ")");
            }
            int protocol = Integer.parseInt(split[2]), online = Integer.parseInt(split[4]), max = Integer.parseInt(split[5]);
            return new PingData(split[1], protocol, Protocol.getByNetworkVersion(protocol, Platform.BEDROCK), info.getPingTime(), online, max);
        } catch (TimeoutException e)    {
            return null;
        } catch (Throwable t)   {
            throw new IllegalStateException(t);
        } finally {
            this.queuedPings.remove(address);
        }
    }
}
