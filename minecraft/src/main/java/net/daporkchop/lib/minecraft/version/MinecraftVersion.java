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

/**
 * Representation of a Minecraft version.
 *
 * @author DaPorkchop_
 */
public interface MinecraftVersion extends Comparable<MinecraftVersion> {
    MinecraftVersion UNKNOWN_JAVA = new DefaultVersion(MinecraftEdition.JAVA, null, 0, 0, false);
    MinecraftVersion UNKNOWN_BEDROCK = new DefaultVersion(MinecraftEdition.BEDROCK, null, 0, 0, false);
    MinecraftVersion UNKNOWN = new DefaultVersion(MinecraftEdition.UNKNOWN, null, 0, 0, false);

    static MinecraftVersion fromName(@NonNull MinecraftEdition edition, @NonNull String name) {
        MinecraftVersion version = edition == MinecraftEdition.JAVA ? DefaultVersion.FROM_NAME.get(name) : null;
        return version != null ? version : new DefaultVersion(edition, name, 0, 0, false);
    }

    static MinecraftVersion fromDataVersion(int dataVersion) {
        MinecraftVersion version = DefaultVersion.FROM_DATA_VERSION.get(dataVersion);
        return version != null ? version : new DefaultVersion(MinecraftEdition.JAVA, null, dataVersion, 0, false);
    }

    static MinecraftVersion fromNameAndDataVersion(@NonNull String name, int dataVersion, boolean snapshot) {
        MinecraftVersion version = DefaultVersion.FROM_DATA_VERSION.get(dataVersion);
        return version != null ? version : new DefaultVersion(MinecraftEdition.JAVA, name, dataVersion, 0, snapshot);
    }

    /**
     * @return the {@link MinecraftEdition} that this version belongs to
     */
    MinecraftEdition edition();

    /**
     * @return this version's name (e.g. {@code "1.12.2"})
     */
    String name();

    /**
     * @return this version's data version, or {@code 0} if it is not known
     */
    int data();

    /**
     * @return this version's network protocol number, or {@code 0} if it is not known
     */
    int protocol();

    /**
     * @return whether or not this version is a snapshot/beta release
     */
    boolean snapshot();
}
