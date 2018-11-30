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
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import org.junit.Test;

import java.io.File;
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
    private static final Collection<Supplier<IndexLookup<String>>> INDEX_LOOKUPS = Arrays.asList(
            null
            //, () -> new BucketingHashTableIndexLookup<>(2, 4),
            , () -> new HashTableIndexLookup<>(16, 4)
            , () -> new MappedHashTableIndexLookup<>(16, 4)
    );
    private static final Collection<Supplier<CompressionHelper>> COMPRESSIONS = Arrays.asList(
            null
            , () -> Compression.NONE
    );
    private static final Supplier<Random> RANDOM = () -> new Random(123456789L);
    private static final Collection<BiConsumer<Random, Map<String, byte[]>>> POPULATORS = Arrays.asList(
            null
            , (random, map) -> {
                for (int i = 0; i < 513; i++) {
                    byte[] b1 = new byte[16];
                    byte[] b2 = new byte[1024];
                    random.nextBytes(b1);
                    random.nextBytes(b2);
                    map.put(Base58.encodeBase58(b1), b2);
                }
            }
            , (random, map) -> {
                if (false) {
                    byte[] b1 = new byte[16];
                    byte[] b2 = new byte[0xFFFFFF];
                    random.nextBytes(b1);
                    map.put(Base58.encodeBase58(b1), b2);
                    b1 = new byte[16];
                    b2 = new byte[0xFFFFFF];
                    random.nextBytes(b1);
                    random.nextBytes(b2);
                    map.put(Base58.encodeBase58(b1), b2);
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
                        Map<String, byte[]> data = new HashMap<>();
                        POPULATORS.forEach(populator -> {
                            if (populator == null) {
                                return;
                            }
                            populator.accept(random, data);
                        });
                        Map<String, byte[]> oldData = new HashMap<>(data);
                        {
                            PorkDB db = PorkDB.builder()
                                    .setRoot(ROOT_DIR)
                                    .build();

                            try {
                                DBMap<String, byte[]> dbMap = DBMap.<String, byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .build();

                                dbMap.putAll(data);
                            } finally {
                                db.close();
                            }
                        }
                        {
                            PorkDB db = PorkDB.builder()
                                    .setRoot(ROOT_DIR)
                                    .build();

                            try {
                                DBMap<String, byte[]> dbMap = DBMap.<String, byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .build();

                                data.forEach((key, val) -> {
                                    if (!dbMap.containsKey(key)) {
                                        throw new IllegalStateException(String.format("Missing key: %s", key));
                                    }
                                    byte[] diskVal = dbMap.get(key);
                                    if (!Arrays.equals(val, diskVal)) {
                                        throw new IllegalStateException(String.format("Value for key %s is incorrect!", key));
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
                                DBMap<String, byte[]> dbMap = DBMap.<String, byte[]>builder(db, "map")
                                        .setValueSerializer(serializer.get())
                                        .setDataLookup(dataLookup.get())
                                        .setIndexLookup(indexLookup.get())
                                        .setCompression(compression.get())
                                        .build();

                                oldData.forEach((key, val) -> {
                                    if (data.containsKey(key)) {
                                        if (!dbMap.containsKey(key)) {
                                            throw new IllegalStateException(String.format("Missing key: %s", key));
                                        }
                                        byte[] diskVal = dbMap.get(key);
                                        if (!Arrays.equals(val, diskVal)) {
                                            throw new IllegalStateException(String.format("Value for key %s is incorrect!", key));
                                        }
                                    } else {
                                        if (dbMap.containsKey(key)) {
                                            throw new IllegalStateException(String.format("Key %s is present even though it was removed!", key));
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
        PorkUtil.rm(ROOT_DIR);
    }
}
