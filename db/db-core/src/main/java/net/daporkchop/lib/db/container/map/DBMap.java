/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.map;

import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.common.setting.Option;
import net.daporkchop.lib.common.setting.OptionGroup;
import net.daporkchop.lib.db.container.Container;

import java.io.IOException;

/**
 * A map stored on the file system.
 *
 * @param <K> the type to be used as a key
 * @param <V> the type to be used as a value
 * @author DaPorkchop_
 */
public interface DBMap<K, V> extends Container, PMap<K, V> {
    Option<Serializer> KEY_SERIALIZER = Option.optional("KEY_SERIALIZER");
    Option<Serializer> VALUE_SERIALIZER = Option.required("VALUE_SERIALIZER");

    OptionGroup DB_MAP_OPTIONS = OptionGroup.of(
            Container.BASE_CONTAINER_OPTIONS,
            KEY_SERIALIZER,
            VALUE_SERIALIZER
    );

    @Override
    void close() throws IOException;

    /**
     * Checks if this map is closed.
     *
     * @return whether or not this map has been closed
     */
    default boolean isClosed() {
        return this.size() == -1L;
    }
}
