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

package net.daporkchop.lib.network.packet.encapsulated.handshake;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.network.packet.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.packet.encapsulated.EncapsulatedType;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
public class HandshakeInitPacket implements EncapsulatedPacket {
    public CryptographySettings cryptographySettings;
    public EnumCompression compression;

    @Override
    public void read(DataIn in) throws IOException {
        this.cryptographySettings = new CryptographySettings();
        this.cryptographySettings.read(in);
        this.compression = EnumCompression.valueOf(in.readUTF());
    }

    @Override
    public void write(DataOut out) throws IOException {
        this.cryptographySettings.write(out);
        out.writeUTF(this.compression.name());
    }

    @Override
    public EncapsulatedType getType() {
        return EncapsulatedType.HANDSHAKE_INIT;
    }
}
