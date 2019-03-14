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

package db;

import net.daporkchop.lib.binary.serialization.impl.BasicSerializer;
import net.daporkchop.lib.binary.serialization.impl.ByteArraySerializer;
import net.daporkchop.lib.common.test.TestRandomData;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.engine.DBEngine;
import net.daporkchop.lib.dbextensions.leveldb.LevelDBEngine;
import net.daporkchop.lib.dbextensions.leveldb.OptionsLevelDB;
import net.daporkchop.lib.encoding.basen.Base58;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
public class DBMapTest implements TestConstants {
    static {
        TestConstants.init();

        logger.trace("Nuking test output...");
        Arrays.stream(ROOT_DIR.listFiles()).parallel().forEach(PorkUtil::rm);
        logger.trace("Test output nuked.");
    }

    public Collection<DBEngine> engines = Stream.of(
            null
            , LevelDBEngine.builder()
                    .set(OptionsLevelDB.PATH, new File(ROOT_DIR, "leveldb"))
                    .build()
    ).filter(Objects::nonNull).collect(Collectors.toList());

    @Test
    public void test() {
        Map<String, byte[]> data = new HashMap<>();
        for (int i = 511; i >= 0; i--) {
            data.put(Base58.encodeBase58(TestRandomData.getRandomBytes(4, 16)), TestRandomData.getRandomBytes(512, 8192));
        }
        for (DBEngine engine : this.engines) {
            try (PorkDB db = new PorkDB(engine)) {
                DBMap<String, byte[]> map = db.getContainer(ContainerType.MAP, "map1", settings -> settings
                        .set(DBMap.KEY_SERIALIZER, BasicSerializer.getInstance())
                        .set(DBMap.VALUE_SERIALIZER, ByteArraySerializer.INSTANCE));
                data.forEach(map::put);
                map.close();
                map = db.getContainer(ContainerType.MAP, "map1", settings -> settings
                        .set(DBMap.KEY_SERIALIZER, BasicSerializer.getInstance())
                        .set(DBMap.VALUE_SERIALIZER, ByteArraySerializer.INSTANCE));
                AtomicInteger i = new AtomicInteger(0);
                map.forEach((key, value) -> {
                    i.incrementAndGet();
                    byte[] real = data.get(key);
                    if (!Arrays.equals(real, value))   {
                        throw new IllegalStateException();
                    }
                });
                if (i.get() != data.size()) {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
