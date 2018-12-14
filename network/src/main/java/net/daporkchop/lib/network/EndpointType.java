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

package net.daporkchop.lib.network;

/**
 * Used to differentiate the various endpoint types
 *
 * @author DaPorkchop_
 */
public enum EndpointType {
    /**
     * An endpoint that has a single outgoing connection.
     * <p>
     * A client can connect to either a {@link #SERVER} or {@link #MULTI} endpoint
     */
    CLIENT,
    /**
     * An endpoint that accepts incoming connections.
     * <p>
     * A server can accept connections from either a {@link #CLIENT} or {@link #MULTI} endpoint
     */
    SERVER,
    /**
     * A mixture of {@link #CLIENT} and {@link #SERVER}.
     * <p>
     * Multi endpoints can accept incoming connections and connect to multiple remote endpoints at the same time.
     */
    MULTI,
    /**
     * An endpoint designed for use in p2p (peer-to-peer) applications.
     * <p>
     * Unlike {@link #MULTI}, p2p endpoints automagically exchange peer IDs with each other in order to build up a
     * decentralized swarm. Additionally, they can only connect with other p2p endpoints.
     */
    P2P;
}
