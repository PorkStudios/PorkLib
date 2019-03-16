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

package net.daporkchop.lib.db.builder;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class DBMapBuilder<K, V, B extends DBMapBuilder> extends ContainerBuilder<DBMap<K, V>, B> {
    protected boolean keysReadable = true;
    protected Serializer<K> keySerializer;
    protected Serializer<V> valueSerializer;
    @NonNull
    protected CompressionHelper valueCompression = Compression.NONE;

    public DBMapBuilder(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    public <NEW_K> DBMapBuilder<NEW_K, V, B> setKeySerializer(@NonNull Serializer<NEW_K> keySerializer) {
        this.keySerializer = (Serializer<K>) keySerializer;
        return (DBMapBuilder<NEW_K, V, B>) this;
    }

    @SuppressWarnings("unchecked")
    public <NEW_V> DBMapBuilder<K, NEW_V, B> setValueSerializer(@NonNull Serializer<NEW_V> valueSerializer) {
        this.valueSerializer = (Serializer<V>) valueSerializer;
        return (DBMapBuilder<K, NEW_V, B>) this;
    }

    @Override
    public DBMapBuilder<K, V, B> validate() {
        if (this.keySerializer == null) {
            throw new NullPointerException("keySerializer");
        } else if (this.valueSerializer == null)    {
            throw new NullPointerException("valueSerializer");
        }
        return (DBMapBuilder<K, V, B>) super.validate();
    }
}
