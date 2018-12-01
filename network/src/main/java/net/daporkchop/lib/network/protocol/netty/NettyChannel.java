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

package net.daporkchop.lib.network.protocol.netty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.network.channel.ChannelImplementation;
import net.daporkchop.lib.network.util.PacketReprocessor;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class NettyChannel implements ChannelImplementation {
    private final PacketReprocessor packetReprocessor = new PacketReprocessor(this);
    @Setter
    private volatile boolean encryptionReady;

    @Override
    public boolean isEncrypted() {
        return this.packetReprocessor.getCipher() != null && this.encryptionReady;
    }

    @Override
    public synchronized void startEncryption(@NonNull CryptographySettings cryptographySettings) {
        if (this.packetReprocessor.getCryptographySettings() != null)   {
            //cryptography settings will be set even before encryption is completely enabled
            throw new IllegalStateException("encryption already enabled");
        }
        this.packetReprocessor.setCryptographySettings(cryptographySettings);

    }
}
