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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Base class for a representation of a Minecraft version.
 *
 * @author DaPorkchop_
 * @see net.daporkchop.lib.minecraft.version.java.JavaVersion
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class MinecraftVersion implements Comparable<MinecraftVersion> {
    @NonNull
    protected final MinecraftEdition edition;
    @NonNull
    protected final String name;
    protected final long releaseTime;

    @Override
    public int compareTo(MinecraftVersion o) {
        if (this.edition != o.edition)  {
            return this.edition.ordinal() - o.edition.ordinal();
        } else if (this.releaseTime > 0L && o.releaseTime > 0L) {
            return Long.compareUnsigned(this.releaseTime, o.releaseTime);
        } else {
            return this.name.compareTo(o.name);
        }
    }

    @Override
    public int hashCode() {
        return this.edition.hashCode() ^ this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MinecraftVersion) {
            MinecraftVersion version = (MinecraftVersion) obj;
            return this.edition == version.edition && this.name.equals(version.name);
        } else {
            return false;
        }
    }

    @Override
    public abstract String toString();
}
