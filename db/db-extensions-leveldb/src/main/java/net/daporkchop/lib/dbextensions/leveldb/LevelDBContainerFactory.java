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

package net.daporkchop.lib.dbextensions.leveldb;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.db.ContainerFactory;
import net.daporkchop.lib.db.builder.DBMapBuilder;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBContainerBuilder;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBMapBuilder;
import net.daporkchop.lib.dbextensions.leveldb.container.LevelDBMap;

import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@Getter
public class LevelDBContainerFactory implements ContainerFactory<
        LevelDBMapBuilder<Object, Object>> {
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    protected LevelDB levelDb;

    @Override
    public <K, V> DBMap<K, V> loadMap(@NonNull String name, @NonNull Function<LevelDBMapBuilder<Object, Object>, DBMap<K, V>> initializer) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name may not be empty!");
        }
        LevelDBMapBuilder<Object, Object> builder = new LevelDBMapBuilder<>(name, this.levelDb);
        this.initBaseBuilder(builder);
        DBMap<K, V> map = initializer.apply(builder);

        return map;
    }

    protected void initBaseBuilder(@NonNull LevelDBContainerBuilder builder)    {
        //TODO: something with prefixes if we aren't using them
    }
}
