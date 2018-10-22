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

package net.daporkchop.lib.network.endpoint.builder;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.endpoint.server.PorkServer;

@Accessors(chain = true)
@Getter
@Setter
public class ServerBuilder<S extends Session> extends AbstractBuilder<ServerBuilder<S>, S, PorkServer<S>> {
    @NonNull
    private CryptographySettings encryption = new CryptographySettings();

    private int readerThreads = 2;

    private int maxConnections = 100;

    @Override
    protected PorkServer<S> doBuild() {
        if (this.readerThreads <= 0)    {
            throw new IllegalArgumentException(String.format("Invalid reader thread count: %d (must be at least 1)", this.readerThreads));
        } else if (this.maxConnections <= 0)    {
            throw new IllegalArgumentException(String.format("Invalid maximum connection count: %d (must be at least 1)", this.maxConnections));
        }
        return new PorkServer<>(this);
    }
}
