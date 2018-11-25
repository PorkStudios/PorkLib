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

package net.daporkchop.lib.network.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.packet.UserProtocol;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Version implements Data, Logging {
    @NonNull
    private String name;
    private int version;

    public Version(@NonNull UserProtocol protocol)  {
        this(protocol.getName(), protocol.getVersion());
    }

    @Override
    public void read(@NonNull DataIn in) throws IOException {
        this.name = in.readUTF();
        this.version = in.readVarInt(true);
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeUTF(this.name);
        out.writeVarInt(this.version, true);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserProtocol)    {
            UserProtocol protocol = (UserProtocol) obj;
            return protocol.getName().equals(this.name) && protocol.getVersion() == this.version;
        } else if (obj instanceof Version)  {
            Version version = (Version) obj;
            return version.name.equals(this.name) && version.version == this.version;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.format("${0} v${1}", this.name, this.version);
    }
}
