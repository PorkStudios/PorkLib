/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.version;

import lombok.NonNull;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;

import static java.lang.Math.*;

/**
 * Representation of a Minecraft version.
 *
 * @author DaPorkchop_
 */
//TODO: make this class identity-comparable, like Identifier
public final class MinecraftVersion implements Comparable<MinecraftVersion> {
    protected final MinecraftEdition edition;
    protected final String name;
    protected final boolean snapshot;
    protected final int protocolVersion;
    protected final int dataVersion;

    public MinecraftVersion(@NonNull MinecraftEdition edition, String name, boolean snapshot, int protocolVersion, int dataVersion) {
        this.edition = edition;
        this.name = PorkUtil.fallbackIfNull(name, "Unknown");
        this.snapshot = snapshot;
        this.protocolVersion = max(protocolVersion, 0);
        this.dataVersion = max(dataVersion, 0);
    }

    @Override
    public int hashCode() {
        return (this.edition.ordinal() * 31 + this.name.hashCode()) * 31 + this.protocolVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MinecraftVersion) {
            MinecraftVersion other = (MinecraftVersion) obj;
            return this.edition == other.edition && this.name.equals(other.name);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull MinecraftVersion o) {
        if (this.edition != o.edition) {
            return Integer.compare(this.edition.ordinal(), o.edition.ordinal());
        } else {
            return this.name.compareTo(o.name);
        }
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            builder.append(this.edition).append(" Edition");
            if (this.name == "Unknown") {
                builder.append(" (unknown version)");
            } else {
                builder.append(' ').append('v').append(this.name);
            }
            if (this.snapshot) {
                builder.append(" (snapshot)");
            }
            if (this.protocolVersion > 0 || this.dataVersion > 0) {
                builder.append(' ').append('(');
                if (this.protocolVersion > 0)   {
                    builder.append("protocol").append(this.protocolVersion);
                }
                if (this.dataVersion > 0) {
                    builder.append(this.protocolVersion > 0 ? ", data version " : "data version ").append(this.dataVersion);
                }
                builder.append(')');
            }
            return builder.toString();
        }
    }
}
