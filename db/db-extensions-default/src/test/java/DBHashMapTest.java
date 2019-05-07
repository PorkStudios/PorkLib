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

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.binary.serialization.impl.ByteArraySerializer;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.dbextensions.defaults.map.DBHashMap;
import net.daporkchop.lib.dbextensions.defaults.map.DataLookup;
import net.daporkchop.lib.dbextensions.defaults.map.IndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.data.IndividualFileLookup;
import net.daporkchop.lib.dbextensions.defaults.map.data.OneTimeWriteDataLookup;
import net.daporkchop.lib.dbextensions.defaults.map.data.SectoredDataLookup;
import net.daporkchop.lib.dbextensions.defaults.map.index.hashtable.BucketingHashTableIndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.index.hashtable.HashTableIndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.index.hashtable.MappedHashTableIndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.index.tree.FasterTreeIndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.key.ByteArrayKeyHasher;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.logging.Logging;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A simple test for all functional implementations of the configurable
 * options for {@link DBHashMap}.
 *
 * @author DaPorkchop_
 */
public class DBHashMapTest implements Logging {
    private static final int TABLE_SIZE_BITS = 16;
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
            , () -> new BucketingHashTableIndexLookup<>(2, 4)
            , () -> new HashTableIndexLookup<>(TABLE_SIZE_BITS, 4)
            , () -> new MappedHashTableIndexLookup<>(TABLE_SIZE_BITS, 4)
            , () -> new FasterTreeIndexLookup<>(4, 2)
    );
    private static final Collection<Supplier<CompressionHelper>> COMPRESSIONS = Arrays.asList(
            null
            , () -> Compression.NONE
    );
    private static final Supplier<Random> RANDOM = () -> new Random(123456789L);
    private static final Collection<BiConsumer<Random, Map<byte[], byte[]>>> POPULATORS = Arrays.asList(
            null
            , (random, map) -> {
                for (int i = 0; i < 513; i++) {
                    byte[] b1 = new byte[TABLE_SIZE_BITS];
                    byte[] b2 = new byte[random.nextInt(1024) + 128];
                    random.nextBytes(b2);
                    do {
                        map.remove(b1);
                        random.nextBytes(b1);
                        map.put(b1, b2);
                    } while (map.keySet().stream()
                            .map(b -> getRelevantHashBits(b, TABLE_SIZE_BITS))
                            .distinct().count() != map.size());
                    //map.put(b1, b2);
                }
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

    protected static long getRelevantHashBits(@NonNull byte[] hash, int usedBits) {
        //get required number of bytes into hash thing
        long bits = 0L;
        for (int i = (usedBits >>> 3) - 1; i >= 0; i--) {
            bits = ((bits << 8L) | (hash[i] & 0xFFL));
        }
        //remove excessive bits at the end
        return bits & ((1L << usedBits) - 1L);
    }

    private File getFile(@NonNull Supplier<Serializer<byte[]>> serializer, @NonNull Supplier<DataLookup> dataLookup, @NonNull Supplier<IndexLookup<byte[]>> indexLookup, @NonNull Supplier<CompressionHelper> compression) {
        return new File(TestConstants.ROOT_DIR, String.format(
                "map-%s-%s-%s-%s",
                serializer.get().getClass(),
                dataLookup.get().getClass(),
                indexLookup.get().getClass(),
                compression.get()
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        TestConstants.init();
        logger.alert("Testing %s", DBHashMap.class);

        logger.info("Deleting output dirs...");
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
                        File out = this.getFile(serializer, dataLookup, indexLookup, compression);
                        for (int i = 0; i < 5; i++) {
                            PorkUtil.rm(out);
                        }
                    });
                });
            });
        });
        logger.info("Running tests...");
        Map<String, Long> requiredTimes = new ConcurrentHashMap<>();
        SERIALIZERS.parallelStream().forEach(serializer -> {
            if (serializer == null) {
                return;
            }
            DATA_LOOKUPS.parallelStream().forEach(dataLookup -> {
                if (dataLookup == null) {
                    return;
                }
                INDEX_LOOKUPS.parallelStream().forEach(indexLookup -> {
                    if (indexLookup == null) {
                        return;
                    }
                    COMPRESSIONS.parallelStream().forEach((IOConsumer<Supplier<CompressionHelper>>) compression -> {
                        if (compression == null) {
                            return;
                        }
                        logger.info(
                                "Testing DBHashMap with (serializer=%s, dataLookup=%s, indexLookup=%s, compression=%s)...",
                                serializer.get().getClass(), dataLookup.get().getClass(), indexLookup.get().getClass(), compression.get()
                        );
                        File out = this.getFile(serializer, dataLookup, indexLookup, compression);

                        Random random = RANDOM.get();
                        Map<byte[], byte[]> data = new HashMap<>();
                        POPULATORS.forEach(populator -> {
                            if (populator == null) {
                                return;
                            }
                            populator.accept(random, data);
                        });
                        Map<byte[], byte[]> oldData = new HashMap<>(data);
                        long startTime = System.currentTimeMillis();
                        {
                            PorkDB db = PorkDB.builder().setRoot(out).build();

                            try {
                                DBHashMap<byte[], byte[]> dbMap = db.<byte[], byte[]>map("map")
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
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
                            PorkDB db = PorkDB.builder().setRoot(out).build();

                            try {
                                DBHashMap<byte[], byte[]> dbMap = db.<byte[], byte[]>map("map")
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
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
                            PorkDB db = PorkDB.builder().setRoot(out).build();

                            try {
                                DBHashMap<byte[], byte[]> dbMap = db.<byte[], byte[]>map("map")
                                        .setKeyHasher(new ByteArrayKeyHasher.ConstantLength(TABLE_SIZE_BITS))
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
                        requiredTimes.put(String.format(
                                "map-%s-%s-%s-%s",
                                serializer.get().getClass(),
                                dataLookup.get().getClass(),
                                indexLookup.get().getClass(),
                                compression.get()
                        ), System.currentTimeMillis() - startTime);
                    });
                });
            });
        });
        logger.trace("");
        logger.trace("");
        logger.trace("Required times:");
        requiredTimes.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .forEachOrdered(entry -> logger.trace(String.format("  %06dms: %s", entry.getValue(), entry.getKey())));
        logger.trace("");
        logger.trace("");
        logger.info("Test for %s finished!", DBHashMap.class);
    }
}
