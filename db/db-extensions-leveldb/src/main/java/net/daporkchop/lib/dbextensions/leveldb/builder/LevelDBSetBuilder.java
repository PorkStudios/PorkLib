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
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.db.DBSet;
import net.daporkchop.lib.db.builder.DBSetBuilder;
import net.daporkchop.lib.db.util.KeyHasher;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true, chain = true)
public class LevelDBSetBuilder<V> extends LevelDBBuilder<LevelDBSetBuilder<V>, DBSet<V>> implements DBSetBuilder<LevelDBSetBuilder<V>, V> {
    protected KeyHasher<V> hasher;
    protected Serializer<V> valueSerializer;

    @SuppressWarnings("unchecked")
    public <NEW_V> LevelDBSetBuilder<NEW_V> hasher(KeyHasher<NEW_V> hasher) {
        ((LevelDBSetBuilder<NEW_V>) this).hasher = hasher;
        return (LevelDBSetBuilder<NEW_V>) this;
    }

    @SuppressWarnings("unchecked")
    public <NEW_V> LevelDBSetBuilder<NEW_V> valueSerializer(Serializer<NEW_V> valueSerializer) {
        ((LevelDBSetBuilder<NEW_V>) this).valueSerializer = valueSerializer;
        return (LevelDBSetBuilder<NEW_V>) this;
    }

    @Override
    public DBSet<V> build() {
        return null;
    }
}
