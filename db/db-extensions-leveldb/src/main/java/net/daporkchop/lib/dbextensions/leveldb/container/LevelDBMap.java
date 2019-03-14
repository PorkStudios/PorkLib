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

package net.daporkchop.lib.dbextensions.leveldb.container;

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.container.map.AbstractDBMap;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.dbextensions.leveldb.LevelDBEngine;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.daporkchop.lib.dbextensions.leveldb.OptionsLevelDB.*;

/**
 * @author DaPorkchop_
 */
public class LevelDBMap<K, V> extends AbstractDBMap<K, V> {
    protected final LevelDBEngine engine;

    protected final Serializer<K> fastKeySerializer;

    @SuppressWarnings("unchecked")
    public LevelDBMap(Settings settings, @NonNull LevelDBEngine engine) {
        super(settings);

        this.engine = engine;

        this.fastKeySerializer = settings.get(MAP_FAST_KEY_SERIALIZER);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public V get(@NonNull K key) {
        return null;
    }

    @Override
    public boolean put(@NonNull K key, @NonNull V value) {
        return false;
    }

    @Override
    public V getAndPut(@NonNull K key, @NonNull V value) {
        return null;
    }

    @Override
    public boolean contains(@NonNull K key) {
        return false;
    }

    @Override
    public boolean remove(@NonNull K key) {
        return false;
    }

    @Override
    public V getAndRemove(@NonNull K key) {
        return null;
    }

    @Override
    public void forEach(@NonNull BiConsumer<K, V> consumer) {
    }

    @Override
    public void forEachEntry(@NonNull Consumer<Entry<K, V>> consumer) {
    }

    @Override
    public PStream<K> keyStream() {
        return null;
    }

    @Override
    public PStream<V> valueStream() {
        return null;
    }

    @Override
    public PStream<Entry<K, V>> entryStream() {
        return null;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }
}
