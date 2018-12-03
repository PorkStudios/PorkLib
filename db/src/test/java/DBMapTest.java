/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

import com.zaxxer.sparsebits.SparseBitSet;
import net.daporkchop.lib.binary.data.Serializer;
import net.daporkchop.lib.binary.data.impl.ByteArraySerializer;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.data.DataLookup;
import net.daporkchop.lib.db.container.map.data.IndividualFileLookup;
import net.daporkchop.lib.db.container.map.data.OneTimeWriteDataLookup;
import net.daporkchop.lib.db.container.map.data.SectoredDataLookup;
import net.daporkchop.lib.db.container.map.index.IndexLookup;
import net.daporkchop.lib.db.container.map.index.hashtable.HashTableIndexLookup;
import net.daporkchop.lib.db.container.map.index.hashtable.MappedHashTableIndexLookup;
import net.daporkchop.lib.db.container.map.key.ByteArrayKeyHasher;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A simple test for all functional implementations of the configurable
 * options for {@link DBMap}.
 *
 * @author DaPorkchop_
 */
public class DBMapTest {
    private static final int TABLE_SIZE_BITS = 16;
    private static final File ROOT_DIR = new File(".", "test_out/map");

    private static final Collection<Supplier<Serializer<byte[]>>> SERIALIZERS = Arrays.asList(
            null
            , () -> ByteArraySerializer.INSTANCE
    );
    private static final Collection<Supplier<DataLookup>> DATA_LOOKUPS = Arrays.asList(
            null
            //TODO: this only works with a constant length serializer! , ConstantLengthLookup::new,
            , IndividualFileLookup::new
            , OneTimeWriteDataLookup::new
            , () -> new SectoredDataLookup(4096)
    );
    private static final Collection<Supplier<IndexLookup<byte[]>>> INDEX_LOOKUPS = Arrays.asList(
            null
            //, () -> new BucketingHashTableIndexLookup<>(2, 4),
            , () -> new HashTableIndexLookup<>(TABLE_SIZE_BITS, 4)
            , () -> new MappedHashTableIndexLookup<>(TABLE_SIZE_BITS, 4)
    );
    private static final Collection<Supplier<CompressionHelper>> COMPRESSIONS = Arrays.asList(
            null
            , () -> Compression.NONE
    );
    private static final Supplier<Random> RANDOM = () -> new Random(123456789L);
    private static final Collection<BiConsumer<Random, Map<byte[], byte[]>>> POPULATORS = Arrays.asList(
            null
            , (random, map) -> {
                for (int i = 0; i < 512; i++) {
                    byte[] b1 = new byte[TABLE_SIZE_BITS];
                    byte[] b2 = new byte[random.nextInt(1024) + 128];
                    do { //TODO: distinct finder thing is borked
                        random.nextBytes(b1);
                    } while (map.keySet().stream()
                            .map(ByteBuffer::wrap)
                            .map(buf -> buf.getLong(TABLE_SIZE_BITS - 8) & ((1 << TABLE_SIZE_BITS) - 1))
                            .distinct().count() != map.size());
                    random.nextBytes(b2);
                    map.put(b1, b2);
                }
                int fullSize = map.size();
                int individualSize = (int) map.keySet().stream()
                        .map(ByteBuffer::wrap)
                        .map(buf -> buf.getLong(TABLE_SIZE_BITS - 8) & ((1 << TABLE_SIZE_BITS) - 1))
                        .distinct().count();
                System.out.printf("Full: %d, indiviudual: %d\n", fullSize, individualSize);
            }
            , (random, map) -> {
                if (false) {
                    byte[] b1 = new byte[TABLE_SIZE_BITS];
                    byte[] b2 = new byte[0xFFFFFF];
                    random.nextBytes(b1);
                    map.put(b1, b2);
                    b1 = new byte[TABLE_SIZE_BITS];
                    b2 = new byte[0xFFFFFF];
                    random.nextBytes(b1);
                    random.nextBytes(b2);
                    map.put(b1, b2);
                }
            }
    );

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        SERIALIZERS.forEach(serializer -> {
            if (serializer == null) {
                return;
            }
            DATA_LOOKUPS.forEach(dataLookup -> {
                if (dataLookup == null) {
                    return;
                }
                INDEX_LOOKUPS.forEach(indexLookup -> {
                    if (indexLookup == null) {
                        return;
                    }
                    COMPRESSIONS.forEach((IOConsumer<Supplier<CompressionHelper>>) compression -> {
                        if (compression == null) {
                            return;
                        }
                        System.out.printf(
                                "Testing DBMap with (serializer=%s, dataLookup=%s, indexLookup=%s, compression=%s)...\n",
                                serializer.get().getClass().getCanonicalName(),
                                dataLookup.get().getClass().getCanonicalName(),
                                indexLookup.get().getClass().getCanonicalName(),
                                compression.get()
                        );
                        PorkUtil.rm(ROOT_DIR);

                        Random random = RANDOM.get();
                        Map<byte[], byte[]> data = new HashMap<>();
                        POPULATORS.forEach(populator -> {
                            if (populator == null) {
                                return;
                            }
                            populator.accept(random, data);
                        });
                        Map<byte[], byte[]> oldData = new HashMap<>(data);
                        {
                            PorkDB db = PorkDB.builder()
                                    .setRoot(ROOT_DIR)
                                    .build();

                            try {
                                DBMap<byte[], byte[]> dbMap = DBMap.<byte[], byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
                                        .build();

                                data.forEach((key, val) -> {
                                    if (dbMap.containsKey(key)) {
                                        throw new IllegalStateException(String.format("Key %s already contained!", Hexadecimal.encode(key)));
                                    } else {
                                        dbMap.put(key, val);
                                    }
                                });
                                //dbMap.putAll(data);
                            } finally {
                                db.close();
                            }
                        }
                        {
                            PorkDB db = PorkDB.builder()
                                    .setRoot(ROOT_DIR)
                                    .build();

                            try {
                                DBMap<byte[], byte[]> dbMap = DBMap.<byte[], byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
                                        .build();

                                data.forEach((key, val) -> {
                                    if (!dbMap.containsKey(key)) {
                                        throw new IllegalStateException(String.format("Missing key: %s", Hexadecimal.encode(key)));
                                    }
                                    byte[] diskVal = dbMap.get(key);
                                    if (!Arrays.equals(val, diskVal)) {
                                        throw new IllegalStateException(String.format("Value for key %s is incorrect!", Hexadecimal.encode(key)));
                                    }
                                });

                                data.keySet().stream()
                                        .filter(s -> random.nextInt(5) == 0)
                                        .collect(Collectors.toList())
                                        .forEach(key -> {
                                            //only remove some elements
                                            data.remove(key);
                                            dbMap.remove(key, false);
                                        });
                            } finally {
                                db.close();
                            }
                        }
                        {
                            PorkDB db = PorkDB.builder()
                                    .setRoot(ROOT_DIR)
                                    .build();

                            try {
                                DBMap<byte[], byte[]> dbMap = DBMap.<byte[], byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
                                        .build();

                                oldData.forEach((key, val) -> {
                                    if (data.containsKey(key)) {
                                        if (!dbMap.containsKey(key)) {
                                            throw new IllegalStateException(String.format("Missing key: %s", Hexadecimal.encode(key)));
                                        }
                                        byte[] diskVal = dbMap.get(key);
                                        if (!Arrays.equals(val, diskVal)) {
                                            throw new IllegalStateException(String.format("Value for key %s is incorrect!", Hexadecimal.encode(key)));
                                        }
                                    } else {
                                        if (dbMap.containsKey(key)) {
                                            throw new IllegalStateException(String.format("Key %s is present even though it was removed!", Hexadecimal.encode(key)));
                                        }
                                    }
                                });
                            } finally {
                                db.close();
                            }
                        }
                    });
                });
            });
        });
    }
}
