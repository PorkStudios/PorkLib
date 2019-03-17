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

package net.daporkchop.lib.dbextensions.leveldb.builder;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.db.builder.DBMapBuilder;
import net.daporkchop.lib.dbextensions.leveldb.LevelDB;
import net.daporkchop.lib.dbextensions.leveldb.container.LevelDBMap;

import java.io.File;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class LevelDBMapBuilder<K, V> extends DBMapBuilder<K, V, LevelDBMapBuilder> implements LevelDBContainerBuilder<LevelDBMapBuilder<K, V>> {
    protected final LevelDB levelDb;
    protected final File path;

    protected byte[] containerPrefix;
    protected Serializer<K> fastKeySerializer;
    protected boolean trackSize = true;

    public LevelDBMapBuilder(String name, @NonNull LevelDB levelDb, @NonNull File path) {
        super(name);

        this.levelDb = levelDb;
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    public <NEW_K> LevelDBMapBuilder<NEW_K, V> setFastKeySerializer(@NonNull Serializer<NEW_K> fastKeySerializer) {
        this.fastKeySerializer = (Serializer<K>) fastKeySerializer;
        if (this.keySerializer == null) {
            this.keySerializer = this.fastKeySerializer;
        }
        return (LevelDBMapBuilder<NEW_K, V>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <NEW_K> LevelDBMapBuilder<NEW_K, V> setKeySerializer(@NonNull Serializer<NEW_K> keySerializer) {
        this.keySerializer = (Serializer<K>) keySerializer;
        return (LevelDBMapBuilder<NEW_K, V>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <NEW_V> LevelDBMapBuilder<K, NEW_V> setValueSerializer(@NonNull Serializer<NEW_V> valueSerializer) {
        this.valueSerializer = (Serializer<V>) valueSerializer;
        return (LevelDBMapBuilder<K, NEW_V>) this;
    }

    @Override
    public LevelDBMap<K, V> build() {
        return new LevelDBMap<>(this);
    }
}
